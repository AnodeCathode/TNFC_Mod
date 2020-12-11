package vazkii.quark.world.world.tree;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import vazkii.quark.world.block.BlockVariantLeaves;
import vazkii.quark.world.feature.TreeVariants;

import javax.annotation.Nonnull;

public class WorldGenSakuraTree extends WorldGenBigTree {

	private final IBlockState leaf;

	public WorldGenSakuraTree(boolean notify) {
		super(notify);

		leaf = TreeVariants.variant_leaves.getDefaultState().withProperty(BlockVariantLeaves.VARIANT, BlockVariantLeaves.Variant.SAKURA_LEAVES).withProperty(BlockLeaves.CHECK_DECAY, false);
	}

	@Override
	protected void setBlockAndNotifyAdequately(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		if(state.getBlock() == Blocks.LEAVES)
			state = leaf;
		else if (state.getBlock() == Blocks.LOG)
			state = state.withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
		
		super.setBlockAndNotifyAdequately(worldIn, pos, state);
	}
	
}
