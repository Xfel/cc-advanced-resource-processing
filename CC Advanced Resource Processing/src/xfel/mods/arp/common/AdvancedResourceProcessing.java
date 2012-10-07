/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common;

import java.util.logging.Logger;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import xfel.mods.arp.base.blocks.ItemBlockSubtypes;
import xfel.mods.arp.base.peripheral.RomInjector;
import xfel.mods.arp.common.blocks.BlockAdvancedMachine;
import xfel.mods.arp.common.blocks.BlockDigitalChest;
import xfel.mods.arp.common.network.NetworkHandler;
import xfel.mods.arp.common.tiles.TileDatabase;
import xfel.mods.arp.common.tiles.TileDigitalAllocator;
import xfel.mods.arp.common.tiles.TileDigitalChest;
import xfel.mods.arp.common.tiles.TileInventoryInterface;
import xfel.mods.arp.common.turtle.TurtleARP;
import xfel.mods.arp.core.ResourceDatabase;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.turtle.api.TurtleAPI;

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

	public static BlockAdvancedMachine blockAdvancedMachine;
	public static BlockDigitalChest blockDigitalChest;

	
	@PreInit
	public void loadConfig(FMLPreInitializationEvent evt) {
		RomInjector.setMinecraftHome(evt.getModConfigurationDirectory().getParentFile());
		evt.getModMetadata().version = MOD_VERSION;
		
		// create the database
		ResourceDatabase.instance();

		Configuration config = new Configuration(evt.getSuggestedConfigurationFile());
		
		config.load();
		
		blockAdvancedMachine=new BlockAdvancedMachine(config.getOrCreateBlockIdProperty("advancedmachine", 2040).getInt());
		GameRegistry.registerBlock(blockAdvancedMachine, ItemBlockSubtypes.class);

		blockDigitalChest=new BlockDigitalChest(config.getOrCreateBlockIdProperty("digitalchest", 2041).getInt());
		GameRegistry.registerBlock(blockDigitalChest);
		
		config.save();
	}

	@Init
	public void init(FMLInitializationEvent evt) {

		ItemStack database = new ItemStack(blockAdvancedMachine, 1,
				BlockAdvancedMachine.TYPE_DATABASE);
		LanguageRegistry.addName(database, "Matter Database");
		GameRegistry.addRecipe(database, new Object[] { "$W§", "S#S", "SRS",
				'#', Block.bookShelf, '$', Block.glowStone, '§',
				Item.enderPearl, 'W', Item.bucketWater, 'S', Block.stone, 'R',
				Item.redstone });

		ItemStack inventoryreader = new ItemStack(blockAdvancedMachine, 1,
				BlockAdvancedMachine.TYPE_INVENTORY_INTERFACE);
		LanguageRegistry.addName(inventoryreader, "Inventory Interface");
		GameRegistry.addRecipe(inventoryreader, new Object[] { "LGL", "S#S",
				"SRS", 'L', new ItemStack(Item.dyePowder, 1, 4), 'G',
				Item.ingotGold, '#', database, 'S', Block.stone, 'R',
				Item.redstone });

		ItemStack digitalAllocator = new ItemStack(blockAdvancedMachine, 1,
				BlockAdvancedMachine.TYPE_DIGITAL_ALLOCATOR);
		LanguageRegistry.addName(digitalAllocator, "Digital Allocator");
		GameRegistry.addRecipe(digitalAllocator, new Object[] { "IRI", "P#G",
				"IRI", 'G', Item.ingotGold, '#', database, 'I', Item.ingotIron,
				'P', Block.pistonBase, 'R', Item.redstone });

		ItemStack digitalChest = new ItemStack(blockDigitalChest, 1, 0);
		LanguageRegistry.addName(digitalChest, "Digital Chest");
		GameRegistry.addRecipe(digitalChest, new Object[] { "SRS", "#CA",
				"SRS", 'A', digitalAllocator, '#', inventoryreader, 'S',
				Block.stone, 'R', Item.redstone, 'C', Block.chest });

//		ItemStack digitalWorkbench = new ItemStack(blockAdvancedMachine, 1,
//				BlockAdvancedMachine.TYPE_DIGITAL_WORKBENCH);
//		LanguageRegistry.addName(digitalWorkbench, "Digital Workbench");
//		GameRegistry.addRecipe(digitalWorkbench, new Object[] { "I#I", "RWR",
//				"IPI", 'W', Block.workbench, '#', database, 'I',
//				Item.ingotIron, 'P', Block.pistonBase, 'R', Item.redstone });

		NetworkRegistry.instance().registerGuiHandler(this, sideHandler);

		GameRegistry.registerTileEntity(TileDatabase.class, "MatterDatabase");
		GameRegistry.registerTileEntity(TileInventoryInterface.class,
				"InventoryReader");
		GameRegistry.registerTileEntity(TileDigitalAllocator.class,
				"DigitalAllocator");
//		GameRegistry.registerTileEntity(TileDigitalWorkbench.class,
//				"DigitalWorkbench");
		GameRegistry.registerTileEntity(TileDigitalChest.class,
				"DigitalChest");
		
		sideHandler.initSide();
	}

	@PostInit
	public void setupCompability(FMLPostInitializationEvent evt) {
		TurtleAPI.registerUpgrade(new TurtleARP());
		
		RomInjector.injectClasspathFile("apis/db", "lua/database.lua", MOD_VERSION);
		RomInjector.injectClasspathFile("help/db", "help/database.txt", MOD_VERSION);
		
		RomInjector.injectClasspathFile("apis/inventory", "lua/inventory.lua", MOD_VERSION);
		RomInjector.injectClasspathFile("help/inventory", "help/inventory.txt", MOD_VERSION);
		RomInjector.injectClasspathFile("programs/inventory-dump", "lua/dumpinv.lua", MOD_VERSION);
		
		ResourceDatabase.instance().load();
	}
}
