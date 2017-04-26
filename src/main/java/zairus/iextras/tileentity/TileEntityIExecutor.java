package zairus.iextras.tileentity;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import zairus.iextras.entity.IEFakePlayer;

public class TileEntityIExecutor extends TileEntityIEBase implements ISidedInventory, IItemHandlerModifiable, IEnergyStorage
{
	private IEFakePlayer fakePlayer = null;
	private ItemStack[] chestContents = new ItemStack[10];
	private int workingTicks = 0;
	
	private int energy;
	private int capacity = 10000;
    private int maxReceive = 160;
	
	protected String defaultName = "iexecutor";
	
	public TileEntityIExecutor()
	{
		;
	}
	
	private void initializeFakePlayer()
	{
		if (this.worldObj != null && this.fakePlayer == null)
		{
			this.fakePlayer = new IEFakePlayer((WorldServer)this.worldObj, new GameProfile(UUID.fromString("858883b3-cc29-44f9-ada3-01075eee02b8"), "Iskallian_Executor"), this);
			this.fakePlayer.connection = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList().get(0).connection; /*new NetHandlerPlayServer(
					FMLCommonHandler.instance().getMinecraftServerInstance(), 
					FMLCommonHandler.instance().getClientToServerNetworkManager(), 
					this.fakePlayer);*/
			
			net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerLoggedInEvent(this.fakePlayer));
		}
	}
	
	@Override
	public void update()
	{
		++workingTicks;
		
		if (workingTicks % 10 == 0)
		{
			if (!this.worldObj.isRemote)
			{
				initializeFakePlayer();
				rightClick();
			}
			
			IBlockState state = this.worldObj.getBlockState(getPos());
			this.worldObj.notifyBlockUpdate(getPos(), state, state, 0);
		}
	}
	
	private void rightClick()
	{
		if (this.fakePlayer != null)
		{
			// DUNSWE
			int meta = this.getBlockMetadata();
			EnumFacing ieFacing = EnumFacing.getFront(meta);
			BlockPos pos = this.getPos();
			pos = pos.offset(ieFacing);
			
			// 0 south
			// 90 west
			// 180 north
			// 270 east
			
			//pitch
			// 0 front
			// 90 down
			// 180 up
			
			this.fakePlayer.setPositionAndRotation(
					pos.getX(), 
					pos.getY() - 1, 
					pos.getZ(), 
					0.0F,
					0.0F);
			
			ItemStack actionStack = this.getStackInSlot(9);
			
			if (!rightClickBlock(pos, actionStack))
			{
				rightClickEntity(pos, actionStack);
			}
		}
	}
	
	private boolean rightClickEntity(BlockPos pos, ItemStack actionStack)
	{
		boolean success = false;
		
		if (actionStack != null)
		{
			List<Entity> entities = this.worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
			
			if (entities.size() > 0)
			{
				Entity target = entities.get(0);
				
				this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, actionStack);
				this.fakePlayer.setActiveHand(EnumHand.MAIN_HAND);
				
				this.fakePlayer.interact(target, actionStack, EnumHand.MAIN_HAND);
				
				ItemStack heldStack = this.fakePlayer.getHeldItemMainhand();
				
				if (actionStack.stackSize <= 0)
					this.setInventorySlotContents(9, null);
				
				if (heldStack != null && heldStack != actionStack)
				{
					addStackToAvailableSlot(heldStack);
					success = true;
				}
			}
		}
		
		return success;
	}
	
	private boolean rightClickBlock(BlockPos pos, ItemStack actionStack)
	{
		boolean success = false;
		
		if (actionStack != null)
		{
			this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, actionStack);
			this.fakePlayer.setActiveHand(EnumHand.MAIN_HAND);
			ActionResult<ItemStack> result = actionStack.getItem().onItemRightClick(actionStack, worldObj, fakePlayer, EnumHand.MAIN_HAND);
			
			if (actionStack.stackSize <= 0 || actionStack.getItem() == Items.WATER_BUCKET)
				this.setInventorySlotContents(9, null);
			
			if (result != null && result.getResult() != actionStack)
			{
				addStackToAvailableSlot(result.getResult());
				
				success = true;
			}
		}
		
		return success;
	}
	
	public void addStackToAvailableSlot(ItemStack stack)
	{
		boolean added = false;
		
		for (int i = 0; i < 9; ++i)
		{
			if (this.getStackInSlot(i) == null)
			{
				this.setInventorySlotContents(i, stack);
				IBlockState state = this.worldObj.getBlockState(getPos());
				this.worldObj.notifyBlockUpdate(getPos(), state, state, 0);
				added = true;
				break;
			}
		}
		
		if (!added)
		{
			EntityItem dropStack = new EntityItem(this.worldObj, this.getPos().getX(), this.getPos().getY() + 1, this.getPos().getZ(), stack);
			this.worldObj.spawnEntityInWorld(dropStack);
		}
	}
	
	@Override
	public ItemStack[] getChestContents()
	{
		return this.chestContents;
	}
	
	@Override
	public void setChestContents(ItemStack[] contents)
	{
		this.chestContents = contents;
	}
	
	@Override
	public int getSlotXOffset()
	{
		return 80;
	}
	
	@Override
	public int getSlotYOffset()
	{
		return 17;
	}
	
	@Override
	public Slot getSlot(IInventory inv, int index, int x, int y)
	{
		return new Slot(inv, index, x, y);
	}
	
	@Override
	public SoundEvent getOpenSound()
	{
		return null;
	}
	
	@Override
	public SoundEvent getCloseSound()
	{
		return null;
	}
	
	@Override
	public SoundEvent getItemPlaceSound()
	{
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTextures()
	{
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
	{
		if (capability != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) this;
		
		return super.getCapability(capability, facing);
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing direction)
	{
		return index == 9;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return index >= 0 && index < 9;
	}
	
	@Override
	public int getSlots()
	{
		return this.getSizeInventory();
	}
	
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (stack == null)
			return null;
		
		if (slot < 9)
			return stack;
		
		if (!this.isItemValidForSlot(slot, stack))
			return stack;
		
		ItemStack curStack = this.getStackInSlot(slot);
		
		int m;
		
		if (curStack == null)
		{
			m = Math.min(stack.getMaxStackSize(), this.getInventoryStackLimit());
			
			if (m < stack.stackSize)
			{
				stack = stack.copy();
				if (!simulate)
				{
					this.setInventorySlotContents(slot, stack.splitStack(m));
					this.markDirty();
					return stack;
				}
				else
				{
					stack.stackSize -= m;
					return stack;
				}
			}
			else
			{
				if (!simulate)
				{
					this.setInventorySlotContents(slot, stack);
					this.markDirty();
				}
				
				return null;
			}
		}
		else
		{
			if (!ItemHandlerHelper.canItemStacksStack(stack, curStack))
				return stack;
			
			m = stack.getMaxStackSize() - curStack.stackSize;
			
			if (stack.stackSize <= m)
			{
				if (!simulate)
				{
					ItemStack copy = stack.copy();
					copy.stackSize += curStack.stackSize;
					this.setInventorySlotContents(slot, copy);
					this.markDirty();
				}
				
				return null;
			}
			else
			{
				stack = stack.copy();
				if (!simulate)
				{
					ItemStack copy = stack.splitStack(m);
					copy.stackSize += curStack.stackSize;
					this.setInventorySlotContents(slot, copy);
					this.markDirty();
					return stack;
				}
				else
				{
					stack.stackSize -= m;
					return stack;
				}
			}
		}
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (amount == 0)
			return null;
		
		if (slot == 9)
			return null;
		
		ItemStack curStack = this.getStackInSlot(slot);
		
		if (curStack == null)
			return null;
		
		if (simulate)
		{
			if (curStack.stackSize < amount)
			{
				return curStack.copy();
			}
			else
			{
				ItemStack copy = curStack.copy();
				copy.stackSize = amount;
				return copy;
			}
		}
		else
		{
			int m = Math.min(curStack.stackSize, amount);
			ItemStack decrStackSize = this.decrStackSize(slot, m);
			this.markDirty();
			return decrStackSize;
		}
	}
	
	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		super.setInventorySlotContents(slot, stack);
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		if (!canReceive())
            return 0;
		
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        
        if (!simulate)
            energy += energyReceived;
        
        return energyReceived;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return 0;
	}
	
	@Override
	public int getEnergyStored()
	{
		return this.energy;
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return this.capacity;
	}
	
	@Override
	public boolean canExtract()
	{
		return false;
	}
	
	@Override
	public boolean canReceive()
	{
		return this.energy < this.capacity;
	}
}
