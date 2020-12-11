package vazkii.quark.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BlockQuarkBush extends BlockBush implements IQuarkBlock {

	private final String[] variants;
	private final String bareName;

	public BlockQuarkBush(String name, String... variants) {
		if(variants.length == 0)
			variants = new String[] { name };
		
		this.variants = variants;
		bareName = name;

		setTranslationKey(name);
	}

	@Nonnull
	@Override
	public Block setTranslationKey(@Nonnull String name) {
		super.setTranslationKey(name);
		register(name);
		return this;
	}

	@Override
	public String getBareName() {
		return bareName;
	}

	@Override
	public String[] getVariants() {
		return variants;
	}

	@Override
	public ItemMeshDefinition getCustomMeshDefinition() {
		return null;
	}

	@Override
	public EnumRarity getBlockRarity(ItemStack stack) {
		return EnumRarity.COMMON;
	}

	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[0];
	}

	@Override
	public IProperty getVariantProp() {
		return null;
	}

	@Override
	public Class getVariantEnum() {
		return null;
	}

}
