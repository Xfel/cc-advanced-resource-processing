/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.utils;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryLargeChest;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class InventoryTools {

	public static IInventory getInventoryAtSide(WorldCoordinate coord,
			ForgeDirection side) {
		coord.move(side, 1);
		IInventory inv = getInventory(coord);

		if (inv instanceof ISidedInventory) {
			ISidedInventory sinv = (ISidedInventory) inv;

			ForgeDirection invertedSide = side.getOpposite();
			return new InventoryMapper(sinv,
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

}
