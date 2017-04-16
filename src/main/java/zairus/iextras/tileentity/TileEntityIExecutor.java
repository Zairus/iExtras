package zairus.iextras.tileentity;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.iextras.entity.IEFakePlayer;

public class TileEntityIExecutor extends TileEntityIEBase implements ISidedInventory
{
	private IEFakePlayer fakePlayer = null;
	private ItemStack[] chestContents = new ItemStack[10];
	private int workingTicks = 0;
	
	protected String defaultName = "iexecutor";
	
	public TileEntityIExecutor()
	{
		;
	}
	
	private void initializeFakePlayer()
	{
		if (this.worldObj != null && !this.worldObj.isRemote && this.fakePlayer == null)
		{
			this.fakePlayer = new IEFakePlayer((WorldServer)this.worldObj, new GameProfile(UUID.fromString("858883b3-cc29-44f9-ada3-01075eee02b8"), "Iskallian_Executor"), this);
		}
	}
	
	@Override
	public void update()
	{
		if (this.worldObj.isRemote)
			return;
		
		++workingTicks;
		
		if (workingTicks % 10 == 0)
		{
			rightClick();
		}
	}
	
	private void rightClick()
	{
		initializeFakePlayer();
		
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
				this.markDirty();
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
}
