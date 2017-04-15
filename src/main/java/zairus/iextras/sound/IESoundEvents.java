package zairus.iextras.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zairus.iextras.IEConstants;

public class IESoundEvents
{
	public static SoundEvent registerSound(ResourceLocation location)
	{
		SoundEvent sound = new SoundEvent(location).setRegistryName(location);
		GameRegistry.register(sound);
		return sound;
	}
	
	@SuppressWarnings("unused")
	private static SoundEvent registerSound(String location)
	{
		return registerSound(new ResourceLocation(IEConstants.MODID, location));
	}
	
	public static void register()
	{
		;
	}
}
