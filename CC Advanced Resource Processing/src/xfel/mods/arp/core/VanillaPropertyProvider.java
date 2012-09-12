/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.arp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.src.Block;
import net.minecraft.src.BlockCloth;
import net.minecraft.src.Item;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemMap;
import net.minecraft.src.ItemMapBase;
import net.minecraft.src.ItemStack;
import xfel.mods.arp.api.IItemPropertyProvider;
import xfel.mods.arp.api.IItemStackPropertyProvider;
import xfel.mods.arp.api.ItemKey;

public class VanillaPropertyProvider implements IItemPropertyProvider,IItemStackPropertyProvider{

	@Override
	public void getProperties(ItemStack stack, Map<String, Object> result) {
		Item item=stack.getItem();
		
		if(item.isDamageable()){
			result.put("damage", stack.getItemDamage());
		}
		
		if (item instanceof ItemMapBase) {
			ItemMapBase imb = (ItemMapBase) item;
			result.put("mapid", stack.getItemDamage());
		}
		
//		List<String> additionalData=new ArrayList<String>();
//		item.addInformation(stack, additionalData);
	}

	@Override
	public void getProperties(ItemKey key, Map<String, Object> result) {
		Item item=key.getItem();
		
		result.put("damagable", item.isDamageable());
		result.put("repairable", item.isRepairable());
		result.put("maxDamage", item.getMaxDamage());
		result.put("containerItem", ResourceDatabase.instance().getItemName(new ItemKey(item.getContainerItemStack(key.toItemStack(1)))));
	
		if(key.isBlock()){
			result.put("block", Boolean.TRUE);
			
			Block block=key.getBlock();
			result.put("solid", block.blockMaterial.isSolid());
			result.put("movable", block.blockMaterial.getMaterialMobility()==0);
			result.put("opaque", block.blockMaterial.isOpaque());
			result.put("liquid", block.blockMaterial.isLiquid());
			
			if (block instanceof BlockCloth) {
				result.put("color", ItemDye.dyeColorNames[BlockCloth.getBlockFromDye(key.getMetadata())]);
			}
		}else if (item instanceof ItemDye) {
			result.put("color", ItemDye.dyeColorNames[key.getMetadata()]);
		}else if (item instanceof ItemFood) {
			ItemFood ifood = (ItemFood) item;
			
			result.put("healAmount", ifood.getHealAmount());
		}
	}

}
