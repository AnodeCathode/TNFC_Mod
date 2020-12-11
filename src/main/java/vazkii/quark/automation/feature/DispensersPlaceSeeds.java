/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 23:56:07 (GMT)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap.BehaviorDispenseOptional;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.util.CommonReflectiveAccessor;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DispensersPlaceSeeds extends Feature {

	public static String[] customSeedsArr;
	public static Map<Item, IBlockState> customSeeds;
	
	@Override
	public void setupConfig() {
		ConfigHelper.needsRestart = true;
		customSeedsArr = loadPropStringList("Custom Seeds", "Add seeds from other mods here, in the following format: mod:seed=mod:block:meta. Set meta to -1 to just place the default.", new String[0]);
	}
	
	@Override
	public void init() {
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.WHEAT_SEEDS, new BehaviourSeeds(Blocks.WHEAT));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.POTATO, new BehaviourSeeds(Blocks.POTATOES));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.CARROT, new BehaviourSeeds(Blocks.CARROTS));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.NETHER_WART, new BehaviourSeeds(Blocks.NETHER_WART));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.PUMPKIN_SEEDS, new BehaviourSeeds(Blocks.PUMPKIN_STEM));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.MELON_SEEDS, new BehaviourSeeds(Blocks.MELON_STEM));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.BEETROOT_SEEDS, new BehaviourSeeds(Blocks.BEETROOTS));
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(Blocks.CHORUS_FLOWER), new BehaviourSeeds(Blocks.CHORUS_FLOWER));

		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.DYE, new BehaviourCocoaBeans(BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.DYE)));
		
		setupCustomSeeds();
	}

	@SuppressWarnings("deprecation")
	public void setupCustomSeeds() {
		customSeeds = new HashMap<>();
		for(String s : customSeedsArr) {
			String[] tokens = s.split("=");
			if(tokens.length == 2) {
				String key = tokens[0];
				String value = tokens[1];
				Item item = Item.getByNameOrId(key);
				if(item != null) {
					tokens = value.split(":");
					if(tokens.length == 3)
						try {
							value = tokens[0] + ":" + tokens[1];
							int meta = Integer.parseInt(tokens[2]);
							Block block = Block.getBlockFromName(value);
							if(block != null) {
								if(meta == -1)
									customSeeds.put(item, block.getDefaultState());
								else customSeeds.put(item, block.getStateFromMeta(meta));
							}
						} catch(NumberFormatException ignored) { }
				}
			}
		}
		
		for(Item i : customSeeds.keySet())
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(i, new BehaviourSeeds(customSeeds.get(i)));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "botania", "animania" };
	}

	public class BehaviourSeeds extends BehaviorDispenseOptional {

		private final IBlockState placeState;

		public BehaviourSeeds(Block block) {
			this(block.getDefaultState());
		}
		
		public BehaviourSeeds(IBlockState state) {
			placeState = state;
		}

		@Nonnull
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
			BlockPos pos = source.getBlockPos().offset(facing);
			World world = source.getWorld();
			this.successful = false;

			if(world.isAirBlock(pos) && placeState.getBlock().canPlaceBlockAt(world, pos)) {
				world.setBlockState(pos, placeState);
				stack.shrink(1);
				this.successful = true;
				return stack;
			}

			return stack;
		}

	}

	public class BehaviourCocoaBeans extends BehaviorDispenseOptional {

		private final IBehaviorDispenseItem vanillaBehaviour;
		public BehaviourCocoaBeans(IBehaviorDispenseItem vanilla) {
			vanillaBehaviour = vanilla;
		}

		@Nonnull
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			this.successful = false;
			if(stack.getItemDamage() == EnumDyeColor.BROWN.getDyeDamage()) {
				Block block = Blocks.COCOA;
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				BlockPos pos = source.getBlockPos().offset(facing);
				World world = source.getWorld();

				BlockPos logPos = pos.offset(facing);
				IBlockState logState = world.getBlockState(logPos);
				if(logState.getBlock() == Blocks.LOG && logState.getValue(BlockOldLog.VARIANT) == BlockPlanks.EnumType.JUNGLE && world.isAirBlock(pos) && block.canPlaceBlockAt(world, pos)) {
					world.setBlockState(pos, block.getDefaultState().withProperty(BlockHorizontal.FACING, facing));
					stack.shrink(1);
					this.successful = true;
					return stack;
				}
			}

			ItemStack out = vanillaBehaviour.dispense(source, stack);

			if (vanillaBehaviour instanceof BehaviorDispenseOptional)
				this.successful = CommonReflectiveAccessor.getSuccess((BehaviorDispenseOptional) vanillaBehaviour);

			return out;
		}

	}

}
