/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [Aug 10, 2016, 4:35:37 PM (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.decoration.feature.IronLadders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DeployLaddersDown extends Feature {

	private static Method canAttachTo;

	private static boolean canAttachTo(Block ladder, World world, BlockPos pos, EnumFacing facing) {
		if (ladder == IronLadders.iron_ladder)
			return IronLadders.iron_ladder.canBlockStay(world, pos, facing);
		else if (ladder instanceof BlockLadder) {
			BlockPos attachPos = pos.offset(facing, -1);
			if (canAttachTo == null)
				canAttachTo = ObfuscationReflectionHelper.findMethod(BlockLadder.class, "func_193392_c", Boolean.TYPE, World.class, BlockPos.class, EnumFacing.class);
			try {
				return (boolean) canAttachTo.invoke(ladder, world, attachPos, facing);
			} catch (IllegalAccessException | InvocationTargetException e) {
				// NO-OP
			}
		}

		return false;
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		EnumHand hand = event.getHand();
		ItemStack stack = player.getHeldItem(hand);

		List<Item> items = new ArrayList<>();
		items.add(Item.getItemFromBlock(Blocks.LADDER));
		if(ModuleLoader.isFeatureEnabled(IronLadders.class))
			items.add(Item.getItemFromBlock(IronLadders.iron_ladder));
		
		if(!stack.isEmpty() && items.contains(stack.getItem())) {
			Block block = Block.getBlockFromItem(stack.getItem());
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			while(world.getBlockState(pos).getBlock() == block) {
				event.setCanceled(true);
				BlockPos posDown = pos.down();

				if (world.isOutsideBuildHeight(posDown))
					break;

				IBlockState stateDown = world.getBlockState(posDown);

				if(stateDown.getBlock() == block)
					pos = posDown;
				else {
					if(stateDown.getBlock().isAir(stateDown, world, posDown)) {
						IBlockState copyState = world.getBlockState(pos);

						EnumFacing facing = copyState.getValue(BlockLadder.FACING);
						if(canAttachTo(block, world, posDown, facing)) {
							world.setBlockState(posDown, copyState);
							world.playSound(null, posDown.getX(), posDown.getY(), posDown.getZ(), SoundEvents.BLOCK_LADDER_PLACE, SoundCategory.BLOCKS, 1F, 1F);
							
							if(world.isRemote)
								player.swingArm(hand);
							
							if(!player.capabilities.isCreativeMode) {
								stack.shrink(1);
								
								if(stack.getCount() <= 0)
									player.setHeldItem(hand, ItemStack.EMPTY);
							}
						}
					}
					break;
				} 
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
