package zairus.iextras;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import zairus.iextras.proxy.CommonProxy;

@Mod(modid = IEConstants.MODID, name = IEConstants.MODNAME, version = IEConstants.VERSION)
public class IExtras
{
	@Mod.Instance(IEConstants.MODID)
	public static IExtras instance;
	
	@SidedProxy(clientSide = IEConstants.CLIENT_PROXY, serverSide = IEConstants.COMMON_PROXY)
	public static CommonProxy proxy;
	
	//Packet Pipeline
	
	public static Logger logger;
	
	//Creative tabs
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		IEConfig.init(event.getSuggestedConfigurationFile());
		
		IExtras.proxy.preInit(event);
		
		//Sounds register
	}
	
	@Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
		IExtras.proxy.init(event);
		//Packer pipeline initialize
		
		//Register items
		//Register blocks
		
		IExtras.proxy.initBuiltinShapes();
		
		//Creafting recipes
		
		//Events
		
		//Achievements and or stats
		
		//Gui handlers
    }
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		IExtras.proxy.postInit(event);
	}
	
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		//Register commands
	}
}
