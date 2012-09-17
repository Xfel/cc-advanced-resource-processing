package xfel.mods.arp.common.blocks;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import xfel.mods.arp.common.network.INetworkedTile;
import xfel.mods.arp.common.network.NetworkHandler;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraftforge.common.ForgeDirection;

public class TileOrientable extends TileExtended implements INetworkedTile {

	public static ForgeDirection determineOrientation(int x, int y, int z,
			EntityLiving entity) {
		if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F
				&& MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
			double ypos = entity.posY + 1.82D - (double) entity.yOffset;

			if (ypos - (double) y > 2.0D) {
				return ForgeDirection.UP;
			}

			if ((double) y - ypos > 0.0D) {
				return ForgeDirection.DOWN;
			}
		}

		int dep = MathHelper
				.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		switch (dep) {
		case 0:
			return ForgeDirection.NORTH;
		case 1:
			return ForgeDirection.EAST;
		case 2:
			return ForgeDirection.SOUTH;
		case 3:
			return ForgeDirection.WEST;
		}
		return ForgeDirection.UNKNOWN;
	}

	private ForgeDirection orientation;

	public ForgeDirection getOrientation() {
		return orientation;
	}

	public void setOrientation(ForgeDirection orientation) {
		if (this.orientation == orientation)
			return;
		this.orientation = orientation;

		worldObj.markBlockNeedsUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void onPlacedBy(EntityLiving entity) {
		setOrientation(determineOrientation(xCoord, yCoord, zCoord, entity));
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
	public Packet getAuxillaryInfoPacket() {
		return NetworkHandler.getUpdatePacket(this);
	}

}
