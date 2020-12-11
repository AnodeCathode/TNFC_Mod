package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockDuskbound;
import vazkii.quark.building.block.BlockDuskboundLantern;
import vazkii.quark.building.block.slab.BlockDuskboundSlab;
import vazkii.quark.building.block.stairs.BlockDuskboundStairs;

public class DuskboundBlocks extends Feature {

	public static Block duskbound_block;
	public static Block duskbound_lantern;

	public static boolean enableStairsAndSlabs;
	public static boolean enableWalls;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
		enableWalls = loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		duskbound_block = new BlockDuskbound();
		duskbound_lantern = new BlockDuskboundLantern();

		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(duskbound_block, 0, new BlockDuskboundStairs());
			BlockModSlab.initSlab(duskbound_block, 0, new BlockDuskboundSlab(false), new BlockDuskboundSlab(true));
		}
		
		VanillaWalls.add("duskbound_block", duskbound_block, 0, enableWalls);
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(duskbound_block, 16), 
				"PPP", "POP", "PPP",
				'P', ProxyRegistry.newStack(Blocks.PURPUR_BLOCK),
				'O', ProxyRegistry.newStack(Blocks.OBSIDIAN));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(duskbound_lantern, 4), 
				"DDD", "DED", "DDD",
				'D', ProxyRegistry.newStack(duskbound_block),
				'E', ProxyRegistry.newStack(Items.ENDER_PEARL));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
