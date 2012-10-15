/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.api;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

/**
 * Represents one item type. It holds the item id and (if needed) the item
 * metadata.
 * 
 * @author Xfel
 * 
 */
public class ItemKey {

	public static ItemKey parse(String idstring, String name) {
		int sep = idstring.indexOf(':');

		int itemId;
		int meta = 0;
		if (sep != -1) {
			itemId = Integer.parseInt(idstring.substring(0, sep));
			meta = Integer.parseInt(idstring.substring(sep + 1));
		} else {
			itemId = Integer.parseInt(idstring);
		}

		return new ItemKey(itemId, meta, name);
	}

	public static ItemKey parse(String idstring) {
		int sep = idstring.indexOf(':');

		int itemId;
		int meta = 0;
		if (sep != -1) {
			itemId = Integer.parseInt(idstring.substring(0, sep));
			meta = Integer.parseInt(idstring.substring(sep + 1));
		} else {
			itemId = Integer.parseInt(idstring);
		}

		return new ItemKey(itemId, meta);
	}

	private Item item;

	private int metadata;

	private String name;

	public ItemKey(int itemId) {
		this(Item.itemsList[itemId], 0, null);
	}

	public ItemKey(int itemId, String name) {
		this(Item.itemsList[itemId], 0, name);
	}

	public ItemKey(int itemId, int metadata) {
		this(Item.itemsList[itemId], metadata, null);
	}

	public ItemKey(int itemId, int metadata, String name) {
		this(Item.itemsList[itemId], metadata, name);
	}

	public ItemKey(Item item) {
		this(item, 0, null);
	}

	public ItemKey(Item item, String name) {
		this(item, 0, name);
	}

	public ItemKey(Item item, int metadata) {
		this(item, metadata, null);
	}

	public ItemKey(Item item, int metadata, String name) {
		if (item == null) {
			throw new IllegalArgumentException("Unknown Item");
		}

		this.item = item;
		if (item.getHasSubtypes())
			this.metadata = metadata;

		this.name = name;
	}

	public ItemKey(ItemStack stack) {
		this(stack, null);
	}

	public ItemKey(ItemStack stack, String name) {
		this.item = stack.getItem();
		if (item == null) {
			throw new IllegalArgumentException("Unknown Item");
		}

		if (item.getHasSubtypes())
			this.metadata = stack.getItemDamage();

		this.name = name;
	}

	public ItemKey withName(String name) {
		return new ItemKey(item, metadata, name);
	}

	public int getItemId() {
		return item.shiftedIndex;
	}

	public Item getItem() {
		return item;
	}

	public int getMetadata() {
		return metadata;
	}

	public ItemStack toItemStack(int amount) {
		return new ItemStack(item, amount, metadata);
	}

	public String getName() {
		if (name == null) {
			try {
				String iname = item.getItemNameIS(new ItemStack(item, 0,
						metadata));
				if (iname != null)
					name = iname.concat(".name");
			} catch (Exception e) {
				// an exception will most likely indicate that the given damage
				// value is unused (eg. ArrayOutOfBoundsException), so let's
				// assume it and do nothing.
			}
		}
		return name;
	}

	public boolean isBlock() {
		return item instanceof ItemBlock
				&& item.shiftedIndex < Block.blocksList.length
				&& Block.blocksList[item.shiftedIndex] != null;
	}

	public Block getBlock() {
		int id = item.shiftedIndex;
		if (!(item instanceof ItemBlock) || id >= Block.blocksList.length)
			return null;
		return Block.blocksList[id];
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(item.shiftedIndex);

		if (item.getHasSubtypes()) {
			sb.append(':');
			sb.append(metadata);
		}

		return sb.toString();
	}

	public boolean matches(ItemStack stack) {
		if (stack.getItem() != item)
			return false;

		if (item.getHasSubtypes() && stack.getItemDamage() != metadata)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + item.shiftedIndex;
		if (item.getHasSubtypes())
			result = prime * result + metadata;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemKey other = (ItemKey) obj;

		if (item.shiftedIndex != other.item.shiftedIndex)
			return false;
		if (item.getHasSubtypes() && metadata != other.metadata)
			return false;
		return true;
	}

}
