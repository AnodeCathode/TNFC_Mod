package vazkii.quark.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Implement this on a Block that you want placed Gunpowder to be
 * able to ignite like TNT.
 */
public interface IFuseIgnitable {

	void onIgnitedByFuse(IBlockAccess world, BlockPos pos, IBlockState state);
	
}
