package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

import java.util.Locale;

import javax.annotation.Nonnull;

public class BlockPaperLantern extends BlockMetaVariants<BlockPaperLantern.Variants> implements IQuarkBlock {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125, 0.125, 0.125, 0.875, 0.875, 0.875);
	
	public BlockPaperLantern() {
		super("paper_lantern", Material.WOOD, Variants.class);
		setHardness(1.5F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setLightLevel(1F);
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 30;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 60;
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER;
	}

	public enum Variants implements IStringSerializable {
		PAPER_LANTERN,
		PAPER_LANTERN_SAKURA;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}


}
