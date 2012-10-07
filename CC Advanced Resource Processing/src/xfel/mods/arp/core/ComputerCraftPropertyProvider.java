/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import dan200.CCTurtle;
import dan200.computer.shared.ItemComputerBase;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.shared.ItemTurtle;
import dan200.turtle.shared.TurtleAPI;

import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import xfel.mods.arp.api.IItemPropertyProvider;
import xfel.mods.arp.api.IItemStackPropertyProvider;
import xfel.mods.arp.api.ItemKey;

public class ComputerCraftPropertyProvider implements IItemPropertyProvider,
		IItemStackPropertyProvider {

	@Override
	public void getProperties(ItemStack stack, Map<String, Object> properties) {
		Item item=stack.getItem();
		if (item instanceof ItemComputerBase) {
			ItemComputerBase icb = (ItemComputerBase) item;
			
			int computerId=icb.getComputerIDFromItemStack(stack);
			String computerLabel=ItemComputerBase.getComputerLabelOnServer(computerId);
			
			properties.put("computerId", Integer.valueOf(computerId));
			properties.put("computerLabel", computerLabel);
		}
	}

	@Override
	public void getProperties(ItemKey key, Map<String, Object> result) {
		Item item = key.getItem();
		if (item instanceof ItemTurtle) {
			ItemTurtle iturtle = (ItemTurtle) item;
			
			ItemStack stack=key.toItemStack(1);
			ITurtleUpgrade leftUpgrade=iturtle.getLeftUpgradeFromItemStack(stack);
			ITurtleUpgrade rightUpgrade=iturtle.getRightUpgradeFromItemStack(stack);
			
			if(leftUpgrade!=null){
				Map<Object,Object> ltu=new Hashtable<Object, Object>();
				ltu.put("adjective", leftUpgrade.getAdjective());
				ltu.put("item", ResourceDatabase.instance().getItemName(new ItemKey(leftUpgrade.getCraftingItem())));
				ltu.put("type", leftUpgrade.getType().toString());
				result.put("leftUpgrade", ltu);
			}
			
			if(rightUpgrade!=null){
				Map<Object,Object> ltu=new Hashtable<Object, Object>();
				ltu.put("adjective", rightUpgrade.getAdjective());
				ltu.put("item", ResourceDatabase.instance().getItemName(new ItemKey(rightUpgrade.getCraftingItem())));
				ltu.put("type", rightUpgrade.getType().toString());
				result.put("rightUpgrade", ltu);
			}
		}else{
			ITurtleUpgrade upgrade=CCTurtle.getTurtleUpgrade(key.toItemStack(1));
			
			if(upgrade!=null){
				Map<Object,Object> ltu=new Hashtable<Object, Object>();
				ltu.put("adjective", upgrade.getAdjective());
				ltu.put("type", upgrade.getType().toString());
				result.put("turtleUpgrade", ltu);
			}
		}
	}

}
