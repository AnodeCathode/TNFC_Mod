/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 22:30:09 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockBlazeLantern extends BlockMod implements IQuarkBlock {

	public BlockBlazeLantern() {
		super("blaze_lantern", Material.GLASS);
		setHardness(0.3F);
		setSoundType(SoundType.GLASS);
		setLightLevel(1.0F);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

}
