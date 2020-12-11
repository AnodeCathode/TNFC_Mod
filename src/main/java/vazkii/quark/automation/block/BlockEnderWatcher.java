/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 21:48:06 (GMT)]
 */
package vazkii.quark.automation.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.automation.tile.TileEnderWatcher;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;

public class BlockEnderWatcher extends BlockModContainer implements IQuarkBlock {

	public static final PropertyBool WATCHED = PropertyBool.create("watched");

	public BlockEnderWatcher() {
		super("ender_watcher", Material.IRON);
		setHardness(3F);
		setResistance(10F);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(WATCHED, false));
		setCreativeTab(CreativeTabs.REDSTONE);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, WATCHED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getValue(WATCHED) ? 15 : 0;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(WATCHED) ? 1 : 0;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(WATCHED, meta != 0);
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileEnderWatcher();
	}

}
