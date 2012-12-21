/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.inventory.ISpecialInventory;

public class SpecialInventoryWrapper extends InventoryWrapper implements
		ISpecialInventory {

	protected final ForgeDirection side;

	public SpecialInventoryWrapper(ISpecialInventory target, ForgeDirection side) {
		super(target);
		this.side = side;
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		return ((ISpecialInventory) target).addItem(stack, doAdd, side);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from,
			int maxItemCount) {
		return ((ISpecialInventory) target).extractItem(doRemove, side,
				maxItemCount);
	}

}
