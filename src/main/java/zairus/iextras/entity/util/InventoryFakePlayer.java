package zairus.iextras.entity.util;

import javax.annotation.Nullable;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import zairus.iextras.entity.IEFakePlayer;

public class InventoryFakePlayer extends InventoryPlayer
{
	private final IEFakePlayer fakePlayer;
	
	public InventoryFakePlayer(IEFakePlayer player)
	{
		super(player);
		fakePlayer = player;
	}
	
	@Override
	public boolean addItemStackToInventory(@Nullable final ItemStack itemStack)
	{
		fakePlayer.itemAddedToInventory(itemStack.copy());
		
		boolean added = super.addItemStackToInventory(itemStack);
		
		return added;
	}
}
