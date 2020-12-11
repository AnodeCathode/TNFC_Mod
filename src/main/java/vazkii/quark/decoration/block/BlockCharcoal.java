/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/06/2016, 11:48:13 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.arl.item.ItemModBlock;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;

public class BlockCharcoal extends BlockMod implements IQuarkBlock {

	public BlockCharcoal() {
		super("charcoal_block", Material.ROCK);
		setHardness(5.0F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public boolean isFireSource(@Nonnull World world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public ItemBlock createItemBlock(ResourceLocation res) {
		return new ItemModBlock(this, res) {
			@Override
			public int getItemBurnTime(ItemStack itemStack) {
				return 16000;
			}
		};
	}
}
