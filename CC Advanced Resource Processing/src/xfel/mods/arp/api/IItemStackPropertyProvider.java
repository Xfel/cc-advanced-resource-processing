/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.api;

import java.util.Map;

import net.minecraft.item.ItemStack;

/**
 * @author Xfel
 *
 */
public interface IItemStackPropertyProvider {
	
	void getProperties(ItemStack stack, Map<String, Object> properties);
	
}
