/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.arp.common.tiles;

import net.minecraft.inventory.IInventory;
import xfel.mods.arp.base.blocks.TileOrientable;
import xfel.mods.arp.base.utils.InventoryTools;
import xfel.mods.arp.base.utils.WorldCoordinate;
import xfel.mods.arp.common.peripheral.InventoryPeripheral;
import xfel.mods.arp.common.peripheral.InventoryPeripheral.IInventoryProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TileInventoryInterface extends TileOrientable implements
		IPeripheral, IInventoryProvider {

	private InventoryPeripheral peripheral = new InventoryPeripheral(this);

	@Override
	public IInventory getDefaultInventory() {
		return InventoryTools.getInventoryAtSide(new WorldCoordinate(this), getOrientation());
	}

	@Override
	public IInventory getKeyedInventory(String key) {
		return null;
	}

	public String getType() {
		return peripheral.getType();
	}

	public String[] getMethodNames() {
		return peripheral.getMethodNames();
	}

	public Object[] callMethod(IComputerAccess computer, int method,
			Object[] arguments) throws Exception {
		return peripheral.callMethod(computer, method, arguments);
	}

	public boolean canAttachToSide(int side) {
		return peripheral.canAttachToSide(side);
	}

	public void attach(IComputerAccess computer) {
		peripheral.attach(computer);
	}

	public void detach(IComputerAccess computer) {
		peripheral.detach(computer);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		peripheral.update();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getTextureFromSide(int side) {
		if (side == getOrientation().ordinal()) {
			if (side == 0 || side == 1) {
				return 4;
			}
			return 3;
		}
		if (side == 0 || side == 1) {
			return 0;
		}
		return 1;
	}

}
