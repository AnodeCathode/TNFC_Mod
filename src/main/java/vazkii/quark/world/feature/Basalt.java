/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 15:05:14 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBasalt;
import vazkii.quark.world.block.slab.BlockBasicStoneSlab;
import vazkii.quark.world.feature.RevampStoneGen.StoneInfo;
import vazkii.quark.world.world.BasaltGenerator;

public class Basalt extends Feature {

	public static BlockMod basalt;

	public static StoneInfo basaltInfo;

	public static boolean enableStairsAndSlabs;
	public static boolean enableWalls;

	@Override
	public void setupConfig() {
		basaltInfo = RevampStoneGen.loadStoneInfo(configCategory, "basalt", 18, 20, 120, 20, true, "-1", BiomeDictionary.Type.NETHER);
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		basalt = new BlockBasalt();

		if(enableStairsAndSlabs) {
			BlockBasicStoneSlab.initSlab(basalt, 0, "stone_basalt_slab");
			BlockModStairs.initStairs(basalt, 0, new BlockQuarkStairs("stone_basalt_stairs", basalt.getDefaultState()));
		}
		VanillaWalls.add("basalt", basalt, 0, enableWalls);
		
		GameRegistry.registerWorldGenerator(new BasaltGenerator(() -> basaltInfo), 0);
		
		addOreDict("stoneBasalt", ProxyRegistry.newStack(basalt, 1, 0));
		addOreDict("stoneBasaltPolished", ProxyRegistry.newStack(basalt, 1, 1));
	}
	
	@Override
	public void postPreInit() {
		Object blackItem = ProxyRegistry.newStack(Items.COAL);
		if(ModuleLoader.isFeatureEnabled(Biotite.class))
			blackItem = "gemBiotite";

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(basalt, 4, 0),
				"BI", "IB",
				'B', "cobblestone",
				'I', blackItem);

		RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Blocks.STONE, 1, 5),
				"stoneBasalt", "gemQuartz");

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(basalt, 4, 1),
				"BB", "BB",
				'B', "stoneBasalt");
	}

	@Override
	public void init() {
		ModIntegrationHandler.registerChiselVariant("basalt", new ItemStack(basalt, 1, 0));
		ModIntegrationHandler.registerChiselVariant("basalt", new ItemStack(basalt, 1, 1));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
