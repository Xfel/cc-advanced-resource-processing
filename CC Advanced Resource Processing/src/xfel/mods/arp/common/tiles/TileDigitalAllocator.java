package xfel.mods.arp.common.tiles;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.arp.base.blocks.TileOrientable;
import xfel.mods.arp.base.utils.InventoryTools;
import xfel.mods.arp.base.utils.WorldCoordinate;
import xfel.mods.arp.common.AdvancedResourceProcessing;
import xfel.mods.arp.common.CommonProxy;

import buildcraft.api.core.Orientations;
import buildcraft.api.inventory.ISpecialInventory;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeEntry;
import buildcraft.api.transport.IPipedItem;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class TileDigitalAllocator extends TileOrientable implements
		ISpecialInventory, IPipeConnection {

	public static final int FILTER_SIZE = 6;
	public static final int BUFFER_SIZE = 9;

	private InventoryBasic invobj = new InventoryBasic("Digital Allocator",
			FILTER_SIZE + BUFFER_SIZE);

	@Override
	@SideOnly(Side.CLIENT)
	public int getTextureFromSide(int side) {
		int backStateShift;
		int frontStateShift;

		if (!this.active) {
			frontStateShift = 8;
			backStateShift = 8;
		} else {
			if (reverseMode) {
				frontStateShift = 4;
				if (isBufferEmpty())
					backStateShift = 4;
				else
					backStateShift = 8;
			} else {
				if (isBufferEmpty())
					frontStateShift = 0;
				else {
					frontStateShift = 8;
				}
				backStateShift = 0;
			}
		}
		if (side == getInputSide().ordinal()) {
			if ((side == 1) || (side == 0)) {
				return 11 + frontStateShift;
			}
			return 10 + frontStateShift;
		}
		if (side == getOutputSide().ordinal()) {
			if ((side == 1) || (side == 0)) {
				return 9 + backStateShift;
			}
			return 8 + backStateShift;
		}
		if ((side == 1) || (side == 0)) {
			return 0;
		}
		return 1;
	}

	// speed control
	private int progress;

	private int speed;

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getProgress() {
		return progress;
	}

	protected boolean incrementProgress() {
		progress++;
		return progress >= speed;
	}

	// access control
	private boolean open = true;

	private boolean active;

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
		requestNetworkUpdate();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		requestNetworkUpdate();
	}

	// direction control
	private boolean reverseMode;

	public boolean isReverseMode() {
		return reverseMode;
	}

	public void setReverseMode(boolean reverseMode) {
		if (this.reverseMode == reverseMode)
			return;
		this.reverseMode = reverseMode;
		requestNetworkUpdate();
	}

	public ForgeDirection getInputSide() {
		if (reverseMode) {
			return getOrientation().getOpposite();
		}
		return getOrientation();
	}

	public ForgeDirection getOutputSide() {
		if (reverseMode) {
			return getOrientation();
		}
		return getOrientation().getOpposite();
	}

	// filter control
	private boolean filterExcludeMode;

	public boolean isFilterExcludeMode() {
		return filterExcludeMode;
	}

	public void setFilterExcludeMode(boolean filterExcludeMode) {
		if (this.filterExcludeMode == filterExcludeMode)
			return;
		this.filterExcludeMode = filterExcludeMode;
		requestNetworkUpdate();
	}

	public boolean isAcceptedItem(ItemStack item) {
		boolean filterEmpty = true;
		for (int i = 0; i < FILTER_SIZE; i++) {
			ItemStack filterElem = invobj.getStackInSlot(BUFFER_SIZE + i);

			if (filterElem != null) {
				if (filterElem.isItemEqual(item)) {
					return !filterExcludeMode;
				}
				filterEmpty = false;
			}
		}
		if (filterEmpty)
			return true;

		return filterExcludeMode;
	}

	public ItemStack findAcceptedItem(IInventory inventory) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			ItemStack invstack = inventory.getStackInSlot(slot);

			boolean wasEqual = false;
			for (int i = 0; i < FILTER_SIZE; i++) {
				ItemStack filterElem = invobj.getStackInSlot(BUFFER_SIZE + i);
				if (filterElem != null && filterElem.isItemEqual(invstack)) {
					if (filterExcludeMode) {
						wasEqual = true;
						break;
					}

					int moveAmount = filterElem.stackSize;

					return inventory.decrStackSize(slot, moveAmount);
				}
			}
			if (!wasEqual && filterExcludeMode) {
				return inventory.decrStackSize(slot, 1);
			}
		}

		return null;
	}

	// buffer control
	private int bufferStart;

	// private int bufferPos;

	public int getBufferStart() {
		return bufferStart % BUFFER_SIZE;
	}

	// public int getBufferPos() {
	// return bufferPos % BUFFER_SIZE;
	// }

	// public boolean isBufferFull() {
	// int np = bufferPos + 1 % BUFFER_SIZE;
	// return np == bufferStart;
	// }
	//
	// public boolean isBufferEmpty() {
	// return bufferPos == bufferStart;
	// }
	public boolean isBufferEmpty() {
		for (int i = 0; i < BUFFER_SIZE; i++) {
			if (invobj.getStackInSlot(i) != null) {
				return true;
			}
		}
		return false;
	}

	// protected boolean requeue(ItemStack item) {
	// int np = bufferStart - 1 % BUFFER_SIZE;
	// if (np == bufferPos)
	// return false;
	// invobj.setInventorySlotContents(np, item);
	// bufferStart = np;
	// requestNetworkUpdate();
	// return true;
	// }

	protected int offer(ItemStack item, boolean doAdd) {
		int remaining = item.stackSize;
		for (int i = 0; i < BUFFER_SIZE && remaining > 0; i++) {
			int slotIndex = (i + bufferStart) % BUFFER_SIZE;

			ItemStack slotContents = invobj.getStackInSlot(slotIndex);
			if (slotContents == null) {
				if (doAdd)
					invobj.setInventorySlotContents(slotIndex, item);
				remaining = 0;
				break;
			} else if (item.isStackable() && slotContents.isItemEqual(item)) {
				int amount = Math.min(remaining, slotContents.getMaxStackSize()
						- slotContents.stackSize);
				if (doAdd)
					slotContents.stackSize += amount;
				remaining -= amount;
			}
		}
		if (doAdd) {
			onInventoryChanged();
			requestNetworkUpdate();
		}
		return item.stackSize - remaining;
		// int np = bufferPos + 1 % BUFFER_SIZE;
		// if (np == bufferStart)
		// return false;
		// invobj.setInventorySlotContents(np, item);
		// bufferPos = np;
		// requestNetworkUpdate();
		// return true;
	}

	protected int peekIndex() {
		int bufferStart = this.bufferStart;

		ItemStack result;
		do {
			result = invobj.getStackInSlot(bufferStart);
			bufferStart++;
			bufferStart %= BUFFER_SIZE;
		} while (result == null && this.bufferStart != bufferStart);

		requestNetworkUpdate();
		return result == null ? -1 : bufferStart - 1;
	}

	protected ItemStack poll() {
		int bufferPos = bufferStart;

		ItemStack result;
		do {
			result = invobj.getStackInSlotOnClosing(bufferStart);
			bufferStart++;
			bufferStart %= BUFFER_SIZE;
		} while (result == null && bufferPos != bufferStart);

		requestNetworkUpdate();
		return result;
	}

	// storage/network control

	@Override
	public void readPacket(DataInput in) throws IOException {
		super.readPacket(in);

		int flags = in.readUnsignedByte();
		active = (flags & 0x01) != 0;
		open = (flags & 0x02) != 0;
		reverseMode = (flags & 0x04) != 0;
		filterExcludeMode = (flags & 0x08) != 0;

		speed = in.readUnsignedShort();
		progress = in.readUnsignedShort();

		// bufferPos = in.readUnsignedByte();
		bufferStart = in.readUnsignedByte();
	}

	@Override
	public void writePacket(DataOutput out) throws IOException {
		super.writePacket(out);

		int flags = 0;
		if (active) {
			flags |= 0x01;
		}
		if (open) {
			flags |= 0x02;
		}
		if (reverseMode) {
			flags |= 0x04;
		}
		if (filterExcludeMode) {
			flags |= 0x08;
		}
		out.writeByte(flags);

		out.writeShort(speed);
		out.writeShort(progress);

		// out.writeByte(bufferPos);
		out.writeByte(bufferStart);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		int flags = nbt.getByte("Flags");
		active = (flags & 0x01) != 0;
		open = (flags & 0x02) != 0;
		reverseMode = (flags & 0x04) != 0;
		filterExcludeMode = (flags & 0x08) != 0;

		speed = nbt.getShort("Speed") & 0xffff;
		progress = nbt.getShort("Progress") & 0xffff;

		// bufferPos = nbt.getByte("BufferPos") & 0xff;
		bufferStart = nbt.getByte("BufferStart") & 0xff;

		for (int slot = 0; slot < invobj.getSizeInventory(); ++slot) {
			invobj.setInventorySlotContents(slot, null);
		}

		NBTTagList itemList = nbt.getTagList("Items");

		for (int i = 0; i < itemList.tagCount(); ++i) {
			NBTTagCompound itemTag = (NBTTagCompound) itemList.tagAt(i);
			int slot = itemTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < invobj.getInventoryStackLimit()) {
				invobj.setInventorySlotContents(slot,
						ItemStack.loadItemStackFromNBT(itemTag));
			}
		}

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		int flags = 0;
		if (active) {
			flags |= 0x01;
		}
		if (open) {
			flags |= 0x02;
		}
		if (reverseMode) {
			flags |= 0x04;
		}
		if (filterExcludeMode) {
			flags |= 0x08;
		}
		nbt.setByte("Flags", (byte) flags);

		nbt.setShort("Speed", (short) speed);
		nbt.setShort("Progress", (short) progress);

		// nbt.setByte("BufferPos", (byte) bufferPos);
		nbt.setByte("BufferStart", (byte) bufferStart);

		NBTTagList itemList = new NBTTagList();

		for (int slot = 0; slot < invobj.getSizeInventory(); ++slot) {
			ItemStack stack = invobj.getStackInSlot(slot);
			if (stack != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) slot);
				stack.writeToNBT(itemTag);
				itemList.appendTag(itemTag);
			}
		}

		nbt.setTag("Items", itemList);
	}

	// gui code

	@Override
	public boolean onActivation(EntityPlayer player, ForgeDirection side,
			float offsetX, float offsetY, float offsetZ) {
		player.openGui(AdvancedResourceProcessing.instance,
				CommonProxy.GUID_ALLOCATOR, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	public IInventory getInventory() {
		return invobj;
	}

	@Override
	public void onDestroyed() {
		Random rnd = new Random();

		for (int slot = 0; slot < BUFFER_SIZE; ++slot) {
			ItemStack stack = invobj.getStackInSlot(slot);

			if (stack != null) {
				float xOffset = rnd.nextFloat() * 0.8F + 0.1F;
				float yOffset = rnd.nextFloat() * 0.8F + 0.1F;
				float zOffset = rnd.nextFloat() * 0.8F + 0.1F;

				while (stack.stackSize > 0) {
					int dropSize = rnd.nextInt(21) + 10;

					if (dropSize > stack.stackSize) {
						dropSize = stack.stackSize;
					}

					stack.stackSize -= dropSize;
					EntityItem droppedEntity = new EntityItem(worldObj, xCoord
							+ xOffset, yCoord + yOffset, zCoord + zOffset,
							new ItemStack(stack.itemID, dropSize,
									stack.getItemDamage()));

					if (stack.hasTagCompound()) {
						droppedEntity.item
								.setTagCompound((NBTTagCompound) stack
										.getTagCompound().copy());
					}

					droppedEntity.motionX = ((float) rnd.nextGaussian() * 0.05F);
					droppedEntity.motionY = ((float) rnd.nextGaussian() * 0.05F + 0.2F);
					droppedEntity.motionZ = ((float) rnd.nextGaussian() * 0.05F);
					worldObj.spawnEntityInWorld(droppedEntity);
				}
			}
		}
	}

	// core logic
	@Override
	public void onEntityCollided(Entity entity) {
		if (entity instanceof EntityItem && isOpen()) {
			EntityItem item = (EntityItem) entity;

			if (!isAcceptedItem(item.item)) {
				return;
			}

			if (offer(item.item, true) == item.item.stackSize)
				item.setDead();
		}
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;

		int peekIdx = peekIndex();
		if (peekIdx != -1) {
			ItemStack stack = invobj.getStackInSlot(peekIdx);

			WorldCoordinate wc = new WorldCoordinate(this);

			WorldCoordinate move = wc.move(getOutputSide(), 1);
			TileEntity destTile = move.getBlockTileEntity();
			if (destTile instanceof IPipeEntry
					&& ((IPipeEntry) destTile).acceptItems()) {
				((IPipeEntry) destTile).entityEntering(stack.copy(),
						Orientations.values()[getOutputSide().ordinal()]);
				stack.stackSize = 0;
			} else {
				IInventory inv = InventoryTools.getInventoryAtSide(wc,
						getOutputSide());
				if (inv instanceof ISpecialInventory) {
					ISpecialInventory isi = (ISpecialInventory) inv;
					stack.stackSize -= isi.addItem(stack, true, Orientations
							.values()[getOutputSide().getOpposite().ordinal()]);
				} else if (inv != null) {
					stack.stackSize -= InventoryTools.putItemStack(inv, stack,
							false);
				} else if (move.isEmpty()) {
					float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
					float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
					float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;

					EntityItem entityitem = new EntityItem(worldObj, xCoord + f
							+ getOutputSide().offsetX * 0.5f, yCoord + f1
							+ getOutputSide().offsetY * 0.5f, zCoord + f2
							+ getOutputSide().offsetZ * 0.5f, stack.copy());

					float f3 = 0.05F;
					entityitem.motionX = (float) worldObj.rand.nextGaussian()
							* f3+ getOutputSide().offsetX;
					entityitem.motionY = (float) worldObj.rand.nextGaussian()
							* f3 + getOutputSide().offsetY;
					entityitem.motionZ = (float) worldObj.rand.nextGaussian()
							* f3+ getOutputSide().offsetZ;
					entityitem.delayBeforeCanPickup=20;
					worldObj.spawnEntityInWorld(entityitem);
					stack.stackSize = 0;
				}
			}
			if (stack.stackSize == 0) {
				invobj.setInventorySlotContents(peekIdx, null);
				bufferStart = (peekIdx + 1) % BUFFER_SIZE;
			} else {
				bufferStart = peekIdx;
			}
			onInventoryChanged();
			requestNetworkUpdate();
		}
	}

	// inventory implementation

	public ItemStack getStackInSlot(int slot) {
		return invobj.getStackInSlot((slot + bufferStart) % BUFFER_SIZE);
	}

	public ItemStack decrStackSize(int slot, int amount) {
		return invobj.decrStackSize((slot + bufferStart) % BUFFER_SIZE, amount);
	}

	public ItemStack getStackInSlotOnClosing(int slot) {
		return invobj.getStackInSlotOnClosing((slot + bufferStart)
				% BUFFER_SIZE);
	}

	public void setInventorySlotContents(int slot, ItemStack stack) {
		invobj.setInventorySlotContents((slot + bufferStart) % BUFFER_SIZE,
				stack);
	}

	public int getSizeInventory() {
		return BUFFER_SIZE;
	}

	public String getInvName() {
		return "inventory.allocator";
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord,
				this.zCoord) != this) {
			return false;
		}

		return player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D,
				this.zCoord + 0.5D) <= 64.0D;
	}

	public void openChest() {
	}

	public void closeChest() {
	}

	// buildcraft interop

	@Override
	public boolean isPipeConnected(Orientations with) {
		ForgeDirection dir = with.toDirection();
		return dir == getInputSide() || dir == getOutputSide();
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, Orientations from) {
		if (!isOpen() || from.toDirection() != getInputSide()
				|| !isAcceptedItem(stack))
			return 0;

		int amount = offer(stack, doAdd);
		return amount;
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, Orientations from,
			int maxItemCount) {
		return new ItemStack[0];
	}
}
