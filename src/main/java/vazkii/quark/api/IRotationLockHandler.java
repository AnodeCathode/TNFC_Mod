package vazkii.quark.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement on a Block that should have special treatment for the Rotation
 * Lock system. 
 */
public interface IRotationLockHandler {
	
	/**
	 * Return the state that should be placed in the world for the rotation lock
	 * currently enabled.
	 * @param face The face currently locked
	 * @param hasHalf true if the rotation lock specifies a block half, false otherwise
	 * @param topHalf true if the rotation lock applies to the upper half of the block, false otherwise
	 */
	IBlockState setRotation(World world, BlockPos pos, IBlockState state, EnumFacing face, boolean hasHalf, boolean topHalf);
	
}
