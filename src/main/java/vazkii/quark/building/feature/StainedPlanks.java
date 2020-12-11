package vazkii.quark.building.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockStainedPlanks;
import vazkii.quark.building.block.slab.BlockStainedPlanksSlab;
import vazkii.quark.building.block.stairs.BlockStainedPlanksStairs;

public class StainedPlanks extends Feature {

	public static BlockMod stained_planks;

	public static boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		stained_planks = new BlockStainedPlanks();

		if(enableStairsAndSlabs) {
			for(BlockStainedPlanks.Variants variant : BlockStainedPlanks.Variants.values())
				BlockModStairs.initStairs(stained_planks, variant.ordinal(), new BlockStainedPlanksStairs(variant));
			for(BlockStainedPlanks.Variants variant : BlockStainedPlanks.Variants.values())
				BlockModSlab.initSlab(stained_planks, variant.ordinal(), new BlockStainedPlanksSlab(variant, false), new BlockStainedPlanksSlab(variant, true));
		}
		
		for(int i = 0; i < 16; i++) {
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(stained_planks, 8, i),
					"BBB", "BDB", "BBB",
					'B', "plankWood",
					'D', LibMisc.OREDICT_DYES.get(15 - i));
		}
		
		addOreDict("plankWood", ProxyRegistry.newStack(stained_planks, 1, OreDictionary.WILDCARD_VALUE));
		addOreDict("plankStained", ProxyRegistry.newStack(stained_planks, 1, OreDictionary.WILDCARD_VALUE));
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
