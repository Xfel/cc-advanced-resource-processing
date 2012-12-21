/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class DatabaseAPI {
	protected static DatabaseAPI INSTANCE;

	private List<IItemPropertyProvider> itemPropertyProviders = new LinkedList<IItemPropertyProvider>();

	private List<IItemStackPropertyProvider> itemStackPropertyProviders = new LinkedList<IItemStackPropertyProvider>();

	public void registerItemStackPropertyProvider(
			IItemStackPropertyProvider provider) {
		itemStackPropertyProviders.add(provider);
	}

	public void registerItemPropertyProvider(IItemPropertyProvider provider) {
		itemPropertyProviders.add(provider);
	}

	public Map<String, Object> getItemProperties(ItemKey key) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("id", key.toString());
		hm.put("name", getItemName(key));

		for (IItemPropertyProvider ipp : itemPropertyProviders) {
			ipp.getProperties(key, hm);
		}

		return hm;
	}

	public Map<String, Object> getItemStackProperties(ItemStack stack) {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("item", new ItemKey(stack).toString());
		hm.put("count", stack.stackSize);

		if (stack.isItemStackDamageable()) {
			hm.put("damage", stack.getItemDamage());
		}

		for (IItemStackPropertyProvider ipp : itemStackPropertyProviders) {
			ipp.getProperties(stack, hm);
		}

		return hm;
	}

	public static DatabaseAPI instance() {
		if (INSTANCE == null) {

			INSTANCE = new DatabaseAPI();
		}

		return INSTANCE;
	}

	public String getItemName(ItemKey key) {
		return null;
	}

	public void registerItem(ItemKey item) {
		// does nothing
	}

	public void ignoreItem(ItemKey item) {
		// does nothing
	}

	public ItemKey getItem(ItemStack stack) {
		return new ItemKey(stack);
	}

	public ItemKey getItem(String spec) {
		try {
			return ItemKey.parse(spec);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
