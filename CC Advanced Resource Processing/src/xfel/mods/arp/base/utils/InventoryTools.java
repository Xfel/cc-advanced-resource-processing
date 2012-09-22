/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.utils;

import java.util.List;

import buildcraft.api.inventory.ISpecialInventory;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryLargeChest;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class InventoryTools {

	public static IInventory getInventoryAtSide(WorldCoordinate coord,
			ForgeDirection side) {
		IInventory inv = getInventory(coord.move(side, 1));

		if (inv instanceof ISpecialInventory) {
			ISpecialInventory spi = (ISpecialInventory) inv;
			return new SpecialInventoryWrapper(spi, side.getOpposite());
		}else
		if (inv instanceof ISidedInventory) {
			ISidedInventory sinv = (ISidedInventory) inv;

			ForgeDirection invertedSide = side.getOpposite();
			return new InventoryWrapper(sinv,
					sinv.getStartInventorySide(invertedSide),
					sinv.getSizeInventorySide(invertedSide));
		}

		return inv;
	}

	public static IInventory getInventory(WorldCoordinate coord) {
		TileEntity tile = coord.getBlockTileEntity();

		if (tile instanceof TileEntityChest) {
			TileEntityChest chest = (TileEntityChest) tile;

			IInventory chest2 = null;
			if (chest.adjacentChestXNeg != null)
				chest2 = chest.adjacentChestXNeg;
			if (chest.adjacentChestXPos != null)
				chest2 = chest.adjacentChestXPos;
			if (chest.adjacentChestZNeg != null)
				chest2 = chest.adjacentChestZNeg;
			if (chest.adjacentChestZPosition != null)
				chest2 = chest.adjacentChestZPosition;
			if (chest2 != null)
				return new InventoryLargeChest("", chest, chest2);

			return chest;
		} else if (tile instanceof IInventory) {
			return (IInventory) tile;
		}

		List<IInventory> list = coord.getWorld().getEntitiesWithinAABB(
				IInventory.class, coord.getBlockBB());
		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	public static int putItemStack(IInventory target, ItemStack stack,
			boolean inverse) {
		int remaining=stack.stackSize;
		if (stack.isStackable()) {
			int slot = inverse ? target.getSizeInventory() - 1 : 0;

			while (remaining > 0
					&& (!inverse && slot < target.getSizeInventory() || inverse
							&& slot >= 0)) {
				ItemStack invStack = target.getStackInSlot(slot);

				if (invStack != null
						&& invStack.itemID == stack.itemID
						&& (!stack.getHasSubtypes() || stack.getItemDamage() == invStack
								.getItemDamage())
						&& ItemStack.func_77970_a(stack, invStack)) {
					int newStackSize = invStack.stackSize + remaining;

					if (newStackSize <= stack.getMaxStackSize()) {
						remaining = 0;
						invStack.stackSize = newStackSize;
						target.onInventoryChanged();
					} else if (invStack.stackSize < stack.getMaxStackSize()) {
						remaining -= stack.getMaxStackSize()
								- invStack.stackSize;
						invStack.stackSize = stack.getMaxStackSize();
						target.onInventoryChanged();
					}
				}

				if (inverse) {
					--slot;
				} else {
					++slot;
				}
			}
		}

		if (remaining > 0) {
			int slot = inverse ? target.getSizeInventory() - 1 : 0;

			while (!inverse && slot < target.getSizeInventory() || inverse
					&& slot >= 0) {
				ItemStack invStack = target.getStackInSlot(slot);

				if (invStack == null) {
					target.setInventorySlotContents(slot, stack.copy());
					remaining = 0;
					break;
				}

				if (inverse) {
					--slot;
				} else {
					++slot;
				}
			}
		}

		return stack.stackSize-remaining;
	}

	public static void loadInventory(IInventory inv, NBTTagList nbt) {
		for (int i = 0; i < nbt.tagCount(); i++) {
			NBTTagCompound itemTag = (NBTTagCompound) nbt.tagAt(i);
			int slot = itemTag.getByte("Slot") & 0xFF;
			if ((slot >= 0) && (slot < inv.getSizeInventory()))
				inv.setInventorySlotContents(slot,
						ItemStack.loadItemStackFromNBT(itemTag));
		}
	}

	public static NBTTagList saveInventory(IInventory inv) {
		NBTTagList nbt = new NBTTagList();

		for (int slot = 0; slot < inv.getSizeInventory(); ++slot) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) slot);
				stack.writeToNBT(itemTag);
				nbt.appendTag(itemTag);
			}
		}

		return nbt;
	}

}
