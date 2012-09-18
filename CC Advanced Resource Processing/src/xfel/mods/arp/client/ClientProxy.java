/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.arp.client;

import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import xfel.mods.arp.common.AdvancedResourceProcessing;
import xfel.mods.arp.common.CommonProxy;
import xfel.mods.arp.common.tiles.TileDigitalAllocator;
import xfel.mods.arp.common.tiles.TileDigitalChest;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	protected void initSide() {
//		int advmachineRenderId = RenderingRegistry.getNextAvailableRenderId();
//		
//		AdvancedResourceProcessing.blockAdvancedMachine.setRenderType(advmachineRenderId);
//		
//		RenderingRegistry.registerBlockHandler(new RenderAdvancedMachine(advmachineRenderId));
		
		MinecraftForgeClient.preloadTexture("/terrain/digitalmachines.png");
//		MinecraftForgeClient.preloadTexture("terrain/digitalchest.png");
		
		TileDigitalChestRenderer tdcRenderer=new TileDigitalChestRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileDigitalChest.class, tdcRenderer);
		MinecraftForgeClient.registerItemRenderer(AdvancedResourceProcessing.blockDigitalChest.blockID, tdcRenderer);
	}
	
	@Override
	public Object getClientGuiElement(int guid, EntityPlayer player, World world,
			int x, int y, int z) {
		switch (guid) {
		case GUID_ALLOCATOR:
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			
			return new GuiDigitalAllocator(player.inventory, (TileDigitalAllocator) tile);
		}
		return null;
	}
	
	@Override
	public void getCreativeSubtypes(Item item, List<ItemStack> list) {
		item.getSubItems(item.shiftedIndex, null, list);
	}
	
}
