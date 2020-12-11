package vazkii.quark.world.item;

import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;

public class ItemRootDye extends ItemMod implements IQuarkItem {

	private static final String[] VARIANTS = {
			"dye_blue",
			"dye_black",
			"dye_white"
	};
	
	public ItemRootDye() {
		super("root_dye", VARIANTS);
		setCreativeTab(CreativeTabs.MATERIALS);
	}

}
