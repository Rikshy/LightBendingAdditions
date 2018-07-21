package de.nerdynet.lba;

import de.nerdynet.lba.common.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
		modid = LightBendingAdditions.MOD_ID,
		name = LightBendingAdditions.MOD_NAME,
		version = LightBendingAdditions.VERSION,
		dependencies = LightBendingAdditions.DEPENDENCIES
)
public class LightBendingAdditions {

	public static final String MOD_ID = "lightbendingadditions";
	public static final String MOD_NAME = "Light Bending Additions";
	public static final String VERSION = "GRADLE:VERSION";
	public static final String DEPENDENCIES = "required-before:refraction";

	public static final String CLIENT = "de.nerdynet.lba.client.ClientProxy";
	public static final String SERVER = "de.nerdynet.lba.common.CommonProxy";

	@SidedProxy(clientSide = LightBendingAdditions.CLIENT, serverSide = LightBendingAdditions.SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static LightBendingAdditions instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}
}
