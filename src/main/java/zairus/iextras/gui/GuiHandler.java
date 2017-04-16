package zairus.iextras.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import zairus.iextras.client.gui.GuiIExecutor;
import zairus.iextras.inventory.ContainerIExecutor;
import zairus.iextras.tileentity.TileEntityIExecutor;

public class GuiHandler implements IGuiHandler
{
	public static final int GUI_EXECUTOR = 0;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity;
		
		switch (ID)
		{
		case GUI_EXECUTOR:
			tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileEntityIExecutor)
			{
				return new ContainerIExecutor(player.inventory, (TileEntityIExecutor)tileEntity, player);
			}
			break;
		default:
			break;
		}
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity;
		
		switch (ID)
		{
		case GUI_EXECUTOR:
			tileEntity = world.getTileEntity(new BlockPos(x, y, z));
			if (tileEntity instanceof TileEntityIExecutor)
			{
				return new GuiIExecutor(player.inventory, (TileEntityIExecutor)tileEntity, player);
			}
			break;
		default:
			break;
		}
		
		return null;
	}
}
