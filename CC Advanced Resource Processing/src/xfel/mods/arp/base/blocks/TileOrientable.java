/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.blocks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.arp.common.network.INetworkedTile;
import xfel.mods.arp.common.network.NetworkHandler;

public class TileOrientable extends TileExtended implements INetworkedTile {

	public static ForgeDirection getWorldSide(String localSide,
			ForgeDirection facing) {
		if ("top".equals(localSide))
			return ForgeDirection.UP;
		if ("bottom".equals(localSide))
			return ForgeDirection.DOWN;

		ForgeDirection front = facing;
		ForgeDirection back;
		ForgeDirection left;
		ForgeDirection right;
		switch (front) {
		case NORTH:
			back = ForgeDirection.SOUTH;
			left = ForgeDirection.WEST;
			right = ForgeDirection.EAST;
			break;
		case SOUTH:
			back = ForgeDirection.NORTH;
			left = ForgeDirection.EAST;
			right = ForgeDirection.WEST;
			break;
		case WEST:
			back = ForgeDirection.EAST;
			left = ForgeDirection.SOUTH;
			right = ForgeDirection.NORTH;
			break;
		case EAST:
			back = ForgeDirection.WEST;
			left = ForgeDirection.NORTH;
			right = ForgeDirection.SOUTH;
			break;
		default:
			throw new IllegalArgumentException("Invalid side");
		}

		if ("front".equals(localSide))
			return front;
		if ("back".equals(localSide))
			return back;
		if ("left".equals(localSide))
			return left;
		if ("right".equals(localSide))
			return right;
		throw new IllegalArgumentException("Invalid side");
	}

	public static String getLocalSide(ForgeDirection worldSide,
			ForgeDirection facing) {
		if (worldSide == ForgeDirection.UP)
			return "top";
		if (worldSide == ForgeDirection.DOWN)
			return "bottom";

		ForgeDirection front = facing;
		ForgeDirection back;
		ForgeDirection left;
		ForgeDirection right;
		switch (front) {
		case NORTH:
			back = ForgeDirection.SOUTH;
			left = ForgeDirection.WEST;
			right = ForgeDirection.EAST;
			break;
		case SOUTH:
			back = ForgeDirection.NORTH;
			left = ForgeDirection.EAST;
			right = ForgeDirection.WEST;
			break;
		case WEST:
			back = ForgeDirection.EAST;
			left = ForgeDirection.SOUTH;
			right = ForgeDirection.NORTH;
			break;
		case EAST:
			back = ForgeDirection.WEST;
			left = ForgeDirection.NORTH;
			right = ForgeDirection.SOUTH;
			break;
		default:
			throw new IllegalArgumentException("Invalid side");
		}

		if (worldSide == front)
			return "front";
		if (worldSide == back)
			return "back";
		if (worldSide == left)
			return "left";
		if (worldSide == right)
			return "right";
		throw new IllegalArgumentException("Invalid side");
	}

	public static ForgeDirection determineOrientation(int blockX, int blockY,
			int blockZ, EntityLiving entity, boolean canFaceVertical) {
		if (canFaceVertical
				&& MathHelper.abs((float) entity.posX - (float) blockX) < 2.0F
				&& MathHelper.abs((float) entity.posZ - (float) blockZ) < 2.0F) {
			double entityY = (entity.posY + 1.8200000000000001D)
					- (double) entity.height;

			if (entityY - (double) blockY > 2D) {
				return ForgeDirection.UP;
			}

			if ((double) blockY - entityY > 0.0D) {
				return ForgeDirection.DOWN;
			}
		}

		int irot = MathHelper
				.floor_double((double) ((entity.rotationYaw * 4F) / 360F) + 0.5D) & 3;

		switch (irot) {
		case 0:
			return ForgeDirection.NORTH;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.SOUTH;
		case 3:
			return ForgeDirection.WEST;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}

	private ForgeDirection orientation=ForgeDirection.UNKNOWN;

	public ForgeDirection getOrientation() {
		return orientation;
	}

	public void setOrientation(ForgeDirection orientation) {
		if (this.orientation == orientation)
			return;
		this.orientation = orientation;

		requestNetworkUpdate();
	}

	protected void requestNetworkUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void onPlacedBy(EntityLiving entity) {
		setOrientation(determineOrientation(xCoord, yCoord, zCoord, entity, true));
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		orientation=ForgeDirection.getOrientation(nbt.getByte("Orientation"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setByte("Orientation", (byte) orientation.ordinal());
	}
	
	@Override
	public void readPacket(DataInput in) throws IOException {
		orientation=ForgeDirection.getOrientation(in.readUnsignedByte());
	}

	@Override
	public void writePacket(DataOutput out) throws IOException {
		out.writeByte(orientation.ordinal());
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return NetworkHandler.getUpdatePacket(this);
	}

}
