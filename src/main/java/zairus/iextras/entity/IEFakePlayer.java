package zairus.iextras.entity;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import zairus.iextras.IExtras;
import zairus.iextras.entity.util.InventoryFakePlayer;
import zairus.iextras.tileentity.TileEntityIEBase;
import zairus.iextras.tileentity.TileEntityIExecutor;

public class IEFakePlayer extends FakePlayer
{
	private final TileEntityIEBase master;
	
	public IEFakePlayer(WorldServer world, GameProfile name, TileEntityIEBase masterTE)
	{
		super(world, name);
		this.inventory = new InventoryFakePlayer(this);
		this.master = masterTE;
	}
	
	public void itemAddedToInventory(ItemStack stack)
	{
		if (!this.worldObj.isRemote)
		{
			IExtras.logger.info("item been added:" + stack + ", s:" + stack.stackSize);
			
			((TileEntityIExecutor)master).addStackToAvailableSlot(stack);
		}
	}
}
