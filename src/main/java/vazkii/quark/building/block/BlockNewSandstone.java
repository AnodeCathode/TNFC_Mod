/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2016, 23:10:28 (GMT)]
 */
package vazkii.quark.building.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IStringSerializable;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.SoulSandstone;

import java.util.Locale;

public class BlockNewSandstone extends BlockMetaVariants<BlockNewSandstone.Variants> implements IQuarkBlock {

	public BlockNewSandstone() {
		super("sandstone_new", Material.ROCK, Variants.class);
		setHardness(0.8F);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public boolean shouldDisplayVariant(int variant) {
		return ModuleLoader.isFeatureEnabled(SoulSandstone.class) || variant < 4;
	}
	
	public enum Variants implements IStringSerializable {
		SANDSTONE_SMOOTH(false, true),
		SANDSTONE_BRICKS(true, true),
		RED_SANDSTONE_SMOOTH(false, true),
		RED_SANDSTONE_BRICKS(true, true),
		SOUL_SANDSTONE_SMOOTH(false, true),
		SOUL_SANDSTONE_BRICKS(true, true);

		Variants(boolean stairs, boolean slabs) {
			this.stairs = stairs;
			this.slabs = slabs;
		}
		public final boolean stairs, slabs;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
