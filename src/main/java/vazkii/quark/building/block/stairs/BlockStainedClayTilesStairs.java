/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 18:32:01 (GMT)]
 */
package vazkii.quark.building.block.stairs;

import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.block.BlockStainedClayTiles;
import vazkii.quark.building.feature.HardenedClayTiles;

public class BlockStainedClayTilesStairs extends BlockQuarkStairs implements IRecipeGrouped {

	@SuppressWarnings("unchecked")
	public BlockStainedClayTilesStairs(BlockStainedClayTiles.Variants variant) {
		super(variant.getName() + "_stairs", HardenedClayTiles.stained_clay_tiles.getDefaultState().withProperty(HardenedClayTiles.stained_clay_tiles.getVariantProp(), variant));
	}
	
	@Override
	public String getRecipeGroup() {
		return "stained_clay_tiles_stairs";
	}

}
