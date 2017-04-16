package zairus.iextras.tileentity;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.iextras.IExtras;

public class TileEntityIExecutor extends TileEntityIEBase implements ISidedInventory
{
	private FakePlayer fakePlayer = null;
	
	private ItemStack[] chestContents = new ItemStack[10];
	protected String defaultName = "iexecutor";
	
	public TileEntityIExecutor()
	{
		;
	}
	
	private void initializeFakePlayer()
	{
		if (this.worldObj != null && !this.worldObj.isRemote && this.fakePlayer == null)
		{
			this.fakePlayer = new FakePlayer((WorldServer)this.worldObj, new GameProfile(UUID.fromString("858883b3-cc29-44f9-ada3-01075eee02b8"), "Iskallian_Executor"));
			
			//if (FMLCommonHandler.instance().getClientToServerNetworkManager() != null)
				//this.fakePlayer.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), FMLCommonHandler.instance().getClientToServerNetworkManager(), this.fakePlayer);
		}
	}
	
	@Override
	public void update()
	{
		if (this.worldObj.isRemote)
			return;
		
		initializeFakePlayer();
		
		if (this.fakePlayer != null)
		{
			ItemStack actionStack = this.getStackInSlot(9);
			
			// DUNSWE
			int meta = this.getBlockMetadata();
			
			// 0 south
			// 90 west
			// 180 north
			// 270 east
			
			//pitch
			// 0 front
			// 90 down
			// 180 up
			
			EnumFacing ieFacing = EnumFacing.getFront(meta);
			BlockPos pos = this.getPos().offset(ieFacing);
			
			this.fakePlayer.setPositionAndRotation(
					pos.getX(), 
					pos.getY() - 1, 
					pos.getZ(), 
					0.0F, //Yaw
					0.0F); //Pitch
			
			if (actionStack != null)
			{
				//this.fakePlayer.inventory.setInventorySlotContents(EntityEquipmentSlot.MAINHAND, actionStack);
				this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, actionStack);
				this.fakePlayer.setActiveHand(EnumHand.MAIN_HAND);
				ActionResult<ItemStack> result = actionStack.getItem().onItemRightClick(actionStack, worldObj, fakePlayer, EnumHand.MAIN_HAND);
				
				if (result.getResult() != actionStack)
				{
					this.setInventorySlotContents(9, null);
					for (int i = 0; i < 9; ++i)
					{
						if (this.getStackInSlot(i) == null)
						{
							this.setInventorySlotContents(i, result.getResult());
							break;
						}
					}
				}
				
				IExtras.logger.info("r:" + result.getResult() + ", as:" + actionStack + " == " + this.fakePlayer.getPosition() + " | " + this.getPos());
			}
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
