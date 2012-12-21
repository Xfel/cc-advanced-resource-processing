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

public class SlotReadOnly extends Slot {

	public SlotReadOnly(IInventory inv, int index, int xpos, int ypos) {
		super(inv, index, xpos, ypos);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int par1) {
		return null;
	}

	@Override
	public ItemStack getStack() {
		return ItemStack.copyItemStack(super.getStack());
	}

}
