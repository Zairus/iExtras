package zairus.iextras.block;

public interface IBlockBase
{
	abstract IBlockBase setBlockName(String name);
	abstract String getBlockName();
	abstract void register();
}
