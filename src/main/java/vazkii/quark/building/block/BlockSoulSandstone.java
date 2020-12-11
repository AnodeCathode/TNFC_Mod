package vazkii.quark.building.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

import java.util.Locale;

public class BlockSoulSandstone extends BlockMetaVariants<BlockSoulSandstone.Variants> implements IQuarkBlock {

	public BlockSoulSandstone() {
		super("soul_sandstone", Material.ROCK, Variants.class);
		setHardness(1F);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	public enum Variants implements IStringSerializable {
		SOUL_SANDSTONE,
		CHISELED_SOUL_SANDSTONE,
		SMOOTH_SOUL_SANDSTONE;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
	
}
