package vazkii.quark.base.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.util.ProxyRegistry;

import javax.annotation.Nonnull;

public class ItemQuarkFood extends ItemFood implements IQuarkItem {

	private final String bareName;
	private final String[] variants;

	public ItemQuarkFood(String name, int amount, float saturation, boolean isWolfFood, String... variants) {
		super(amount, saturation, isWolfFood);
		setTranslationKey(name);
		bareName = name;
		if (variants == null || variants.length == 0)
			variants = new String[] { bareName };
		if (variants.length > 1)
			setHasSubtypes(true);
		this.variants = variants;
		setCreativeTab(CreativeTabs.FOOD);
	}
	
	public ItemQuarkFood(String name, int amount, float saturation, String... variants) {
		this(name, amount, saturation, false, variants);
	}
	
	@Nonnull
	@Override
	public Item setTranslationKey(@Nonnull String name) {
		super.setTranslationKey(name);
		setRegistryName(new ResourceLocation(getPrefix() + name));
		ProxyRegistry.register(this);

		return this;
	}

	@Nonnull
	@Override
	public String getTranslationKey(ItemStack par1ItemStack) {
		int dmg = par1ItemStack.getItemDamage();
		String[] variants = getVariants();

		String name;
		if(dmg >= variants.length)
			name = bareName;
		else name = variants[dmg];

		return "item." + getPrefix() + name;
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if(isInCreativeTab(tab))
			for(int i = 0; i < getVariants().length; i++)
				subItems.add(new ItemStack(this, 1, i));
	}

	@Override
	public String[] getVariants() {
		return variants;
	}

}
