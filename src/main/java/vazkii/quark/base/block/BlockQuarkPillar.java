/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 23:32:36 (GMT)]
 */
package vazkii.quark.base.block;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;

import javax.annotation.Nonnull;

public class BlockQuarkPillar extends BlockMod implements IQuarkBlock {

	public BlockQuarkPillar(String name, Material materialIn, String... variants) {
		super(name, materialIn, variants);
	}

	@Override
	public boolean rotateBlock(net.minecraft.world.World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
		net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
		for(net.minecraft.block.properties.IProperty<?> prop : state.getProperties().keySet()) {
			if(prop == BlockRotatedPillar.AXIS) {
				world.setBlockState(pos, state.cycleProperty(prop));
				return true;
			}
		}
		return false;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch (state.getValue(BlockRotatedPillar.AXIS)) {
			case X:
				return state.withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.Z);
			case Z:
				return state.withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.X);
			default:
				return state;
			}

		default:
			return state;
		}
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing.Axis axis = EnumFacing.Axis.Y;
		int i = meta & 12;

		if (i == 4)
			axis = EnumFacing.Axis.X;
		else if (i == 8)
			axis = EnumFacing.Axis.Z;

		return getDefaultState().withProperty(BlockRotatedPillar.AXIS, axis);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		EnumFacing.Axis axis = state.getValue(BlockRotatedPillar.AXIS);

		if(axis == EnumFacing.Axis.X)
			i |= 4;
		else if(axis == EnumFacing.Axis.Z)
			i |= 8;

		return i;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockRotatedPillar.AXIS);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getStateFromMeta(meta).withProperty(BlockRotatedPillar.AXIS, facing.getAxis());
	}

}
