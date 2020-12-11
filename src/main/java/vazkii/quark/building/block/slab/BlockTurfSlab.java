package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.building.feature.Turf.ITurfBlock;

public class BlockTurfSlab extends BlockQuarkSlab implements ITurfBlock {

	public BlockTurfSlab(boolean doubleSlab) {
		super("turf_slab", Material.GRASS, doubleSlab);
		setHardness(0.6F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

}
