/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.turtle;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.arp.base.blocks.TileOrientable;
import xfel.mods.arp.base.utils.InventoryTools;
import xfel.mods.arp.base.utils.WorldCoordinate;
import xfel.mods.arp.common.peripheral.InventoryPeripheral;
import dan200.turtle.api.ITurtleAccess;

public class TurtleInventoryProvider implements
		InventoryPeripheral.IInventoryProvider {

	private static class TurtleInventory implements IInventory {

		private ITurtleAccess turtle;

		TurtleInventory(ITurtleAccess turtle) {
			this.turtle = turtle;
		}

		@Override
		public int getSizeInventory() {
			return turtle.getInventorySize();
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return turtle.getSlotContents(slot);
		}

		@Override
		public ItemStack decrStackSize(int slot, int amount) {
			ItemStack stack = turtle.getSlotContents(slot);
			ItemStack split = stack.splitStack(amount);
			if (stack.stackSize == 0) {
				turtle.setSlotContents(slot, null);
			}
			return split;
		}

		@Override
		public ItemStack getStackInSlotOnClosing(int slot) {
			ItemStack stack = turtle.getSlotContents(slot);
			turtle.setSlotContents(slot, null);
			return stack;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			turtle.setSlotContents(slot, stack);
		}

		@Override
		public String getInvName() {
			return "Turtle";
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void onInventoryChanged() {
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer var1) {
			return false;
		}

		@Override
		public void openChest() {
		}

		@Override
		public void closeChest() {
		}

	}

	public static IInventory getTurtleInventory(ITurtleAccess turtle) {
		if (turtle instanceof IInventory) {
			return (IInventory) turtle;
		}
		return new TurtleInventory(turtle);
	}

	private ITurtleAccess turtle;

	private IInventory turtleInventory;

	public TurtleInventoryProvider(ITurtleAccess turtle) {
		this.turtle = turtle;
		this.turtleInventory = getTurtleInventory(turtle);
	}

	@Override
	public IInventory getDefaultInventory() {
		return turtleInventory;
	}

	@Override
	public IInventory getKeyedInventory(String key) {
		if (key.equals("self"))
			return turtleInventory;

		ForgeDirection dir = TileOrientable.getWorldSide(key,
				ForgeDirection.getOrientation(turtle.getFacingDir()));

		return InventoryTools.getInventoryAtSide(
				new WorldCoordinate(turtle.getWorld(), turtle.getPosition()),
				dir);
	}

}
