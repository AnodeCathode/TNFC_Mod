package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.UndergroundBiomes;

import java.util.Locale;
import java.util.function.Supplier;

public class BlockBiomeBrick extends BlockMetaVariants<BlockBiomeBrick.Variants> implements IQuarkBlock {

	public BlockBiomeBrick() {
		super("biome_brick", Material.ROCK, Variants.class);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	@Override
	public boolean shouldDisplayVariant(int variant) {
		return Variants.values()[variant].isEnabled();
	}

	public enum Variants implements IStringSerializable {
		FIRE_STONE_BRICK(() -> UndergroundBiomes.firestoneEnabled),
		ICY_STONE_BRICK(() -> UndergroundBiomes.icystoneEnabled);
		
		Variants(Supplier<Boolean> enabledCond) {
			this.enabledCond = enabledCond;
		}
		
		private final Supplier<Boolean> enabledCond;
		
		public boolean isEnabled() {
			return enabledCond.get();
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}


}
