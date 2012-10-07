/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.blocks;

import java.util.logging.Level;

import xfel.mods.arp.common.AdvancedResourceProcessing;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class BlockSubtypes extends BlockExtended {

	static class Subtype {
		String name;
		Class<? extends TileEntity> teClass;
		Class<? extends TileEntity> teClientClass;

		Subtype(String name, Class<? extends TileEntity> teClass,
				Class<? extends TileEntity> teClientClass) {
			super();
			this.name = name;
			this.teClass = teClass;
			this.teClientClass = teClientClass;
		}

		Class<? extends TileEntity> getTileClass(World world) {
			if (world != null && world.isRemote)
				return teClientClass;
			return teClass;
		}
	}

	Subtype[] subtypes;

	protected BlockSubtypes(int id, Material material) {
		super(id, material);
		subtypes = new Subtype[16];
	}

	public void registerSubtype(int metadata, String name,
			Class<? extends TileEntity> teClass) {
		subtypes[metadata] = new Subtype(name, teClass, teClass);
	}

	public void registerSubtype(int metadata, String name,
			Class<? extends TileEntity> teServerClass,
			Class<? extends TileEntity> teClientClass) {
		subtypes[metadata] = new Subtype(name, teServerClass, teClientClass);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return subtypes[metadata] != null;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		if (subtypes[metadata] == null)
			return null;

		Class<? extends TileEntity> teClass = subtypes[metadata]
				.getTileClass(world);

		try {
			return teClass.newInstance();
		} catch (InstantiationException e) {
			// should not happen
			AdvancedResourceProcessing.MOD_LOGGER.log(Level.SEVERE,
					"Unexpected exception while creating tile entity", e);
		} catch (IllegalAccessException e) {
			// should not happen
			AdvancedResourceProcessing.MOD_LOGGER.log(Level.SEVERE,
					"Unexpected exception while creating tile entity", e);
		}
		return null;
	}
	
	@Override
	protected int damageDropped(int metadata) {
		return metadata;
	}

}
