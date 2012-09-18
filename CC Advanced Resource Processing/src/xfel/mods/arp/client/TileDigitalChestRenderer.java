/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package xfel.mods.arp.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import xfel.mods.arp.common.tiles.TileDigitalChest;

import net.minecraft.src.ItemStack;
import net.minecraft.src.ModelChest;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySpecialRenderer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeDirection;

public class TileDigitalChestRenderer extends TileEntitySpecialRenderer
		implements IItemRenderer {

	private ModelChest chestModel = new ModelChest();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y,
			double z, float partialTicks) {

		if (tile instanceof TileDigitalChest) {
			TileDigitalChest tdc = (TileDigitalChest) tile;
			GL11.glTranslated(x, y, z);
			renderDigitalChest(tdc.getOrientation(),
					tdc.getRenderLidAngle(partialTicks));
			GL11.glTranslated(-x, -y, -z);
		}
	}

	public void renderDigitalChest(ForgeDirection direction, float lidAngle) {

		float rotation = 0;

		if (direction == ForgeDirection.NORTH) {
			rotation = 180;
		}

		if (direction == ForgeDirection.SOUTH) {
			rotation = 0;
		}

		if (direction == ForgeDirection.WEST) {
			rotation = 90;
		}

		if (direction == ForgeDirection.EAST) {
			rotation = -90;
		}

		ModelChest model = this.chestModel;

		lidAngle = 1.0F - lidAngle;
		lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
		model.chestLid.rotateAngleX = -(lidAngle * (float) Math.PI / 2.0F);

		this.bindTextureByName("/terrain/digitalchest.png");

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glTranslatef(0, 1.0F, 1.0F);
		GL11.glScalef(1.0F, -1.0F, -1.0F);

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		model.renderAll();

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
		case ENTITY:
		case EQUIPPED:
		case INVENTORY:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		renderDigitalChest(ForgeDirection.NORTH, 0);
	}

}
