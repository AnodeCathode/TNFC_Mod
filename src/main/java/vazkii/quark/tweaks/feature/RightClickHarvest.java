/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 05, 2019, 16:56 AM (EST)]
 */
package vazkii.quark.tweaks.feature;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.base.BlockStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RightClickHarvest extends Feature {

	public static boolean hoeHarvest;
	public static boolean emptyHandHarvest;
	public static boolean harvestingCostsDurability;
	public static boolean doHarvestingSearch;

	public static String[] harvestableBlocks;

	public static final Map<BlockStack, BlockStack> crops = Maps.newHashMap();

	private static boolean hasInit;

	@Override
	public void setupConfig() {
		hoeHarvest = loadPropBool("Hoe Harvest", "Can hoes harvest crops with right click?", true);
		emptyHandHarvest = loadPropBool("Empty Hand Harvest", "Can players harvest crops with empty hand clicks?", true);
		harvestingCostsDurability = loadPropBool("Harvesting Costs Durability", "Does harvesting crops with a hoe cost durability?", false);
		doHarvestingSearch = loadPropBool("Add Harvestable Crops", "Should Quark look for (nonvanilla) crops, and handle them?", true);
		harvestableBlocks = loadPropStringList("Harvestable Crops", "Which crops can be harvested?\n" +
						"Format is: \"harvestState[,afterHarvest]\", i.e. \"minecraft:wheat:7\" or \"minecraft:cocoa:11,minecraft:cocoa:3\"",
				new String[] {
						"minecraft:wheat:7",
						"minecraft:carrots:7",
						"minecraft:potatoes:7",
						"minecraft:beetroots:3",
						"minecraft:nether_wart:3",
						"minecraft:cocoa:11,minecraft:cocoa:3",
						"minecraft:cocoa:10,minecraft:cocoa:2",
						"minecraft:cocoa:9,minecraft:cocoa:1",
						"minecraft:cocoa:8,minecraft:cocoa:0"
				});

		if (hasInit)
			fillCropList();
	}

	@Override
	public void init() {
		hasInit = true;
		fillCropList();
	}

	private void fillCropList() {
		crops.clear();

		if (doHarvestingSearch) {
			ForgeRegistries.BLOCKS.getValuesCollection().stream()
					.filter(b -> !isVanilla(b) && b instanceof BlockCrops)
					.forEach(b -> crops.put(new BlockStack(b, ((BlockCrops) b).getMaxAge()), new BlockStack(b)));
		}

		for (String harvestKey : harvestableBlocks) {
			BlockStack initial, result;
			String[] split = harvestKey.split(",", 2);
			initial = BlockStack.fromString(split[0]);
			if (split.length > 1)
				result = BlockStack.fromString(split[1]);
			else
				result = new BlockStack(initial.getBlock());

			crops.put(initial, result);
		}
	}

	private static Method getSeed;

	private static void replant(World world, BlockPos pos, BlockStack inWorld, EntityPlayer player) {
		ItemStack mainHand = player.getHeldItemMainhand();
		boolean isHoe = !mainHand.isEmpty() && mainHand.getItem() instanceof ItemHoe;

		BlockStack newBlock = crops.get(inWorld);
		NonNullList<ItemStack> drops = NonNullList.create();
		int fortune = HoeSickle.canFortuneApply(Enchantments.FORTUNE, mainHand) && isHoe ?
				EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, mainHand) : 0;


		inWorld.getBlock().getDrops(drops, world, pos, inWorld.getState(), fortune);

		for (ItemStack stack : drops) {
			if (stack.isEmpty())
				continue;

			if (stack.getItem() instanceof IPlantable || (stack.getItem() == Items.DYE && stack.getMetadata() == EnumDyeColor.BROWN.getDyeDamage())) {
				stack.shrink(1);
				break;
			}
		}

		ForgeEventFactory.fireBlockHarvesting(drops, world, pos, inWorld.getState(), fortune, 1.0F, false, player);

		boolean seedNotNull = true;
		if (inWorld.getBlock() instanceof BlockCrops) {
			try {
				if (getSeed == null)
					getSeed = ObfuscationReflectionHelper.findMethod(BlockCrops.class, "func_149866_i", Item.class);
				Item seed = (Item) getSeed.invoke(inWorld.getBlock());
				seedNotNull = seed != null && seed != Items.AIR;
			} catch (IllegalAccessException | InvocationTargetException e) {
				Quark.LOG.error("Failed to reflect BlockCrops", e);
			}
		}

		if (seedNotNull) {
			if (!world.isRemote) {
				world.playEvent(2001, pos, Block.getStateId(newBlock.getState()));
				world.setBlockState(pos, newBlock.getState());
				for (ItemStack stack : drops) {
					EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
					entityItem.setPickupDelay(10);
					world.spawnEntity(entityItem);
				}
			}
		} else {
			Quark.LOG.warn("Crop definition {},{} does not work! Couldn't find a seed definition.", inWorld, newBlock);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onCropClick(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != EnumHand.MAIN_HAND)
			return;

		ItemStack mainHand = event.getEntityPlayer().getHeldItemMainhand();
		boolean isHoe = !mainHand.isEmpty() && mainHand.getItem() instanceof ItemHoe;

		if (!emptyHandHarvest && !isHoe)
			return;

		int range = HoeSickle.getRange(mainHand);

		int harvests = 0;

		for(int x = 1 - range; x < range; x++) {
			for (int z = 1 - range; z < range; z++) {
				BlockPos pos = event.getPos().add(x, 0, z);

				BlockStack worldBlock = BlockStack.getStackFromPos(event.getWorld(), pos);
				if (crops.containsKey(worldBlock)) {
					replant(event.getWorld(), pos, worldBlock, event.getEntityPlayer());
					harvests++;
				}
			}
		}

		if (harvests > 0) {
			if (harvestingCostsDurability && isHoe && !event.getWorld().isRemote)
				mainHand.damageItem(harvests, event.getEntityPlayer());
			event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "harvest" };
	}
}
