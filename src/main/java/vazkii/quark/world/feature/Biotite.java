/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/04/2016, 17:34:10 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBiotite;
import vazkii.quark.world.block.BlockBiotiteOre;
import vazkii.quark.world.block.slab.BlockBiotiteSlab;
import vazkii.quark.world.item.ItemBiotite;
import vazkii.quark.world.world.BiotiteGenerator;

import java.util.Random;

public class Biotite extends Feature {

	public static Block biotite_ore;
	public static Block biotite_block;

	public static Item biotite;

	public static boolean generateNaturally;
	public static boolean generateByDragon;
	public static boolean enableWalls;
	public static int clusterSize, clusterCount;
	public static int generationDelay;
	public static int clustersPerTick;

	@Override
	public void setupConfig() {
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
		generateNaturally = loadPropBool("Generate naturally", "", false);
		generateByDragon = loadPropBool("Generate by dragon kill", "", true);
		clusterSize = loadPropInt("Cluster size", "", 14);
		clusterCount = loadPropInt("Cluster count for natural generation", "", 16);
		generationDelay = loadPropInt("Generation delay on dragon death", "", 1);
		clustersPerTick = loadPropInt("Clusters generated per dragon death tick", "", 16);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		biotite_ore = new BlockBiotiteOre();
		biotite_block = new BlockBiotite();

		biotite = new ItemBiotite();

		BlockModSlab singleSlab = new BlockBiotiteSlab(false);
		BlockModSlab.initSlab(biotite_block, 0, singleSlab, new BlockBiotiteSlab(true));
		BlockModStairs.initStairs(biotite_block, 0, new BlockQuarkStairs("biotite_stairs", biotite_block.getDefaultState()));

		VanillaWalls.add("biotite", biotite_block, 0, enableWalls);

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biotite_block),
				"BB", "BB",
				'B', ProxyRegistry.newStack(biotite));

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biotite_block, 2, 1),
				"B", "B",
				'B', ProxyRegistry.newStack(singleSlab));

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biotite_block, 2, 2),
				"B", "B",
				'B', ProxyRegistry.newStack(biotite_block));

		GameRegistry.registerWorldGenerator(new BiotiteGenerator(clusterSize, clusterCount), 0);
		
		addOreDict("gemEnderBiotite", biotite);
	}

	@Override
	public void postInit() {
		FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(biotite_ore), new ItemStack(biotite), 1);
	}

	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event) {
		if(generateByDragon && event.getEntityLiving() instanceof EntityDragon && !event.getEntity().getEntityWorld().isRemote) {
			EntityDragon dragon = (EntityDragon) event.getEntity();

			if(dragon.deathTicks > 0 && dragon.deathTicks % generationDelay == 0) {
				Random rand = dragon.getEntityWorld().rand;
				BlockPos basePos = dragon.getPosition();
				basePos = new BlockPos(basePos.getX() - 128, 0, basePos.getZ() -128);

				for(int i = 0; i < clustersPerTick; i++) {
					BlockPos pos = basePos.add(rand.nextInt(256), rand.nextInt(64), rand.nextInt(256));
					BiotiteGenerator.generator.generate(dragon.getEntityWorld(), rand, pos);
				}
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
