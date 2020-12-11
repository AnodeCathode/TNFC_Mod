/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [16/07/2016, 21:39:56 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class HoeSickle extends Feature {

	public static boolean hoesCanHaveFortune;

	@Override
	public void setupConfig() {
		hoesCanHaveFortune = loadPropBool("Hoes Can Have Fortune", "Can hoes have Fortune anviled on?", true);
	}

	public static int getRange(ItemStack hoe) {
		if (!ModuleLoader.isFeatureEnabled(HoeSickle.class))
			return 1;

		if (hoe.isEmpty() || !(hoe.getItem() instanceof ItemHoe))
			return 1;
		else if (hoe.getItem() == Items.DIAMOND_HOE)
			return 3;
		else
			return 2;
	}

	public static boolean canFortuneApply(Enchantment enchantment, ItemStack stack) {
		return enchantment == Enchantments.FORTUNE && hoesCanHaveFortune &&
				!stack.isEmpty() && stack.getItem() instanceof ItemHoe;
	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		World world = event.getWorld();
		EntityPlayer player = event.getPlayer();
		BlockPos basePos = event.getPos();
		ItemStack stack = player.getHeldItemMainhand();
		if (!stack.isEmpty() && stack.getItem() instanceof ItemHoe && canHarvest(world, basePos, event.getState())) {
			int range = getRange(stack);

			for (int i = 1 - range; i < range; i++)
				for (int k = 1 - range; k < range; k++) {
					if (i == 0 && k == 0)
						continue;

					BlockPos pos = basePos.add(i, 0, k);
					IBlockState state = world.getBlockState(pos);
					if (canHarvest(world, pos, state)) {
						Block block = state.getBlock();
						if (block.canHarvestBlock(world, pos, player))
							block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
						world.setBlockToAir(pos);
						world.playEvent(2001, pos, Block.getStateId(state));
					}
				}

			stack.damageItem(1, player);
		}
	}
	
	private boolean canHarvest(World world, BlockPos pos, IBlockState state) {
		Block block = state.getBlock();
		if(block instanceof IPlantable) {
			IPlantable plant = (IPlantable) block;
			EnumPlantType type = plant.getPlantType(world, pos);
			return type != EnumPlantType.Water && type != EnumPlantType.Desert;
		}
		
		return state.getMaterial() == Material.PLANTS && block.isReplaceable(world, pos);
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
