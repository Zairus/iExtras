package zairus.iextras.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import zairus.iextras.tileentity.TileEntityIEBase;

public class ContainerIExecutor extends ContainerIEBase
{
	public ContainerIExecutor(IInventory playerInventory, IInventory inventory, EntityPlayer player)
	{
		super(playerInventory, inventory, player, 3, 3, 8, 16);
		
		if (inventory instanceof TileEntityIEBase)
		{
			this.addSlotToContainer(((TileEntityIEBase)inventory).getSlot(inventory, 9, 26, 17));
		}
	}
}
