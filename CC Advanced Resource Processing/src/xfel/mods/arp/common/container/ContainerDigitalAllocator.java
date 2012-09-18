package xfel.mods.arp.common.container;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import xfel.mods.arp.common.tiles.TileDigitalAllocator;

public class ContainerDigitalAllocator extends Container {

	private TileDigitalAllocator tile;
	private int playerInventoryStart;
	private int bufferInventoryStart;

	public ContainerDigitalAllocator(IInventory playerInventory,
			TileDigitalAllocator tile) {
		super();
		this.tile = tile;
		IInventory inv = tile.getInventory();

		for (int y = 0; y < 2; ++y) {
			for (int x = 0; x < 3; ++x) {
				this.addSlotToContainer(new SlotTaggable(inv, 9 + x + y * 3,
						8 + x * 18, 17 + y * 18));
			}
		}

		bufferInventoryStart = inventorySlots.size();
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(inv, x, 8 + x * 18, 66));
		}

		playerInventoryStart = inventorySlots.size();
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInventory,
						x + y * 9 + 9, 8 + x * 18, 97 + y * 18));
			}
		}

		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18,
					155));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if (tile.worldObj.getBlockTileEntity(tile.xCoord, tile.yCoord,
				tile.zCoord) != tile) {
			return false;
		}

		return player.getDistanceSq(tile.xCoord + 0.5D, tile.yCoord + 0.5D,
				tile.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public ItemStack transferStackInSlot(int slotId) {
		if (slotId < bufferInventoryStart) {
			return null;
		}
		ItemStack stack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();

			if (slotId < playerInventoryStart) {
				if (!this.mergeItemStack(slotStack, playerInventoryStart,
						this.inventorySlots.size(), true)) {
					return null;
				}
			} else if (!this.mergeItemStack(slotStack, bufferInventoryStart,
					playerInventoryStart, false)) {
				return null;
			}

			if (slotStack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return stack;
	}

	// @Override
	// public ItemStack transferStackInSlot(int slot) {
	// if(getSlot(slot)instanceof SlotTaggable)
	// return null;
	//
	// return super.transferStackInSlot(slot);
	// }

}
