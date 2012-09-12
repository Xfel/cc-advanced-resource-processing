/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common.peripheral;

public class CommandManager {
	
	public static abstract class Command{
		
		int commandId;
		
		protected abstract Object[] execute() throws Exception;
		
		protected int getCommandId() {
			return commandId;
		}
	}
	
}
