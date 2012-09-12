/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface INetworkedTile {
	void readPacket(DataInput in) throws IOException;

	void writePacket(DataOutput out) throws IOException;
}
