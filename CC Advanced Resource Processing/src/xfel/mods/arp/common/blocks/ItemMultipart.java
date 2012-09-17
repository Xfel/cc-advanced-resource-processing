package xfel.mods.arp.common.blocks;

import java.util.List;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;

public class ItemMultipart extends ItemBlock {

	private BlockMultipart.Subtype[] subtypes;

	public ItemMultipart(int id) {
		super(id);
		setHasSubtypes(true);

		subtypes = ((BlockMultipart) Block.blocksList[getBlockID()]).subtypes;
	}

	@Override
	public String getItemNameIS(ItemStack stack) {
		if(subtypes[stack.getItemDamage()]==null)return null;
		String baseName=super.getItemName();
		return baseName+"."+subtypes[stack.getItemDamage()].name;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs tab,
			List result) {
		for (int metadata = 0; metadata < subtypes.length; metadata++) {
			BlockMultipart.Subtype st = subtypes[metadata];
			if(st!=null){
				result.add(new ItemStack(this, 1, metadata));
			}
		}
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}
