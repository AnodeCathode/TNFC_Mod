package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.world.block.BlockCrystal;
import vazkii.quark.world.block.BlockCrystalPane;
import vazkii.quark.world.world.CrystalCaveGenerator;

public class CrystalCaves extends Feature {

	public static Block crystal;
	public static Block crystalPane;

	public static DimensionConfig dims;
	public static double crystalCaveRarity, oreChance, crystalRate;
	public static boolean enablePanes;
	
	@Override
	public void setupConfig() {
		crystalCaveRarity = loadLegacyPropChance("Crystal Cave Percentage Chance", "Crystal Cave Rarity", "The percentage chance crystal caves will spawn in a given chunk", 1.0/150);
		oreChance = loadPropChance("Ore Chance", "Chance that, if a crystal location doesn't spawn a crystal, it will spawn an ore", 0.5);
		crystalRate = loadPropChance("Crystal Chance", "Chance that a given crystal location will actually spawn a crystal", 0.3);
		enablePanes = loadPropBool("Enable Panes", "", true);
		dims = new DimensionConfig(configCategory);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		crystal = new BlockCrystal();

		if (enablePanes)
			crystalPane = new BlockCrystalPane();
		
		GameRegistry.registerWorldGenerator(new CrystalCaveGenerator(dims), 1);
		
		ModIntegrationHandler.allowChiselAndBitsChiseling(crystal);
	}
	
	@Override
	public void postPreInit() {
		if(ModuleLoader.isFeatureEnabled(ColorRunes.class)) {
			addRuneRecipe(0, 0);
			addRuneRecipe(1, 14);
			addRuneRecipe(2, 1);
			addRuneRecipe(3, 4);
			addRuneRecipe(4, 5);
			addRuneRecipe(5, 3);
			addRuneRecipe(6, 11);
			addRuneRecipe(7, 2);
			addRuneRecipe(8, 15);
		}

		if (enablePanes) {
			for (int i = 0; i < BlockCrystal.Variants.values().length; i++) {
				RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(crystalPane, 16, i),
						"CCC",
						"CCC",
						'C', ProxyRegistry.newStack(crystal, 1, i));
			}
		}
	}
	
	private void addRuneRecipe(int crystalMeta, int runeMeta) {
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(ColorRunes.rune, 1, runeMeta), 
				"CCC", "CSC", "CCC",
				'C', ProxyRegistry.newStack(crystal, 1, crystalMeta),
				'S', "stone");
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
