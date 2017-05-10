package zairus.iextras;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import zairus.iextras.block.IEBlocks;
import zairus.iextras.event.IEEvents;
import zairus.iextras.gui.GuiHandler;
import zairus.iextras.inventory.crafting.IECraftingManager;
import zairus.iextras.item.IEItems;
import zairus.iextras.proxy.CommonProxy;
import zairus.iextras.sound.IESoundEvents;
import zairus.iextras.util.network.PacketPipeline;

@Mod(modid = IEConstants.MODID, name = IEConstants.MODNAME, version = IEConstants.VERSION)
public class IExtras
{
	@Mod.Instance(IEConstants.MODID)
	public static IExtras instance;
	
	@SidedProxy(clientSide = IEConstants.CLIENT_PROXY, serverSide = IEConstants.COMMON_PROXY)
	public static CommonProxy proxy;
	
	public static PacketPipeline packetPipeline = new PacketPipeline();
	
	public static Logger logger;
	
	public static CreativeTabs creativeTab = new CreativeTabs("iextras") {
		@Override
		public Item getTabIconItem()
		{
			return Item.getItemFromBlock(IEBlocks.IEXECUTOR);
		}
	};
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		
		IEConfig.init(event.getSuggestedConfigurationFile());
		
		IExtras.proxy.preInit(event);
		
		IESoundEvents.register();
	}
	
	@Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
		IExtras.proxy.init(event);
		IExtras.packetPipeline.initalise();
		
		IEItems.register();
		IEBlocks.register();
		
		IExtras.proxy.initBuiltinShapes();
		
		IECraftingManager.addRecipes();
		
		IEEvents eventHandler = new IEEvents();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		MinecraftForge.TERRAIN_GEN_BUS.register(eventHandler);
		MinecraftForge.ORE_GEN_BUS.register(eventHandler);
		
		//Achievements and or stats
		
		NetworkRegistry.INSTANCE.registerGuiHandler(IExtras.instance, new GuiHandler());
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
