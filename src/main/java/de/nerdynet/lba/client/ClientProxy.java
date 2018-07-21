package de.nerdynet.lba.client;

import de.nerdynet.lba.common.CommonProxy;
import de.nerdynet.lba.common.init.ModBlocks;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		ModBlocks.initModels();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		MinecraftForge.EVENT_BUS.post(new com.teamwizardry.refraction.client.proxy.ClientProxy.ResourceReloadEvent(resourceManager));
	}

	public static class ResourceReloadEvent extends Event {
		public final IResourceManager resourceManager;

		public ResourceReloadEvent(IResourceManager manager) {
			resourceManager = manager;
		}
	}
}
