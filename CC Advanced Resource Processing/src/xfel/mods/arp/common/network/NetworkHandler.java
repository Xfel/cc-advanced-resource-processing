/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import xfel.mods.arp.common.AdvancedResourceProcessing;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class NetworkHandler implements IPacketHandler {

	public static final String TILE_UPDATE_CHANNEL = "xfel|tile";

	public static <TE extends TileEntity & INetworkedTile> Packet getUpdatePacket(
			TE tile) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			DataOutputStream dos = new DataOutputStream(bos);

			dos.writeInt(tile.xCoord);
			dos.writeInt(tile.yCoord);
			dos.writeInt(tile.zCoord);

			tile.writePacket(dos);
		} catch (IOException e) {
			AdvancedResourceProcessing.MOD_LOGGER.log(Level.WARNING,
					"IO error that should not have hapenend", e);
		}

		return PacketDispatcher.getPacket(TILE_UPDATE_CHANNEL,
				bos.toByteArray());
	}

	public static void updateTileEntity(byte[] data, World world) {
		DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(data));

		try {
			int x = dis.readInt();
			int y = dis.readInt();
			int z = dis.readInt();

			TileEntity tile = world.getBlockTileEntity(x, y, z);

			if (tile instanceof INetworkedTile) {
				INetworkedTile nettile = (INetworkedTile) tile;

				nettile.readPacket(dis);
			} else {
				AdvancedResourceProcessing.MOD_LOGGER.log(Level.WARNING,
						"Invalid update packet for location: (%d,%d,%d)",
						new Object[] { x, y, z });
			}

		} catch (IOException e) {
			AdvancedResourceProcessing.MOD_LOGGER.log(Level.WARNING,
					"IO error that should not have hapenend", e);
		}
	}

	@Override
	public void onPacketData(NetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals(TILE_UPDATE_CHANNEL)) {
			updateTileEntity(packet.data, ((EntityPlayer) player).worldObj);
		}
	}

}
