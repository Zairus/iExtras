package zairus.iextras.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import zairus.iextras.tileentity.TileEntityIEBase;

public class ContainerIEBase extends Container
{
	private IInventory inventory;
	
	private int rows = 3;
	private int columns = 9;
	
	public ContainerIEBase(IInventory playerInventory, IInventory inventory, EntityPlayer player, int r, int c)
	{
		this.inventory = inventory;
		this.inventory.openInventory(player);
		
		this.rows = r;
		this.columns = c;
		
		int xOffset = 8;
		int yOffset = 16;
		
		if (inventory instanceof TileEntityIEBase)
		{
			xOffset = ((TileEntityIEBase)inventory).getSlotXOffset();
			yOffset = ((TileEntityIEBase)inventory).getSlotYOffset();
		}
		
		// Container inventory
		for (int i = 0; i < rows; ++i)
		{
			for (int j = 0; j < columns; ++j)
			{
				if (inventory instanceof TileEntityIEBase)
				{
					this.addSlotToContainer(((TileEntityIEBase)inventory).getSlot(inventory, i * columns + j, xOffset + j * 18, yOffset + i * 18));
				}
				else
				{
					this.addSlotToContainer(new Slot(inventory, i * columns + j, xOffset + j * 18, yOffset + i * 18));
				}
			}
		}
		
		// Player inventory
		for (int l = 0; l < 3; ++l)
		{
			for (int j1 = 0; j1 < 9; ++j1)
			{
				this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, (16 + 66) + l * 18));
			}
		}
		
		// Hotbar
        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 16 + 124));
        }
	}
	
	public ContainerIEBase(IInventory playerInventory, IInventory inventory, EntityPlayer player)
	{
		this(playerInventory, inventory, player, 3, 9);
	}
	
	public ContainerIEBase setGrid(int rows, int cols)
	{
		this.rows = rows;
		this.columns = cols;
		
		return this;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return this.inventory.isUseableByPlayer(player);
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		this.inventory.closeInventory(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot)this.inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (index < (this.rows * this.columns))
			{
				if (!this.mergeItemStack(itemstack1, ((this.rows * this.columns) - 0), this.inventorySlots.size(), true))
				{
					return null;
				}
			}
			else if (!this.mergeItemStack(itemstack1, 0, (this.rows * this.columns), false))
			{
				return null;
			}
			
			if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
		}
		
		return itemstack;
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
	}
	
	@Override
	public void putStackInSlot(int slotID, ItemStack stack)
	{
		super.putStackInSlot(slotID, stack);
	}
	
	@Override
	@Nullable
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player)
	{
		if (slotId < 9 && slotId >= 0)
		{
			if (this.inventory instanceof TileEntityIEBase)
			{
				TileEntityIEBase te = (TileEntityIEBase)this.inventory;
				
				ItemStack stack = te.getStackInSlot(slotId);
				
				if (stack != null)
				{
					SoundEvent placeSound = te.getItemPlaceSound();
					
					if (placeSound != null)
					{
						World world = te.getWorld();
						world.playSound(
								(EntityPlayer)null, 
								te.getPos(), 
								placeSound, 
								SoundCategory.BLOCKS, 
								0.8F, 
								world.rand.nextFloat() * 0.1F + 0.9F);
					}
				}
			}
		}
		
		return super.slotClick(slotId, dragType, clickType, player);
	}
}
