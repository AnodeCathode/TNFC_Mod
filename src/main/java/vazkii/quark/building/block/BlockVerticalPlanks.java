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

public class BlockVerticalPlanks extends BlockMetaVariants<BlockVerticalPlanks.Variants> implements IQuarkBlock {

	public BlockVerticalPlanks() {
		super("vertical_planks", Material.WOOD, Variants.class);
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
		VERTICAL_OAK_PLANKS,
		VERTICAL_SPRUCE_PLANKS,
		VERTICAL_BIRCH_PLANKS,
		VERTICAL_JUNGLE_PLANKS,
		VERTICAL_ACACIA_PLANKS,
		VERTICAL_DARK_OAK_PLANKS;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
