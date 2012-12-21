/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.common;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import xfel.mods.arp.common.container.ContainerDigitalAllocator;
import xfel.mods.arp.common.tiles.TileDigitalAllocator;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler{
	
	public static final String TERRAIN_TEXTURES="/terrain/arp.png";
	public static final int GUID_ALLOCATOR = 101;
	
	protected void initSide(){
		
	}

	@Override
	public Object getServerGuiElement(int guid, EntityPlayer player, World world,
			int x, int y, int z) {
		switch (guid) {
		case GUID_ALLOCATOR:
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			
			return new ContainerDigitalAllocator(player.inventory, (TileDigitalAllocator) tile);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// implementd on client side
		return null;
	}

	public void getCreativeSubtypes(Item item, List<ItemStack> list) {
		// only on client
	}
	
}
