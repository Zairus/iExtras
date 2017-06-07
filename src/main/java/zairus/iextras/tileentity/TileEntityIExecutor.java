package zairus.iextras.tileentity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
import zairus.iextras.IEConfig;
import zairus.iextras.entity.IEFakePlayer;

public class TileEntityIExecutor extends TileEntityIEBase implements ISidedInventory, IItemHandlerModifiable, IEnergyStorage
{
	private IEFakePlayer fakePlayer = null;
	private ItemStack[] chestContents = new ItemStack[10];
	private int workingTicks = 0;
	
	private int energy = 0;
	private int capacity = IEConfig.IEXECUTOR_ENERGY_CAPACITY;
    private int maxReceive = IEConfig.IEXECUTOR_ENERGY_RECEIVE;
    private int consumption = IEConfig.IEXECUTOR_ENERGY_CONSUMPTION;
    
    private int inventoryMode = 0;
    private int useAction = 0;
    
	protected String defaultName = "iexecutor";
	
	public TileEntityIExecutor()
	{
		;
	}
	
	public void configureWork(int m, int a)
	{
		this.inventoryMode = m;
		this.useAction = a;
	}
	
	public int getInventoryMode()
	{
		return this.inventoryMode;
	}
	
	public int getUseAction()
	{
		return this.useAction;
	}
	
