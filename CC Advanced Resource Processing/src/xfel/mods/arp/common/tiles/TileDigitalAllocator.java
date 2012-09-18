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
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.arp.base.blocks.TileOrientable;
import xfel.mods.arp.common.AdvancedResourceProcessing;
import xfel.mods.arp.common.CommonProxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class TileDigitalAllocator extends TileOrientable {

	public static final int FILTER_SIZE = 6;
	public static final int BUFFER_SIZE = 9;

	private InventoryBasic invobj = new InventoryBasic("Digital Allocator",
			FILTER_SIZE + BUFFER_SIZE);

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
	private boolean open;

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
		for (int i = 0; i < FILTER_SIZE; i++) {
			ItemStack filterElem = invobj.getStackInSlot(BUFFER_SIZE + i);

			if (filterElem != null && filterElem.isItemEqual(item)) {
				return !filterExcludeMode;
			}
		}
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
	private int bufferPos;

	public int getBufferStart() {
		return bufferStart;
	}
	
	public int getBufferPos() {
		return bufferPos;
	}
	
	public boolean isBufferFull() {
		int np = bufferPos + 1 % BUFFER_SIZE;
		return np == bufferStart;
	}

	public boolean isBufferEmpty() {
		return bufferPos == bufferStart;
	}

	protected boolean requeue(ItemStack item) {
		int np = bufferStart - 1 % BUFFER_SIZE;
		if (np == bufferPos)
			return false;
		invobj.setInventorySlotContents(np, item);
		bufferStart = np;
		requestNetworkUpdate();
		return true;
	}

	protected boolean offer(ItemStack item) {
		int np = bufferPos + 1 % BUFFER_SIZE;
		if (np == bufferStart)
			return false;
		invobj.setInventorySlotContents(np, item);
		bufferPos = np;
		requestNetworkUpdate();
		return true;
	}

	protected ItemStack poll() {
		if (bufferPos == bufferStart)
			return null;

		ItemStack result;
		do {
			result = invobj.getStackInSlotOnClosing(bufferStart);
			bufferStart++;
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

		bufferPos = in.readUnsignedByte();
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

		out.writeByte(bufferPos);
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

		bufferPos = nbt.getByte("BufferPos") & 0xff;
		bufferStart = nbt.getByte("BufferStart") & 0xff;
		
		for (int slot = 0; slot < invobj.getSizeInventory(); ++slot)
        {
			invobj.setInventorySlotContents(slot, null);
        }

		NBTTagList itemList = nbt.getTagList("Items");
		
		for (int i = 0; i < itemList.tagCount(); ++i) {
			NBTTagCompound itemTag = (NBTTagCompound) itemList.tagAt(i);
			int slot = itemTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < invobj.getInventoryStackLimit()) {
				invobj.setInventorySlotContents(slot,  ItemStack.loadItemStackFromNBT(itemTag));
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

		nbt.setByte("BufferPos", (byte) bufferPos);
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
	public boolean onActivation(EntityPlayer player, ForgeDirection side, float offsetX,
			float offsetY, float offsetZ) {
		player.openGui(AdvancedResourceProcessing.instance, CommonProxy.GUID_ALLOCATOR, worldObj, xCoord, yCoord, zCoord);
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
		if (entity instanceof EntityItem&&!isBufferFull()) {
			EntityItem item = (EntityItem) entity;
			
			offer(item.item);
			item.setDead();
		}
	}
	
	@Override
	public void updateEntity() {
		if(!isBufferEmpty()){
			
		}
	}
}
