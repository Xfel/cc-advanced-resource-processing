/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.peripheral;

import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringTranslate;
import xfel.mods.arp.base.peripheral.bind.PeripheralMethod;

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

		return StringTranslate.getInstance().translateKey(
				inventory.getInvName());
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

		if (stack == null)
			return null;

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
	public int swap(String key1, int slot1, String key2, int slot2) {
		final IInventory inv1 = getInventory(key1);
		final int checkedSlot1 = checkSlot(inv1, slot1);

		final IInventory inv2 = getInventory(key2);
		final int checkedSlot2 = checkSlot(inv2, slot2);

		if (inv1 == inv2 && slot1 == slot2)
			return -1;

		return queueTask(new Task() {

			@Override
			protected Object[] execute() throws Exception {
				ItemStack stack1 = inv1.getStackInSlot(checkedSlot1);
				inv1.setInventorySlotContents(checkedSlot1,
						inv2.getStackInSlot(checkedSlot2));
				inv2.setInventorySlotContents(checkedSlot2, stack1);
				return new Object[0];
			}
		});

		// TODO extract to make thread-safe
		// ItemStack stack1 = inv1.getStackInSlot(slot1);
		// inv1.setInventorySlotContents(slot1, inv2.getStackInSlot(slot2));
		// inv2.setInventorySlotContents(slot2, stack1);
	}

	@PeripheralMethod
	public int move(String sourceKey, int sourceSlot, String targetKey,
			int targetSlot, final int amount) {
		if (amount <= 0)
			return -1;

		final IInventory source = getInventory(sourceKey);
		final int checkedSourceSlot = checkSlot(source, sourceSlot);

		final IInventory target = getInventory(targetKey);
		final int checkedTargetSlot = checkSlot(target, targetSlot);

		if (source == target && sourceSlot == targetSlot)
			return -1;

		return queueTask(new Task() {

			@Override
			protected Object[] execute() throws Exception {

				// TODO extract to make thread-safe
				ItemStack sourceStack = source
						.getStackInSlot(checkedSourceSlot);
				if (sourceStack == null)
					return new Object[]{0};
				ItemStack targetStack = target
						.getStackInSlot(checkedTargetSlot);

				int moveAmount;
				if (targetStack == null) {
					moveAmount = Math.min(amount, Math.min(
							target.getInventoryStackLimit(),
							sourceStack.stackSize));

					targetStack = source.decrStackSize(checkedSourceSlot,
							moveAmount);
					target.setInventorySlotContents(checkedTargetSlot,
							targetStack);
				} else if (targetStack.isItemEqual(sourceStack)) {
					moveAmount = Math.min(
							Math.min(amount, sourceStack.stackSize),
							Math.min(target.getInventoryStackLimit(),
									targetStack.getMaxStackSize())
									- targetStack.stackSize);
					source.decrStackSize(checkedSourceSlot, moveAmount);
					targetStack.stackSize += moveAmount;
					target.onInventoryChanged();
				} else {
					throw new IllegalArgumentException(
							"Can't merge the given item stacks");
				}

				return new Object[] { moveAmount };
			}
		});

	}

	public void onInventoryChanged() {
		queueSidedEvent("inventory");
	}
}
