package vazkii.quark.building.block.stairs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.MagmaBricks;

import javax.annotation.Nonnull;

public class BlockMagmaBricksStairs extends BlockQuarkStairs {

	public BlockMagmaBricksStairs() {
		super("magma_bricks_stairs", MagmaBricks.magma_bricks.getDefaultState());
		useNeighborBrightness = false;
		setLightLevel(0.2F);
	}
	
	@Override
	public boolean isFireSource(@Nonnull World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos).isSideSolid(world, pos, side);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public int getPackedLightmapCoords(IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
		return 0xf000f0;
	}

}
