package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.interf.IRecipeGrouped;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.building.block.BlockBark;

public class BlockBarkSlab extends BlockQuarkSlab implements IRecipeGrouped {

	public BlockBarkSlab(BlockBark.Variants variant, boolean doubleSlab) {
		super(variant.getName() + "_slab", Material.WOOD, doubleSlab);
		setHardness(2.0F);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public String getRecipeGroup() {
		return "bark_slab";
	}
}
