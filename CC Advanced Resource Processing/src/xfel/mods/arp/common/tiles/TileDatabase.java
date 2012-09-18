/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.arp.common.tiles;

import xfel.mods.arp.base.blocks.TileExtended;
import xfel.mods.arp.common.peripheral.DatabasePeripheral;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TileDatabase extends TileExtended implements IPeripheral {
	
	private DatabasePeripheral peripheral=new DatabasePeripheral();

	public void detach(IComputerAccess computer) {
		peripheral.detach(computer);
	}

	public boolean canAttachToSide(int side) {
		return peripheral.canAttachToSide(side);
	}

	public String getType() {
		return peripheral.getType();
	}

	public void attach(IComputerAccess computer, String computerSide) {
		peripheral.attach(computer, computerSide);
	}

	public final String[] getMethodNames() {
		return peripheral.getMethodNames();
	}

	public final Object[] callMethod(IComputerAccess computer, int methodId,
			Object[] arguments) throws Exception {
		return peripheral.callMethod(computer, methodId, arguments);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		peripheral.update();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getTextureFromSide(int side) {
		if(side==0||side==1){
			return 0;
		}
		return 2;
	}
	
	
}
