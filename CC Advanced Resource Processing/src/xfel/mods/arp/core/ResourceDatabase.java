/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StringTranslate;
import xfel.mods.arp.api.DatabaseAPI;
import xfel.mods.arp.api.ItemKey;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * @author Xfel
 * 
 */
public class ResourceDatabase extends DatabaseAPI {

	static {
		INSTANCE = new ResourceDatabase();

		VanillaPropertyProvider vpp = new VanillaPropertyProvider();
		INSTANCE.registerItemPropertyProvider(vpp);
		INSTANCE.registerItemStackPropertyProvider(vpp);
	}

	public static ResourceDatabase instance() {
		return (ResourceDatabase) INSTANCE;
	}

	private BiMap<String, ItemKey> itemMapping = HashBiMap
			.create(32000);

	private HashSet<ItemKey> ignoredItems = new HashSet<ItemKey>();

	private int itemDamageScanRange = 16;

	public int getItemDamageScanRange() {
		return itemDamageScanRange;
	}

	public void setItemDamageScanRange(int range) {
		itemDamageScanRange = range;
	}

	@Override
	public void registerItem(ItemKey item) {
		itemMapping.put(
				StringTranslate.getInstance().translateKey(item.getName()).toLowerCase(),
				item);
	}

	@Override
	public void ignoreItem(ItemKey item) {
		ignoredItems.add(item);
	}

	public ItemKey getItem(ItemStack stack) {
		ItemKey key = new ItemKey(stack);

		if (ignoredItems.contains(key))
			return null;

		String name = itemMapping.inverse().get(key);

		if (name == null)
			return null;

		return key.withName(name);
	}

	public ItemKey getItem(String spec) {
		if (itemMapping.containsKey(spec.toLowerCase()))
			return itemMapping.get(spec.toLowerCase());

		try {
			ItemKey key = ItemKey.parse(spec);

			if (ignoredItems.contains(key)) {
				// invisible
				return null;
			}

			return key;
		} catch (NumberFormatException e) {
			// invalid id string format
			return null;
		}
	}

	public ItemKey lookupItem(String name) {
		return itemMapping.get(name.toLowerCase());
	}

	public String getItemName(ItemKey key) {
		if (key == null)
			return null;
		return itemMapping.inverse().get(key);
	}

	public void load(){
		
		
		if(FMLCommonHandler.instance().getSide().isClient()){
			lookupItemsCreative();
		}else{
			lookupItemsFromList();
		}
	}
	
	private void lookupItemsCreative() {
		List<ItemStack> items = new ArrayList<ItemStack>();

		for (int i = 0; i < Item.itemsList.length; i++) {
			Item item = Item.itemsList[i];
			if (item != null) {
				item.getSubItems(i, null, items);
			}
		}

		for (ItemStack stack : items) {
			ItemKey key = new ItemKey(stack);

			if (!ignoredItems.contains(key)
					&& !itemMapping.containsKey(key.getName())
					&& !itemMapping.containsValue(key)) {
				registerItem(key);
			}
		}
	}

	private void lookupItemsFromList() {
		for (Item item : Item.itemsList) {
			if (item == null) {
				continue;
			}

			if (item.getHasSubtypes()) {
				for (int i = 0; i < itemDamageScanRange; i++) {
					ItemKey key = new ItemKey(item, i);

					if (!ignoredItems.contains(key)
							&& !itemMapping.containsKey(key.getName())
							&& !itemMapping.containsValue(key)
							&& key.getName() != null) {
						registerItem(key);
					}
				}
			} else {
				ItemKey key = new ItemKey(item);

				if (!ignoredItems.contains(key)
						&& !itemMapping.containsKey(key.getName())
						&& !itemMapping.containsValue(key)
						&& key.getName() != null) {
					registerItem(key);
				}
			}
		}
	}

}
