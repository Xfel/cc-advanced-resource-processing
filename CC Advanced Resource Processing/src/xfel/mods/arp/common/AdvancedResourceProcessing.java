/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common;

import java.util.logging.Logger;

import xfel.mods.arp.common.CommonProxy;
import xfel.mods.arp.common.network.NetworkHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

/**
 * Mod main class
 * 
 * @author Xfel
 * 
 */
@Mod(modid = "ccarp", name = AdvancedResourceProcessing.MOD_VERSION, version = "@mod.version@", useMetadata = false)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, packetHandler = NetworkHandler.class, channels = { NetworkHandler.TILE_UPDATE_CHANNEL })
public class AdvancedResourceProcessing {

	public static final Logger MOD_LOGGER;

	static {
		MOD_LOGGER = Logger.getLogger("AdvancedResourceProcessing");
		MOD_LOGGER.setParent(FMLLog.getLogger());
	}

	static final String MOD_VERSION = "@mod.version@";

	@SidedProxy(clientSide = "xfel.mods.arp.client.ClientProxy", serverSide = "xfel.mods.arp.common.CommonProxy")
	public static CommonProxy sideHandler;

	@Instance
	public static AdvancedResourceProcessing instance;

	@Metadata
	public static ModMetadata metadata;

	@PreInit
	public void loadConfig(FMLPreInitializationEvent evt) {
		evt.getModMetadata().version = MOD_VERSION;
	}

	@Init
	public void init(FMLInitializationEvent evt) {

	}

	@PostInit
	public void setupCompability(FMLPostInitializationEvent evt) {

	}
}
