package vazkii.quark.building.block.stairs;

import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.Turf;
import vazkii.quark.building.feature.Turf.ITurfBlock;

public class BlockTurfStairs extends BlockQuarkStairs implements ITurfBlock {

	public BlockTurfStairs() {
		super("turf_stairs", Turf.turf.getDefaultState());
	}

}
