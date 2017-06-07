package zairus.iextras.block;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import zairus.iextras.IExtras;
import zairus.iextras.gui.GuiHandler;
import zairus.iextras.tileentity.TileEntityIExecutor;

public class BlockIExecutor extends BlockIEContainerBase implements IBlockBase
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			return true;
		}
		else
		{
			TileEntity te = world.getTileEntity(pos);
			
			if (te != null && te instanceof TileEntityIExecutor)
			{
				player.openGui(IExtras.instance, GuiHandler.GUI_EXECUTOR, world, pos.getX(), pos.getY(), pos.getZ());
			}
			
			return true;
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityIExecutor();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}
	
	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withProperty(FACING, mirrorIn.mirror((EnumFacing)state.getValue(FACING)));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState iblockstate = this.getDefaultState();
		iblockstate = iblockstate.withProperty(FACING, EnumFacing.getFront(meta));
		return iblockstate;
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, getFacingFromEntity(pos, placer) ); //facing
	}
	
	public static EnumFacing getFacingFromEntity(BlockPos pos, EntityLivingBase entity)
	{
		if (MathHelper.abs((float)entity.posX - (float)pos.getX()) < 2.0F && MathHelper.abs((float)entity.posZ - (float)pos.getZ()) < 2.0F)
		{
			double d0 = entity.posY + (double)entity.getEyeHeight();
			
			if (d0 - (double)pos.getY() > 2.0D)
			{
				return EnumFacing.UP;
			}
			
			if ((double)pos.getY() - d0 > 0.0D)
			{
				return EnumFacing.DOWN;
			}
		}
		
		return entity.getHorizontalFacing().getOpposite();
	}
}
