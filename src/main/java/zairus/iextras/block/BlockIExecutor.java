package zairus.iextras.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import zairus.iextras.IExtras;
import zairus.iextras.tileentity.TileEntityIExecutor;

public class BlockIExecutor extends BlockIEContainerBase implements IBlockBase
{
	private String blockName = "";
	
	public BlockIExecutor()
	{
		super(Material.GROUND);
		this.setCreativeTab(IExtras.creativeTab);
		this.setDefaultState(this.blockState.getBaseState());
		this.setHardness(1.5F);
		this.setSoundType(SoundType.METAL);
		this.setHarvestLevel("pickaxe", 0);
	}
	
	@Override
	public BlockIExecutor setBlockName(String name)
	{
		this.blockName = name;
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		return this;
	}
	
	@Override
	public String getBlockName()
	{
		return this.blockName;
	}
	
	@Override
	public void register()
	{
		IEBlocks.registerBlock(this, this.blockName, TileEntityIExecutor.class, "tileEntityIExecutor", true);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityIExecutor();
	}
}
