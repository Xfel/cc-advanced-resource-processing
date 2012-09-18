package xfel.mods.arp.base.utils;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.Orientations;
import buildcraft.api.inventory.ISpecialInventory;

public class SpecialInventoryWrapper extends InventoryWrapper implements
		ISpecialInventory {

	protected final Orientations side;

	public SpecialInventoryWrapper(ISpecialInventory target, ForgeDirection side) {
		super(target);
		this.side = Orientations.values()[side.ordinal()];
	}

	@Override
	public int addItem(ItemStack stack, boolean doAdd, Orientations from) {
		return ((ISpecialInventory) target).addItem(stack, doAdd, side);
	}

	@Override
	public ItemStack[] extractItem(boolean doRemove, Orientations from,
			int maxItemCount) {
		return ((ISpecialInventory) target).extractItem(doRemove, side,
				maxItemCount);
	}

}
