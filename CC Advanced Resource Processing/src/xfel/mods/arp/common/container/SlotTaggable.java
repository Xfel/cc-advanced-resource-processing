/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTaggable extends Slot {

	public SlotTaggable(IInventory inventory, int slotIndex, int xpos, int ypos) {
		super(inventory, slotIndex, xpos, ypos);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null) {
			super.putStack(null);
		} else {
			super.putStack(new ItemStack(stack.itemID, 1, stack.getItemDamage()));
		}
		return false;
	}
	
	@Override
	public void putStack(ItemStack stack) {
		if (stack == null) {
			super.putStack(null);
		} else {
			super.putStack(new ItemStack(stack.itemID, 1, stack.getItemDamage()));
		}
	}

	@Override
	public ItemStack getStack() {
		return ItemStack.copyItemStack(super.getStack());
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public ItemStack decrStackSize(int amount) {
		super.decrStackSize(1);
		return null;
	}

}
