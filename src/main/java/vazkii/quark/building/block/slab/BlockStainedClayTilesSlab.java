/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 18:31:18 (GMT)]
 */
package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.building.block.BlockStainedClayTiles;

import javax.annotation.Nonnull;

public class BlockStainedClayTilesSlab extends BlockQuarkSlab implements IRecipeGrouped {

	public BlockStainedClayTilesSlab(BlockStainedClayTiles.Variants variant, boolean doubleSlab) {
		super(variant.getName() + "_slab", Material.ROCK, doubleSlab);
		setHardness(1.25F);
		setResistance(7.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
		return MapColor.ADOBE;
	}
	
	@Override
	public String getRecipeGroup() {
		return "stained_clay_tiles_slab";
	}

}
