package vazkii.quark.world.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

import java.util.Locale;

public class BlockLimestone extends BlockMetaVariants<BlockLimestone.Variants> implements IQuarkBlock {

	public BlockLimestone() {
		super("limestone", Material.ROCK, Variants.class);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	public enum Variants implements IStringSerializable {
		STONE_LIMESTONE,
		STONE_LIMESTONE_SMOOTH;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
