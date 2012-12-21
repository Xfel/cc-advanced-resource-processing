/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileExtended extends TileEntity {

	public boolean onActivation(EntityPlayer player,
			ForgeDirection orientation, float offsetX, float offsetY,
			float offsetZ) {
		return false;
	}

	public void onPlacedBy(EntityLiving entity) {
	}

	public void onDestroyed() {
	}

	@SideOnly(Side.CLIENT)
	public int getTextureFromSide(int side) {
		return 0;
	}

	public void onEntityCollided(Entity entity) {
	}

}
