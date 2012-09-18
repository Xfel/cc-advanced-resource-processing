/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.utils;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

/**
 * Maps a sub-part of an inventory.
 * 
 * @author Xfel
 * 
 */
public class InventoryWrapper implements IInventory {

	protected final IInventory target;

	protected final int start;

	protected final int size;

	public InventoryWrapper(IInventory target){
		this(target,0,target.getSizeInventory());
	}
	
	public InventoryWrapper(ISidedInventory sidedTarget, ForgeDirection side) {
		this(sidedTarget, sidedTarget.getStartInventorySide(side), sidedTarget
				.getSizeInventorySide(side));
	}

	public InventoryWrapper(IInventory target, int start, int size) {
		super();
		this.target = target;
		this.start = start;
		this.size = size;
	}

	public IInventory getTarget() {
		return target;
	}

	public int getStart() {
		return start;
	}

	public int getSize() {
		return size;
	}

	@Override
	public int getSizeInventory() {
		return size;
	}

	private void checkSlot(int slot) {
		if (slot < 0 || slot >= size)
			throw new IndexOutOfBoundsException("Slot index out of bounds");
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		checkSlot(slot);
		return target.getStackInSlot(start + slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		checkSlot(slot);
		return target.decrStackSize(start + slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		checkSlot(slot);
		return target.getStackInSlotOnClosing(start + slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		checkSlot(slot);
		target.setInventorySlotContents(start + slot, stack);
	}

	@Override
	public String getInvName() {
		return target.getInvName();
	}

	@Override
	public int getInventoryStackLimit() {
		return target.getInventoryStackLimit();
	}

	@Override
	public void onInventoryChanged() {
		target.onInventoryChanged();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return target.isUseableByPlayer(player);
	}

	@Override
	public void openChest() {
		target.openChest();
	}

	@Override
	public void closeChest() {
		target.closeChest();
	}

}
