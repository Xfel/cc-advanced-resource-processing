/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import xfel.mods.arp.base.blocks.BlockSubtypes;
import xfel.mods.arp.common.tiles.TileDatabase;
import xfel.mods.arp.common.tiles.TileDigitalAllocator;
import xfel.mods.arp.common.tiles.TileInventoryInterface;

public class BlockAdvancedMachine extends BlockSubtypes {

	public static final int TYPE_DATABASE = 0;
	public static final int TYPE_INVENTORY_INTERFACE = 1;
	public static final int TYPE_DIGITAL_CHEST = 2;
	public static final int TYPE_DIGITAL_WORKBENCH = 7;
	public static final int TYPE_DIGITAL_ALLOCATOR = 11;

	public BlockAdvancedMachine(int id) {
		super(id, Material.iron);
		setHardness(1);
		setCreativeTab(CreativeTabs.tabRedstone);
		setTextureFile("/terrain/digitalmachines.png");
		setBlockName("arpmachine");

		registerSubtype(TYPE_DATABASE, "database", TileDatabase.class);

		registerSubtype(TYPE_INVENTORY_INTERFACE, "iinterface",
				TileInventoryInterface.class);
		// registerSubtype(TYPE_DIGITAL_WORKBENCH, "workbench",
		// TileDigitalWorkbench.class);
		registerSubtype(TYPE_DIGITAL_ALLOCATOR, "allocator",
				TileDigitalAllocator.class);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int metadata) {
		if (metadata == TYPE_DATABASE) {
			if (side != 0 && side != 1) {
				return 2;
			}
		} else if (metadata == TYPE_INVENTORY_INTERFACE) {
			if (side == 4) {
				return 3;
			}
		} else if (metadata == TYPE_DIGITAL_ALLOCATOR) {
			if (side == 2) {
				return 8;
			}
			if (side == 3) {
				return 10;
			}
		}
		if (side == 0 || side == 1) {
			return 0;
		}
		return 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
