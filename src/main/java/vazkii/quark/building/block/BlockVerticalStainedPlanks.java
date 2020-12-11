package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

import java.util.Locale;

public class BlockVerticalStainedPlanks extends BlockMetaVariants<BlockVerticalStainedPlanks.Variants> implements IQuarkBlock {

	public BlockVerticalStainedPlanks() {
		super("vertical_stained_planks", Material.WOOD, Variants.class);
		setHardness(2.0F);
		setResistance(5.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 5;
	}

	public enum Variants implements IStringSerializable {
		VERTICAL_STAINED_PLANKS_WHITE,
		VERTICAL_STAINED_PLANKS_ORANGE,
		VERTICAL_STAINED_PLANKS_MAGENTA,
		VERTICAL_STAINED_PLANKS_LIGHT_BLUE,
		VERTICAL_STAINED_PLANKS_YELLOW,
		VERTICAL_STAINED_PLANKS_LIME,
		VERTICAL_STAINED_PLANKS_PINK,
		VERTICAL_STAINED_PLANKS_GRAY,
		VERTICAL_STAINED_PLANKS_SILVER,
		VERTICAL_STAINED_PLANKS_CYAN,
		VERTICAL_STAINED_PLANKS_PURPLE,
		VERTICAL_STAINED_PLANKS_BLUE,
		VERTICAL_STAINED_PLANKS_BROWN,
		VERTICAL_STAINED_PLANKS_GREEN,
		VERTICAL_STAINED_PLANKS_RED,
		VERTICAL_STAINED_PLANKS_BLACK;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
