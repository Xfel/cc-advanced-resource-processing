/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

import net.minecraft.src.Block;
import net.minecraft.src.BlockCloth;
import net.minecraft.src.BlockFlowing;
import net.minecraft.src.Enchantment;
import net.minecraft.src.Item;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemMap;
import net.minecraft.src.ItemMapBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.oredict.OreDictionary;
import xfel.mods.arp.api.IItemPropertyProvider;
import xfel.mods.arp.api.IItemStackPropertyProvider;
import xfel.mods.arp.api.ItemKey;

public class VanillaPropertyProvider implements IItemPropertyProvider,
		IItemStackPropertyProvider {

	private Map<ItemKey, Integer> oreIds = new HashMap<ItemKey, Integer>();

	public VanillaPropertyProvider() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void addOre(OreDictionary.OreRegisterEvent evt) {
		oreIds.put(new ItemKey(evt.Ore), OreDictionary.getOreID(evt.Name));
	}

	@Override
	public void getProperties(ItemStack stack, Map<String, Object> result) {
		Item item = stack.getItem();

		if (item.isDamageable()) {
			result.put("damage", stack.getItemDamage());
		}

		if (item instanceof ItemMapBase) {
			ItemMapBase imb = (ItemMapBase) item;
			result.put("mapid", stack.getItemDamage());
		}

		if (stack.isItemEnchanted()) {
			NBTTagList enchlist = stack.getEnchantmentTagList();
			Map lst = new HashMap(enchlist.tagCount());

			for (int i = 0; i < enchlist.tagCount(); i++) {
				short id = ((NBTTagCompound) enchlist.tagAt(i)).getShort("id");
				short level = ((NBTTagCompound) enchlist.tagAt(i))
						.getShort("lvl");

				Enchantment ench = Enchantment.enchantmentsList[id];

				if (ench != null) {
					lst.put(StatCollector.translateToLocal(ench.getName())
							.toLowerCase(), level);
				}
			}

			result.put("enchantments", lst);
		}

		// List<String> additionalData=new ArrayList<String>();
		// item.addInformation(stack, additionalData);
	}

	@Override
	public void getProperties(ItemKey key, Map<String, Object> result) {
		Item item = key.getItem();

		result.put("damageable", item.isDamageable());
		result.put("repairable", item.isRepairable());
		result.put("maxDamage", item.getMaxDamage());
		ItemStack containerItemStack = item.getContainerItemStack(key
				.toItemStack(1));
		if (containerItemStack != null) {
			result.put("containerItem", ResourceDatabase.instance()
					.getItemName(new ItemKey(containerItemStack)));
		}
		if (key.isBlock()) {
			result.put("block", Boolean.TRUE);

			Block block = key.getBlock();
			result.put("solid", block.blockMaterial.isSolid());
			result.put("movable",
					block.blockMaterial.getMaterialMobility() == 0);
			result.put("opaque", block.blockMaterial.isOpaque());
			result.put("liquid", block.blockMaterial.isLiquid());

			if (block instanceof BlockCloth) {
				result.put("color", ItemDye.dyeColorNames[BlockCloth
						.getBlockFromDye(key.getMetadata())]);
			}
		} else if (item instanceof ItemDye) {
			result.put("color", ItemDye.dyeColorNames[key.getMetadata()]);
		} else if (item instanceof ItemFood) {
			ItemFood ifood = (ItemFood) item;

			result.put("healAmount", ifood.getHealAmount());
		}

		if (oreIds.containsKey(key)) {
			Integer oreId = oreIds.get(key);

			result.put("ore", OreDictionary.getOreName(oreId));

			List<ItemStack> enchlist = OreDictionary.getOres(oreId);
			Map lst = new HashMap(enchlist.size());

			for (int i = 0; i < enchlist.size(); i++) {
				lst.put(Integer.valueOf(i), ResourceDatabase.instance()
						.getItemName(new ItemKey(enchlist.get(i))));
			}

			result.put("otherOres", lst);
		}
	}

}
