package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockSoulSandstoneSlab extends BlockQuarkSlab {

	public BlockSoulSandstoneSlab(boolean doubleSlab) {
		super("soul_sandstone_slab", Material.ROCK, doubleSlab);
		setHardness(1F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

}
