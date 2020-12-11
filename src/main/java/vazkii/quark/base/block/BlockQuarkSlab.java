/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [28/08/2016, 00:46:22 (GMT)]
 */
package vazkii.quark.base.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.item.ItemModBlockSlab;

public class BlockQuarkSlab extends BlockModSlab implements IQuarkBlock {

	public BlockQuarkSlab(String name, Material materialIn, boolean doubleSlab) {
		super(name, materialIn, doubleSlab);
	}

	@Override
	public ItemBlock createItemBlock(ResourceLocation res) {
		if (!isDouble())
			return new ItemModBlockSlab(this, res) {
				@Override
				public int getItemBurnTime(ItemStack itemStack) {
					return material == Material.WOOD ? 150 : -1;
				}
			};
		return null;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return material == Material.WOOD ? 20 : super.getFlammability(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return material == Material.WOOD ? 5 : super.getFireSpreadSpeed(world, pos, face);
	}
}
