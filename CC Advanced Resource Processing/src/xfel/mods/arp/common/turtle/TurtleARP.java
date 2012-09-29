package xfel.mods.arp.common.turtle;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;

import cpw.mods.fml.relauncher.ReflectionHelper;
import xfel.mods.arp.api.ItemKey;
import xfel.mods.arp.base.blocks.TileOrientable;
import xfel.mods.arp.base.peripheral.bind.PeripheralMethod;
import xfel.mods.arp.base.utils.WorldCoordinate;
import xfel.mods.arp.common.AdvancedResourceProcessing;
import xfel.mods.arp.common.CommonProxy;
import xfel.mods.arp.common.blocks.BlockAdvancedMachine;
import xfel.mods.arp.common.peripheral.InventoryPeripheral;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.ITurtlePeripheral;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleSide;
import dan200.turtle.api.TurtleUpgradeType;
import dan200.turtle.api.TurtleVerb;

public class TurtleARP implements ITurtleUpgrade {

	public static class Peripheral extends InventoryPeripheral {

		private ITurtleAccess turtle;

		public Peripheral(ITurtleAccess turtle) {
			super(new TurtleInventoryProvider(turtle));
			this.turtle = turtle;
		}

		private Map doDetect(ForgeDirection side) {
			WorldCoordinate target = new WorldCoordinate(turtle.getWorld(),
					turtle.getPosition());
			target.moveLocal(side, 1);

			Block block = target.getBlock();
			Item item = Item.itemsList[block.blockID];

			// use reflection to calculate item damage
			int damage = 0;
			if (item.getHasSubtypes()) {
				damage = target.getBlockMetadata();
				try {
					damage = ((Number) ReflectionHelper.findMethod(Block.class,
							block, new String[] { "damageDropped", "b" },
							new Class[] { int.class }).invoke(block,
							Integer.valueOf(damage))).intValue();
				} catch (InvocationTargetException e) {
					AdvancedResourceProcessing.MOD_LOGGER
							.log(Level.WARNING,
									"Unexpected exception while trying to decode block metadata to item damage. Using direct metadata...",
									e.getTargetException());
				} catch (Exception e) {
					AdvancedResourceProcessing.MOD_LOGGER
							.log(Level.WARNING,
									"Unexpected exception while trying to decode block metadata to item damage. Using direct metadata...",
									e);
				}
			}
			ItemKey type = new ItemKey(item, damage);
			
			return database.getItemProperties(type);
		}
		
		@PeripheralMethod
		public void detect(){
			doDetect(ForgeDirection.getOrientation(turtle.getFacingDir()));
		}
		@PeripheralMethod
		public void detectUp(){
			doDetect(ForgeDirection.UP);
		}
		
		@PeripheralMethod
		public void detectDown(){
			doDetect(ForgeDirection.DOWN);
		}

	}

	@Override
	public int getUpgradeID() {
		return 200;
	}

	@Override
	public String getAdjective() {
		return "ARP";
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return new ItemStack(AdvancedResourceProcessing.blockAdvancedMachine,
				1, BlockAdvancedMachine.TYPE_INVENTORY_INTERFACE);
	}

	@Override
	public boolean isSecret() {
		return false;
	}

	@Override
	public String getIconTexture(ITurtleAccess turtle, TurtleSide side) {
		return CommonProxy.TERRAIN_TEXTURES;
	}

	@Override
	public int getIconIndex(ITurtleAccess turtle, TurtleSide side) {
		return 2;
	}

	@Override
	public ITurtlePeripheral createPeripheral(ITurtleAccess turtle,
			TurtleSide side) {
		return new Peripheral(turtle);
	}

	@Override
	public boolean useTool(ITurtleAccess turtle, TurtleSide side,
			TurtleVerb verb, int direction) {
		return false;
	}

}
