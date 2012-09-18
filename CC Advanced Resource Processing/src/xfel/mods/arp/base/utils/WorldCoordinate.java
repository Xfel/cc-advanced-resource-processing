/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.utils;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;

public class WorldCoordinate {

	private World world;

	private int x;

	private int y;

	private int z;

	public WorldCoordinate(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldCoordinate(World world, Vec3 vec) {
		this.world = world;
		this.x = (int) vec.xCoord;
		this.y = (int) vec.yCoord;
		this.z = (int) vec.zCoord;
	}

	public WorldCoordinate(TileEntity te) {
		this.world = te.worldObj;
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
	}

	public World getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public WorldCoordinate move(ForgeDirection dir, int dist) {
		return new WorldCoordinate(world, x + dist * dir.offsetX, y + dist
				* dir.offsetY, z + dist * dir.offsetZ);
	}

	public int getBlockId() {
		return world.getBlockId(x, y, z);
	}

	public int getBlockMetadata() {
		return world.getBlockMetadata(x, y, z);
	}

	public TileEntity getBlockTileEntity() {
		return world.getBlockTileEntity(x, y, z);
	}

	public <T> T getBlockTileEntity(Class<T> constraint) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (constraint.isInstance(tile))
			return (T) tile;

		return null;
	}

	public AxisAlignedBB getBlockBB() {
		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(x, y, z,
				x + 1, y + 1, z + 1);
	}

}