	private void initializeFakePlayer()
	{
		if (this.worldObj != null && this.fakePlayer == null)
		{
			List<EntityPlayerMP> playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList();
			
			if (playerList.size() > 0 && playerList.get(0).connection != null)
			{
				this.fakePlayer = new IEFakePlayer((WorldServer)this.worldObj, new GameProfile(UUID.fromString("858883b3-cc29-44f9-ada3-01075eee02b8"), "Iskallian_Executor"), this);
				this.fakePlayer.connection = playerList.get(0).connection; 
				
				net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new PlayerLoggedInEvent(this.fakePlayer));
			}
		}
	}
	
	@Override
	public void update()
	{
		++workingTicks;
		
		if (workingTicks % 10 == 0)
		{
			if (!this.worldObj.isRemote && this.energy >= consumption)
			{
				boolean worked = false;
				
				initializeFakePlayer();
				
				switch(this.useAction)
				{
				case 0:
					if (leftClick())
						worked = true;
					break;
				default:
					if (rightClick())
						worked = true;
					break;
				}
				
				if (pickupItems())
					worked = true;
				
				if (worked)
					this.energy -= consumption;
			}
			
			IBlockState state = this.worldObj.getBlockState(getPos());
			this.worldObj.notifyBlockUpdate(getPos(), state, state, 0);
		}
	}
	
	private boolean pickupItems()
	{
		boolean worked = false;
		
		if (this.fakePlayer != null)
		{
			BlockPos itemPos = this.getActionPos();
			Entity toPick = this.getTargetEntity(itemPos);
			
			if (toPick != null && toPick instanceof EntityItem)
			{
				EntityItem item = (EntityItem)toPick;
				ItemStack stack = item.getEntityItem().copy();
				
				if (stack != null)
				{
					this.addStackToAvailableSlot(stack);
					
					this.worldObj.playSound(
							this.fakePlayer
							, itemPos
							, SoundEvents.ENTITY_ITEM_PICKUP
							, SoundCategory.PLAYERS
							, 1.0F
							, 1.0F / (this.worldObj.rand.nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
					
					item.setDead();
					
					worked = true;
				}
			}
		}
		
		return worked;
	}
	
	private boolean leftClick()
	{
		boolean worked = false;
		
		if (this.fakePlayer != null)
		{
			BlockPos pos = this.getActionPos();
			ItemStack actionStack = this.getStackInSlot(9);
			
			if (!(worked = leftClickEntity(pos, actionStack)))
				worked = leftClickBlock();
		}
		
		return worked;
	}
	
	private boolean leftClickEntity(BlockPos pos, ItemStack actionStack)
	{
		boolean success = false;
		
		if (actionStack != null)
		{
			Entity target = this.getTargetEntity(pos);
			
			if (target != null)
			{
				if (this.fakePlayer.getHeldItemMainhand() == null)
				{
					this.equipStack(actionStack);
				}
				
				this.fakePlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).setBaseValue(1000.0D);
				this.fakePlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
				
				this.fakePlayer.attackTargetEntityWithCurrentItem(target);
				
				success = true;
			}
		}
		
		return success;
	}
	
	private boolean leftClickBlock()
	{
		return false;
	}
	
	private void equipStack(ItemStack stack)
	{
		this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, stack);
		this.fakePlayer.setActiveHand(EnumHand.MAIN_HAND);
	}
	
	private Entity getTargetEntity(BlockPos pos)
	{
		Entity target = null;
		
		List<Entity> entities = this.worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos));
		
		if (entities.size() > 0)
			target = entities.get(0);
		
		return target;
	}
	
	private BlockPos getActionPos()
	{
		int meta = this.getBlockMetadata();
		EnumFacing ieFacing = EnumFacing.getFront(meta);
		BlockPos pos = this.getPos();
		pos = pos.offset(ieFacing);
		
		this.fakePlayer.setPositionAndRotation(
				pos.getX(), 
				pos.getY() - 1, 
				pos.getZ(), 
				(meta == 3) ? 0.0F : (meta == 4) ? 90.0F : (meta == 2) ? 180.0F : 270.0F,
				0.0F);
		
		return pos;
	}
	
	private boolean rightClick()
	{
		boolean worked = false;
		
		if (this.fakePlayer != null)
		{
			BlockPos pos = this.getActionPos();
			
			ItemStack actionStack = this.getStackInSlot(9);
			
			if (!(worked = rightClickBlock(pos, actionStack)))
				worked = rightClickEntity(pos, actionStack);
		}
		
		return worked;
	}
	
	private boolean rightClickEntity(BlockPos pos, ItemStack actionStack)
	{
		boolean success = false;
		
		if (actionStack != null)
		{
			Entity target = this.getTargetEntity(pos);
			
			if (target != null)
			{
				this.equipStack(actionStack);
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
			
			if (actionStack.stackSize <= 0)
				this.setInventorySlotContents(9, null);
			
			if (result != null && result.getResult() != actionStack)
			{
				if (actionStack.getItem() == Items.WATER_BUCKET)
					this.setInventorySlotContents(9, null);
				
				addStackToAvailableSlot(result.getResult());
				
				success = true;
			}
		}
		
		return success;
	}
	
	public boolean addStackToAvailableSlot(ItemStack stack)
	{
		boolean added = false;
		
		ItemStack resultStack = null;
		
		for (int i = 0; i < 9; ++i)
		{
			resultStack = this.insertStackIntoSlot(i, stack, false);
			
			if (resultStack == null)
			{
				IBlockState state = this.worldObj.getBlockState(getPos());
				this.worldObj.notifyBlockUpdate(getPos(), state, state, 0);
				added = true;
				break;
			}
		}
		
		if (!added && resultStack != null)
		{
			EntityItem dropStack = new EntityItem(this.worldObj, this.getPos().getX(), this.getPos().getY() + 1, this.getPos().getZ(), resultStack);
			this.worldObj.spawnEntityInWorld(dropStack);
		}
		
		return added;
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
		if (capability != null)
		{
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return (T) this;
			
			if (capability.getName() == "net.minecraftforge.energy.IEnergyStorage")
				return (T) this;
		}
		
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
	{
		if (capability.getName() == "net.minecraftforge.energy.IEnergyStorage")
			return true;
		
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing direction)
	{
		return ((this.inventoryMode == 0 || this.inventoryMode == 2) && index == 9);
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		return ((this.inventoryMode == 1 || this.inventoryMode == 2) && (index >= 0 && index < 9));
	}
	
	@Override
	public int getSlots()
	{
		return this.getSizeInventory();
	}
	
	private ItemStack insertStackIntoSlot(int slot, ItemStack stack, boolean simulate)
	{
		if (stack == null)
			return null;
		
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
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (!this.canInsertItem(slot, stack, null))
			return stack;
		
		if (slot < 9)
			return stack;
		
		return this.insertStackIntoSlot(slot, stack, simulate);
	}
	
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (!this.canExtractItem(slot, null, null))
			return null;
		
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
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		NBTTagCompound c = super.writeToNBT(compound);
		
		c.setInteger("executor_energy", this.energy);
		c.setInteger("executor_inventoryMode", this.inventoryMode);
		c.setInteger("executor_useAction", this.useAction);
		
		return c;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		this.energy = compound.getInteger("executor_energy");
		this.inventoryMode = compound.getInteger("executor_inventoryMode");
		this.useAction = compound.getInteger("executor_useAction");
	}
	
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.getPos(), 1, this.getUpdateTag());
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.getNbtCompound());
	}
}
