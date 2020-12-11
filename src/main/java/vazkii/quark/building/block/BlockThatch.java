/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 22:41:27 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.building.feature.Thatch;

public class BlockThatch extends BlockMod implements IQuarkBlock {

	public BlockThatch() {
		super("thatch", Material.GRASS);
		setHardness(0.5F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		entityIn.fall(fallDistance, Thatch.fallDamageMultiplier);
	}

}
