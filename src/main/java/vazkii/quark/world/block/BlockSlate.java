package vazkii.quark.world.block;

import java.util.Locale;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSlate extends BlockMetaVariants<BlockSlate.Variants> implements IQuarkBlock {

	public BlockSlate() {
		super("slate", Material.ROCK, Variants.class);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	public enum Variants implements IStringSerializable {
		STONE_SLATE,
		STONE_SLATE_SMOOTH;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
