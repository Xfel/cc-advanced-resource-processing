/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.base.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockExtended extends BlockContainer {

	protected BlockExtended(int id, Material material) {
		super(id, material);
		isBlockContainer=true;
	}
	
	@Deprecated
	@Override
	public TileEntity createNewTileEntity(World world) {
		// this method should not be called, as it is not metadata sensitive.
		return createTileEntity(world, 0);
	}
	
	@Override
	public abstract TileEntity createNewTileEntity(World world, int metadata);
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockTexture(IBlockAccess iba, int x, int y, int z, int side) {
		TileEntity te = iba.getBlockTileEntity(x, y, z);

		if (te instanceof TileExtended) {
			TileExtended tx = (TileExtended) te;

			return tx.getTextureFromSide(side);
		}

		return getBlockTextureFromSideAndMetadata(side,
				iba.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int side, float offsetX, float offsetY,
			float offsetZ) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileExtended) {
			TileExtended tx = (TileExtended) te;

			return tx.onActivation(player, ForgeDirection.getOrientation(side),
					offsetX, offsetY, offsetZ);
		}

		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving player) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileExtended) {
			TileExtended tx = (TileExtended) te;

			tx.onPlacedBy(player);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockId,
			int blockMetadata) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileExtended) {
			TileExtended tx = (TileExtended) te;

			tx.onDestroyed();
		}
		
		world.removeBlockTileEntity(x, y, z);
	}

}
