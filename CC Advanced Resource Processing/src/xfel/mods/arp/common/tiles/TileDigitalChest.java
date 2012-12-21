/** 
 * Copyright (c) Xfel, 2012
 * 
 * This file is distributed under the terms of the Minecraft Mod Public 
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */package xfel.mods.arp.common.tiles;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.ForgeDirection;
import xfel.mods.arp.base.blocks.TileOrientable;
import xfel.mods.arp.base.utils.InventoryTools;
import xfel.mods.arp.base.utils.WorldCoordinate;
import xfel.mods.arp.common.AdvancedResourceProcessing;
import xfel.mods.arp.common.peripheral.InventoryPeripheral;
import xfel.mods.arp.common.peripheral.InventoryPeripheral.IInventoryProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

public class TileDigitalChest extends TileOrientable implements IPeripheral,
		IInventoryProvider, IInventory {

	private static final int INVENTORY_STACK_LIMIT = 64;

	private static final int INVENTORY_SIZE = 27;

	private ItemStack[] contents = new ItemStack[INVENTORY_SIZE];

	private InventoryPeripheral peripheral = new InventoryPeripheral(this);

	/** The current angle of the lid (between 0 and 1) */
	private float lidAngle;

	/** The angle of the lid last tick */
	private float prevLidAngle;

	/** The number of players currently using this chest */
	private int numUsingPlayers;

	/** Server sync counter (once per 20 ticks) */
	private int ticksSinceSync;

	private boolean migrating;

	// inventory provider functions
	@Override
	public IInventory getDefaultInventory() {
		return this;
	}

	@Override
	public IInventory getKeyedInventory(String key) {
		if(key.equals("self"))
			return this;
		
		ForgeDirection dir = getWorldSide(key, getOrientation());

		return InventoryTools.getInventoryAtSide(new WorldCoordinate(this), dir);
	}

	// peripheral functions

	public String getType() {
		return peripheral.getType();
	}

	public String[] getMethodNames() {
		return peripheral.getMethodNames();
	}

	public Object[] callMethod(IComputerAccess computer, int method,
			Object[] arguments) throws Exception {
		return peripheral.callMethod(computer, method, arguments);
	}

	public boolean canAttachToSide(int side) {
		return peripheral.canAttachToSide(side);
	}

	public void attach(IComputerAccess computer) {
		peripheral.attach(computer);
	}

	public void detach(IComputerAccess computer) {
		peripheral.detach(computer);
	}

	@Override
	public void onInventoryChanged() {
		super.onInventoryChanged();
		peripheral.onInventoryChanged();
	}

	// Inventory functions
	/**
	 * Reads a tile entity from NBT.
	 */
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		NBTTagList itemList = nbt.getTagList("Items");
		contents = new ItemStack[INVENTORY_SIZE];

		for (int i = 0; i < itemList.tagCount(); ++i) {
			NBTTagCompound itemTag = (NBTTagCompound) itemList.tagAt(i);
			int slot = itemTag.getByte("Slot") & 255;

			if (slot >= 0 && slot < INVENTORY_SIZE) {
				contents[slot] = ItemStack.loadItemStackFromNBT(itemTag);
			}
		}
	}

	/**
	 * Writes a tile entity to NBT.
	 */
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList itemList = new NBTTagList();

		for (int slot = 0; slot < INVENTORY_SIZE; ++slot) {
			if (contents[slot] != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) slot);
				contents[slot].writeToNBT(itemTag);
				itemList.appendTag(itemTag);
			}
		}

		nbt.setTag("Items", itemList);
	}

	@Override
	public int getSizeInventory() {
		return INVENTORY_SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return contents[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (contents[slot] == null) {
			return null;
		}
		ItemStack result;
		if (contents[slot].stackSize <= amount) {
			result = contents[slot];
			contents[slot] = null;
		} else {
			result = contents[slot].splitStack(amount);

			if (contents[slot].stackSize == 0) {
				contents[slot] = null;
			}
		}
		this.onInventoryChanged();
		return result;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (contents[slot] == null) {
			return null;
		}

		ItemStack result = contents[slot];
		contents[slot] = null;
		onInventoryChanged();
		return result;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		contents[slot] = stack;

		if (stack != null && stack.stackSize > INVENTORY_STACK_LIMIT) {
			stack.stackSize = INVENTORY_STACK_LIMIT;
		}

		onInventoryChanged();
	}

	@Override
	public String getInvName() {
		return "inventory.digitalchest";
	}

	@Override
	public int getInventoryStackLimit() {
		return INVENTORY_STACK_LIMIT;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}

		return player
				.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public boolean onActivation(EntityPlayer player, ForgeDirection side, float offsetX,
			float offsetY, float offsetZ) {
		player.displayGUIChest(this);
		return true;
	}

	@Override
	public void onDestroyed() {
		if (migrating)
			return;

		Random rnd = new Random();

		for (int slot = 0; slot < INVENTORY_SIZE; ++slot) {
			ItemStack stack = contents[slot];

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
						droppedEntity.func_92014_d()
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

	// chest visualization methods

	@SideOnly(Side.CLIENT)
	public float getRenderLidAngle(float partialTicks) {
		return prevLidAngle + (lidAngle - prevLidAngle) * partialTicks;
	}

	public void updateEntity() {
		super.updateEntity();

		if (getBlockType() != AdvancedResourceProcessing.blockDigitalChest) {
			migrating = true;
			worldObj.setBlockAndMetadata(xCoord, yCoord, zCoord,
					AdvancedResourceProcessing.blockDigitalChest.blockID, 0);
			worldObj.setBlockTileEntity(xCoord, yCoord, zCoord, this);
			updateContainingBlockInfo();
			migrating = false;
		}

		ticksSinceSync++;
		if (ticksSinceSync % 20 * 4 == 0) {
			worldObj.addBlockEvent(xCoord, yCoord, zCoord,
					AdvancedResourceProcessing.blockDigitalChest.blockID, 1,
					numUsingPlayers);
		}

		// dirty check to find the rendering world...
		if (!worldObj.isRemote) {
			peripheral.update();
		} else {
			updateRenderData();
		}
	}

	private void updateRenderData() {
		prevLidAngle = lidAngle;

		float lidSpeed = 0.1F;

		if (numUsingPlayers > 0 && lidAngle == 0.0F) {
			worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D,
					zCoord + 0.5D, "random.chestopen", 0.5F,
					worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0
				&& lidAngle < 1.0F) {
			if (this.numUsingPlayers > 0) {
				this.lidAngle += lidSpeed;
			} else {
				this.lidAngle -= lidSpeed;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float halfTime = 0.5F;

			if (lidAngle < halfTime && prevLidAngle >= halfTime) {
				worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D,
						zCoord + 0.5D, "random.chestclosed", 0.5F,
						worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
	}

	/**
	 * Called when a client event is received with the event number and
	 * argument, see World.sendClientEvent
	 */
	public void receiveClientEvent(int event, int arg) {
		if (event == 1) {
			numUsingPlayers = arg;
		}
	}

	public void openChest() {
		numUsingPlayers++;
		
		worldObj.addBlockEvent(xCoord, yCoord, zCoord,
				AdvancedResourceProcessing.blockDigitalChest.blockID, 1,
				numUsingPlayers);
	}

	public void closeChest() {
		numUsingPlayers--;

		worldObj.addBlockEvent(xCoord, yCoord, zCoord,
				AdvancedResourceProcessing.blockDigitalChest.blockID, 1,
				numUsingPlayers);
	}

}
