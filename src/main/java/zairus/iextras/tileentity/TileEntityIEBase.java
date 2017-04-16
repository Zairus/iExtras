package zairus.iextras.tileentity;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.iextras.IEConstants;
import zairus.iextras.inventory.ContainerIEBase;

public abstract class TileEntityIEBase extends TileEntityLockableLoot implements ITickable, ILootContainer
{
	public int playersUsing;
	
	protected String customName;
	protected String defaultName = "IEContainer";
	
	public TileEntityIEBase()
	{
		;
	}
	
	public abstract ItemStack[] getChestContents();
	public abstract void setChestContents(ItemStack[] contents);
	public abstract int getSlotXOffset();
	public abstract int getSlotYOffset();
	
	public abstract Slot getSlot(IInventory inv, int index, int x, int y);
	
	@Nullable
	public abstract SoundEvent getOpenSound();
	
	@Nullable
	public abstract SoundEvent getCloseSound();
	
	@Nullable
	public abstract SoundEvent getItemPlaceSound();
	
	@SideOnly(Side.CLIENT)
	public abstract ResourceLocation getTextures();
	
	public String getDefaultName()
	{
		return this.defaultName;
	}
	
	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
	{
		this.fillWithLoot(player);
		return new ContainerIEBase(playerInventory, this, player);
	}
	
	@Override
	public String getGuiID()
	{
		return IEConstants.MODID + ":" + this.defaultName;
	}
	
	public String getGUIDisplayName()
	{
		return this.hasCustomName()? this.customName : "Iskallian Executor";
	}
	
	@Override
	public String getName()
	{
		return this.hasCustomName()? this.customName : "container." + this.defaultName;
	}
	
	@Override
	public boolean hasCustomName()
	{
		return customName != null;
	}
	
	public void setCustomName(String name)
	{
		this.customName = name;
	}
	
	@Override
	public int getSizeInventory()
	{
		return getChestContents().length;
	}
	
	public boolean isEmpty()
	{
		boolean empty = true;
		
		for (int i = 0; i < getChestContents().length; ++i)
		{
			if (getChestContents()[i] != null)
			{
				empty = false;
				break;
			}
		}
		
		return empty;
	}
	
	@Override
	public ItemStack getStackInSlot(int index)
	{
		this.fillWithLoot((EntityPlayer)null);
		return index < getChestContents().length ? getChestContents()[index] : null;
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		this.fillWithLoot((EntityPlayer)null);
		
		if (getChestContents()[index] != null)
		{
			if (getChestContents()[index].stackSize <= count)
			{
				ItemStack itemstack1 = getChestContents()[index];
				getChestContents()[index] = null;
                this.markDirty();
                return itemstack1;
			}
			else
			{
				ItemStack itemstack = getChestContents()[index].splitStack(count);
				
                if (getChestContents()[index].stackSize == 0)
                {
                	getChestContents()[index] = null;
                }
                
                this.markDirty();
                return itemstack;
			}
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index)
	{
		this.fillWithLoot((EntityPlayer)null);
		
		if (getChestContents()[index] != null)
        {
            ItemStack itemstack = getChestContents()[index];
            getChestContents()[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.fillWithLoot((EntityPlayer)null);
		
		getChestContents()[index] = stack;
		
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
        
        this.markDirty();
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}
	
	@Override
	public void openInventory(EntityPlayer player)
	{
		if (!player.isSpectator())
		{
			if (this.playersUsing < 0)
			{
				this.playersUsing = 0;
			}
			
			++this.playersUsing;
			this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.playersUsing);
			this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
			this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
		}
	}
	
	@Override
	public void closeInventory(EntityPlayer player)
	{
		if (!player.isSpectator())
		{
			--this.playersUsing;
			this.worldObj.addBlockEvent(this.pos, this.getBlockType(), 1, this.playersUsing);
			this.worldObj.notifyNeighborsOfStateChange(this.pos, this.getBlockType());
			this.worldObj.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType());
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}
	
	@Override
	public int getField(int id)
	{
		return 0;
	}
	
	@Override
	public void setField(int id, int value)
	{
	}
	
	@Override
	public int getFieldCount()
	{
		return 0;
	}
	
	@Override
	public void clear()
	{
		this.fillWithLoot((EntityPlayer)null);
		
		for (int i = 0; i < getChestContents().length; ++i)
		{
			getChestContents()[i] = null;
		}
	}
	
	@Override
	public void update()
	{
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		setChestContents(new ItemStack[this.getSizeInventory()]);
		
		if (!this.checkLootAndRead(compound))
		{
			for (int i = 0; i < nbttaglist.tagCount(); ++i)
			{
				NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot") & 255;
				
				if (j >= 0 && j < getChestContents().length)
				{
					getChestContents()[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
				}
			}
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		if (!this.checkLootAndWrite(compound))
		{
			NBTTagList nbttaglist = new NBTTagList();
			
			for (int i = 0; i < getChestContents().length; ++i)
			{
				if (getChestContents()[i] != null)
				{
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setByte("Slot", (byte)i);
					getChestContents()[i].writeToNBT(nbttagcompound);
					nbttaglist.appendTag(nbttagcompound);
				}
			}
			
			compound.setTag("Items", nbttaglist);
		}
		
		if (this.hasCustomName())
		{
			compound.setString("CustomName", this.customName);
		}
		
		return compound;
	}
	
	@Override
	public void updateContainingBlockInfo()
	{
		super.updateContainingBlockInfo();
	}
	
	@Override
	public boolean receiveClientEvent(int id, int type)
	{
		if (id == 1)
		{
			this.playersUsing = type;
			return true;
		}
		else
		{
			return super.receiveClientEvent(id, type);
		}
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
		this.updateContainingBlockInfo();
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
}
