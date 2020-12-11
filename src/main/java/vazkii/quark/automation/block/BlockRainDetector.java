/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 19:46:53 (GMT)]
 */
package vazkii.quark.automation.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.automation.tile.TileRainDetector;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;

public class BlockRainDetector extends BlockModContainer implements IQuarkBlock {

	protected static final AxisAlignedBB RAIN_DETECTOR_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);

	private static final PropertyBool POWER = PropertyBool.create("power");
	private static final PropertyBool INVERTED = PropertyBool.create("inverted");

	public BlockRainDetector() {
		super("rain_detector", Material.ROCK);
		setCreativeTab(CreativeTabs.REDSTONE);
		setHardness(1.5F);
		setSoundType(SoundType.STONE);

		setDefaultState(blockState.getBaseState().withProperty(POWER, false).withProperty(INVERTED, false));
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return RAIN_DETECTOR_AABB;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(POWER) ? 15 : 0;
	}

	public void updatePower(World worldIn, BlockPos pos) {
		if (worldIn.provider.hasSkyLight()) {
			IBlockState iblockstate = worldIn.getBlockState(pos);
			boolean raining = worldIn.isRaining();
			worldIn.getCelestialAngleRadians(1.0F);

			if(iblockstate.getValue(INVERTED))
				raining = !raining;

			if(iblockstate.getValue(POWER) != raining)
				worldIn.setBlockState(pos, iblockstate.withProperty(POWER, raining), 3);
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(playerIn.isAllowEdit()) {
			if(worldIn.isRemote)
				return true;
			else {
				worldIn.setBlockState(pos, state.withProperty(INVERTED, !state.getValue(INVERTED)), 4);
				updatePower(worldIn, pos);

				return true;
			}
		} else return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileRainDetector();
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWER, INVERTED);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(POWER) ? 0b1 : 0) | (state.getValue(INVERTED) ? 0b10 : 0);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(POWER, (meta & 0b1) != 0).withProperty(INVERTED, (meta & 0b10) != 0);
	}

}
