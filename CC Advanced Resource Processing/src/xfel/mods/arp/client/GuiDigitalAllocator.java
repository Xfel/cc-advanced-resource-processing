package xfel.mods.arp.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

import xfel.mods.arp.common.container.ContainerDigitalAllocator;
import xfel.mods.arp.common.tiles.TileDigitalAllocator;

import net.minecraft.src.Container;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;
import net.minecraft.src.StatCollector;

public class GuiDigitalAllocator extends GuiContainer {

	private TileDigitalAllocator tile;
	private IInventory playerInventory;

	private GuiButton excludeModeButton;

	public GuiDigitalAllocator(IInventory playerInventory,
			TileDigitalAllocator tile) {
		super(new ContainerDigitalAllocator(playerInventory, tile));
		this.tile = tile;
		this.playerInventory = playerInventory;
		ySize = 178;
	}

	@Override
	public void initGui() {
		super.initGui();
		excludeModeButton = new GuiButton(0, guiLeft + 70, guiTop + 14, 80, 20,
				getFilterExcludeModeText());
		controlList.add(excludeModeButton);
	}

	public String getFilterExcludeModeText() {
		return tile.isFilterExcludeMode() ? "Exclude filter" : "Include filter";
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			tile.setFilterExcludeMode(!tile.isFilterExcludeMode());
			excludeModeButton.displayString = getFilterExcludeModeText();
			break;
		}
		
		PacketDispatcher.sendPacketToServer(tile.getDescriptionPacket());
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	protected void drawGuiContainerForegroundLayer() {
		this.fontRenderer.drawString(StatCollector.translateToLocal("Filter"),
				8, 6, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("Buffer"),
				8, 55, 4210752);
		this.fontRenderer.drawString(
				StatCollector.translateToLocal(playerInventory.getInvName()),
				8, 86, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	protected void drawGuiContainerBackgroundLayer(float partialTicks,
			int mouseX, int mouseY) {
		int var4 = this.mc.renderEngine.getTexture("/gui/digitalallocator.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		this.drawTexturedModalRect(x + 5 + tile.getBufferStart() * 18, y + 63,
				176, 0, 22, 22);
//		if (!tile.isBufferEmpty())
//			this.drawTexturedModalRect(x + 5 + tile.getBufferPos() * 18,
//					y + 63, 176, 22, 22, 22);
	}

}
