package vazkii.quark.building.block.stairs;

import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.block.BlockBark;
import vazkii.quark.building.feature.BarkBlocks;

public class BlockBarkStairs extends BlockQuarkStairs implements IRecipeGrouped {

	@SuppressWarnings("unchecked")
	public BlockBarkStairs(BlockBark.Variants variant) {
		super(variant.getName() + "_stairs", BarkBlocks.bark.getDefaultState().withProperty(BarkBlocks.bark.getVariantProp(), variant));
	}
	
	@Override
	public String getRecipeGroup() {
		return "bark_stairs";
	}

}
