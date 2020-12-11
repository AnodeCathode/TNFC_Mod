/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 19:31:28 (GMT)]
 */
package vazkii.quark.building.block.slab;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import vazkii.arl.block.BlockModSlab;
import vazkii.quark.base.block.BlockQuarkSlab;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockVanillaSlab extends BlockQuarkSlab {

	private final IBlockState parent;

	public BlockVanillaSlab(String name, IBlockState state, boolean doubleSlab) {
		super(name, state.getMaterial(), doubleSlab);

		parent = state;

		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
		return parent.getBlock().getExplosionResistance(world, pos, exploder, explosion) * 5 / 3;
	}

	@Override
	@SuppressWarnings("deprecation")
	public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
		return parent.getBlockHardness(worldIn, pos);
	}

	@Nonnull
	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
		return parent.getBlock().getSoundType(parent, world, pos, entity);
	}

	public static BlockModSlab initSlab(Block base, int meta, IBlockState state, String name) {
		BlockModSlab slab = new BlockVanillaSlab(name, state, false);
		BlockModSlab.initSlab(base, meta, slab, new BlockVanillaSlab(name, state, true));
		return slab;
	}
}
