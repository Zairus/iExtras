package zairus.iextras;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class IEConfig
{
	public static Configuration configuration;
	
	public static int IEXECUTOR_ENERGY_CAPACITY = 10000;
	public static int IEXECUTOR_ENERGY_RECEIVE = 80;
	public static int IEXECUTOR_ENERGY_CONSUMPTION = 20;
	
	public static void init(File cFile)
	{
		configuration = new Configuration(cFile);
		
		configuration.load();
		
		IEXECUTOR_ENERGY_CAPACITY = configuration.getInt("IEXECUTOR_ENERGY_CAPACITY", "ENERGY_SETTINGS", IEXECUTOR_ENERGY_CAPACITY, 0, Integer.MAX_VALUE, "Defines the maximum energy capacity for the iExecutor.");
		IEXECUTOR_ENERGY_RECEIVE = configuration.getInt("IEXECUTOR_ENERGY_RECEIVE", "ENERGY_SETTINGS", IEXECUTOR_ENERGY_RECEIVE, 0, Integer.MAX_VALUE, "Defines the energy amount the iExecutor can receive on a tick.");
		IEXECUTOR_ENERGY_CONSUMPTION = configuration.getInt("IEXECUTOR_ENeRGY_CONSUMPTION", "ENERGY_SETTINGS", IEXECUTOR_ENERGY_CONSUMPTION, 0, Integer.MAX_VALUE, "Defines the amount of energy the iExecutor will consume per operation.");
		
		configuration.save();
	}
}
