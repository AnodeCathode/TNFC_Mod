package vazkii.quark.world.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.item.ItemQuarkFood;
import vazkii.quark.world.feature.CaveRoots;

import javax.annotation.Nonnull;

public class ItemRootFlower extends ItemQuarkFood implements IQuarkItem {

	private static final String[] VARIANTS = {
			"root_blue_flower",
			"root_black_flower",
			"root_white_flower"
	};
	
	public ItemRootFlower() {
		super("root_flower", 1, 0F, VARIANTS);
		setCreativeTab(CreativeTabs.MATERIALS);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, @Nonnull EntityPlayer player) {
		if (worldIn.isRemote)
			return;

		switch(stack.getItemDamage()) {
			case 0:
				player.addPotionEffect(new PotionEffect(CaveRoots.blue_effect, 3600));
				break;
			case 1:
				player.addPotionEffect(new PotionEffect(CaveRoots.black_effect, 3600));
				break;
			case 2:
				player.addPotionEffect(new PotionEffect(CaveRoots.white_effect, 3600));
				break;
		}
	}
}
