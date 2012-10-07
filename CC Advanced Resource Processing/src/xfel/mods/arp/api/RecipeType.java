/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.api;

import java.util.HashMap;
import java.util.Map;

public class RecipeType {
	private static Map<String, RecipeType> allTypes = new HashMap<String, RecipeType>();

	/**
 * 
 */
	public static final RecipeType CRAFTING = getOrCreateRecipeType("crafting",
			3, 3);

	/**
 * 
 */
	public static final RecipeType FURNACE = getOrCreateRecipeType("furnace",
			1, 1);
	private String id;
	private int gridWidth;
	private int gridHeight;

	/**
	 * @param id
	 * @return
	 */
	public static RecipeType getRecipeType(String id) {
		return (RecipeType) allTypes.get(id.toLowerCase());
	}

	/**
	 * @param id
	 * @param gridWidth
	 * @param gridHeight
	 * @return
	 */
	public static RecipeType getOrCreateRecipeType(String id, int gridWidth,
			int gridHeight) {
		id = id.toLowerCase();

		RecipeType rt = (RecipeType) allTypes.get(id);
		if (rt == null) {
			rt = new RecipeType(id, gridWidth, gridHeight);
			allTypes.put(id, rt);
		}
		return rt;
	}

	private RecipeType(String id, int gridWidth, int gridHeight) {
		this.id = id;
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
	}

	public String getId() {
		return this.id;
	}

	public int getGridWidth() {
		return this.gridWidth;
	}

	public int getGridHeight() {
		return this.gridHeight;
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecipeType other = (RecipeType) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

	public String toString() {
		return this.id;
	}
}