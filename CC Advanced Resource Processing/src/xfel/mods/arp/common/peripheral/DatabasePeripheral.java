/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.peripheral;

import java.util.HashMap;
import java.util.Map;

import xfel.mods.arp.api.ItemKey;
import xfel.mods.arp.api.RecipeType;
import xfel.mods.arp.base.peripheral.bind.AbstractAnnotatedPeripheral;
import xfel.mods.arp.base.peripheral.bind.PeripheralMethod;
import xfel.mods.arp.core.ResourceDatabase;

public class DatabasePeripheral extends AbstractAnnotatedPeripheral {
	
	protected final ResourceDatabase database;
	
	public DatabasePeripheral() {
		this("database");
	}

	protected DatabasePeripheral(String type) {
		super(type);
		database=ResourceDatabase.instance();
	}
	
	@PeripheralMethod
	public Map<?, ?> getItem(String nameOrId){
		ItemKey key=database.getItem(nameOrId);
		
		if(key==null){
			System.out.println("Item not found: "+nameOrId);
			return null;
		}
		
		return database.getItemProperties(key);
	}
	
	@PeripheralMethod
	public Map<?,?> getRecipeType(String id){
		RecipeType rtype=RecipeType.getRecipeType(id);
		
		if(rtype==null)return null;
		
		HashMap<String, Object> hm = new HashMap<String, Object>();
		
		hm.put("id",id);
		hm.put("width",rtype.getGridWidth());
		hm.put("height",rtype.getGridHeight());
		
		return hm;
	}
	
	
}
