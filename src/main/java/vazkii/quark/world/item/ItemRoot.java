package vazkii.quark.world.item;

import net.minecraft.item.ItemStack;
import vazkii.quark.base.item.ItemQuarkFood;

public class ItemRoot extends ItemQuarkFood {

	public ItemRoot() {
		super("root", 3, 0.4F);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}
	
}
