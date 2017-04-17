package zairus.iextras.inventory.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zairus.iextras.block.IEBlocks;

public class IECraftingManager
{
	public static void addRecipes()
	{
		GameRegistry.addShapedRecipe(new ItemStack(
				IEBlocks.IEXECUTOR)
				, new Object[] {
						"idi"
						,"crc"
						,"iii"
						,'i'
						,Blocks.IRON_BLOCK
						,'d'
						,Blocks.DIAMOND_BLOCK
						,'c'
						,new ItemStack(Items.DYE, 1, 2)
						,'r'
						,Items.REDSTONE
				});
	}
}
