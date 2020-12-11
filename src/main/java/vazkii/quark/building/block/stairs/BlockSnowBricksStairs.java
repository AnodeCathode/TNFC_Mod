/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 22:53:42 (GMT)]
 */
package vazkii.quark.building.block.stairs;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.building.feature.SnowBricks;

import javax.annotation.Nonnull;

public class BlockSnowBricksStairs extends BlockQuarkStairs {

	public BlockSnowBricksStairs() {
		super("snow_bricks_stairs", SnowBricks.snow_bricks.getDefaultState());
	}

	@Override
	public boolean isToolEffective(String type, @Nonnull IBlockState state) {
		return type.equals("shovel");
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
		return true;
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

}
