/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.blocks;

import java.util.List;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemBlockSubtypes extends ItemBlock {

	private BlockSubtypes.Subtype[] subtypes;

	public ItemBlockSubtypes(int id) {
		super(id);
		setHasSubtypes(true);

		subtypes = ((BlockSubtypes) Block.blocksList[getBlockID()]).subtypes;
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
		if(subtypes[stack.getItemDamage()]==null)return null;
		String baseName=super.getItemName();
		return baseName+"."+subtypes[stack.getItemDamage()].name;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab,
			List result) {
		for (int metadata = 0; metadata < subtypes.length; metadata++) {
			BlockSubtypes.Subtype st = subtypes[metadata];
			if(st!=null){
				result.add(new ItemStack(this, 1, metadata));
			}
		}
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}
