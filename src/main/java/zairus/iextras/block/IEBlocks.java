package zairus.iextras.block;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zairus.iextras.IExtras;

public class IEBlocks
{
	public static final Block IEXECUTOR;
	
	static
	{
		IEXECUTOR = new BlockIExecutor().setBlockName("iexecutor");
	}
	
	public static void register()
	{
		((IBlockBase)IEXECUTOR).register();
	}
	
	protected static void registerBlock(Block block, String name, Class<? extends TileEntity> teClazz, String id, boolean model)
	{
		IExtras.proxy.registerBlock(block, name, teClazz, id, model);
	}
	
	@SuppressWarnings("unused")
	private static void registerBlock(Block block, String name)
	{
		IExtras.proxy.registerBlock(block, name, true);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient()
	{
		;
	}
}
