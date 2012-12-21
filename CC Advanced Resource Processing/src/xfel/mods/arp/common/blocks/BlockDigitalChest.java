/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.arp.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xfel.mods.arp.base.blocks.BlockExtended;
import xfel.mods.arp.common.tiles.TileDigitalChest;

public class BlockDigitalChest extends BlockExtended{

	public BlockDigitalChest(int id) {
		super(id, Material.iron);
		setHardness(1);
		setCreativeTab(CreativeTabs.tabRedstone);
		setBlockName("digitalchest");
		
//		registerSuptype(0, "instance", TileDigitalChest.class);
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileDigitalChest();
	}
	
}
