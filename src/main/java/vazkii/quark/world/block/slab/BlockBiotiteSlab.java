/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 18:18:35 (GMT)]
 */
package vazkii.quark.world.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockBiotiteSlab extends BlockQuarkSlab {

	public BlockBiotiteSlab(boolean doubleSlab) {
		super("biotite_slab", Material.ROCK, doubleSlab);
		setSoundType(SoundType.STONE);
		setHardness(0.8F);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

}
