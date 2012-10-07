/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.peripheral;

import java.util.Map;

import org.objectweb.asm.tree.analysis.SourceInterpreter;

import xfel.mods.arp.base.peripheral.bind.PeripheralMethod;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

public class InventoryPeripheral extends DatabasePeripheral {

	public static interface IInventoryProvider {
		IInventory getDefaultInventory();

		IInventory getKeyedInventory(String key);
	}

	private IInventoryProvider inventoryProvider;

	public InventoryPeripheral(IInventoryProvider provider) {
		super("inventory");
		inventoryProvider = provider;
	}

	private IInventory getInventory(String key) {
		if (key == null)
			return inventoryProvider.getDefaultInventory();
		return inventoryProvider.getKeyedInventory(key);
	}

	@PeripheralMethod
	public boolean isInventoryValid(String key) {
		return getInventory(key) != null;
	}

	@PeripheralMethod
	public int getInventorySize(String key) {
		IInventory inventory = getInventory(key);
		if (inventory == null)
			throw new IllegalArgumentException("Invalid inventory");

		return inventory.getSizeInventory();
	}
	
	@PeripheralMethod
	public String getInventoryName(String key) {
		IInventory inventory = getInventory(key);
		if (inventory == null)
			throw new IllegalArgumentException("Invalid inventory");

		return inventory.getInvName();
	}

	@PeripheralMethod
	public int getInventoryStackLimit(String key) {
		IInventory inventory = getInventory(key);
		if (inventory == null)
			throw new IllegalArgumentException("Invalid inventory");

		return inventory.getInventoryStackLimit();
	}

	@PeripheralMethod
	public Map<?, ?> getInventorySlot(String key, int slot) {
		IInventory inventory = getInventory(key);
		slot = checkSlot(inventory, slot);

		ItemStack stack = inventory.getStackInSlot(slot);

		return database.getItemStackProperties(stack);
	}

	private int checkSlot(IInventory inventory, int slot) {
		if (inventory == null)
			throw new IllegalArgumentException("Invalid inventory");

		if (slot <= 0 || slot > inventory.getSizeInventory()) {
			throw new IllegalArgumentException("Invalid inventory slot");
		}
		return slot - 1;
	}

	@PeripheralMethod
	public void swap(String key1, int slot1, String key2, int slot2) {
		IInventory inv1 = getInventory(key1);
		slot1 = checkSlot(inv1, slot1);

		IInventory inv2 = getInventory(key2);
		slot2 = checkSlot(inv2, slot2);

		if (inv1 == inv2 && slot1 == slot2)
			return;

		// TODO extract to make thread-safe
		ItemStack stack1 = inv1.getStackInSlot(slot1);
		inv1.setInventorySlotContents(slot1, inv2.getStackInSlot(slot2));
		inv2.setInventorySlotContents(slot2, stack1);
	}

	@PeripheralMethod
	public int move(String sourceKey, int sourceSlot, String targetKey,
			int targetSlot, int amount) {
		if(amount<=0)return 0;
		
		IInventory source = getInventory(sourceKey);
		sourceSlot = checkSlot(source, sourceSlot);

		IInventory target = getInventory(targetKey);
		targetSlot = checkSlot(target, targetSlot);

		if (source == target && sourceSlot == targetSlot)
			return 0;

		// TODO extract to make thread-safe
		ItemStack sourceStack = source.getStackInSlot(sourceSlot);
		if (sourceStack == null)
			return 0;
		ItemStack targetStack = target.getStackInSlot(targetSlot);

		int moveAmount;
		if (targetStack == null) {
			moveAmount = Math.min(amount, Math.min(
					target.getInventoryStackLimit(), sourceStack.stackSize));

			targetStack = source.decrStackSize(sourceSlot, moveAmount);
			target.setInventorySlotContents(targetSlot, targetStack);
		} else if (targetStack.isItemEqual(sourceStack)) {
			moveAmount = Math.min(
					Math.min(amount, sourceStack.stackSize),
					Math.min(target.getInventoryStackLimit(),
							targetStack.getMaxStackSize())
							- targetStack.stackSize);
			source.decrStackSize(sourceSlot, moveAmount);
			targetStack.stackSize+=moveAmount;
			target.onInventoryChanged();
		}else{
			throw new IllegalArgumentException("Can't merge the given item stacks");
		}
		
		return moveAmount;
	}

	public void onInventoryChanged() {
		queueSidedEvent("inventory");
	}
}
