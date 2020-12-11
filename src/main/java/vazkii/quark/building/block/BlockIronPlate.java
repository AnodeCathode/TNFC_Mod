/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [30/06/2016, 14:42:34 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

import java.util.Locale;

public class BlockIronPlate extends BlockMetaVariants<BlockIronPlate.Variants> implements IQuarkBlock {

	public BlockIronPlate() {
		super("iron_plate", Material.IRON, Variants.class);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	public enum Variants implements IStringSerializable {
		IRON_PLATE,
		RUSTY_IRON_PLATE;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
