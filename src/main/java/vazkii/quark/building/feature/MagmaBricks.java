package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockMagmaBricks;
import vazkii.quark.building.block.slab.BlockMagmaBricksSlab;
import vazkii.quark.building.block.stairs.BlockMagmaBricksStairs;

public class MagmaBricks extends Feature {

	public static Block magma_bricks;

	public static boolean enableStairsAndSlabs;

	@Override
	public void setupConfig() {
		enableStairsAndSlabs = loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		magma_bricks = new BlockMagmaBricks();
		
		if(enableStairsAndSlabs) {
			BlockModStairs.initStairs(magma_bricks, 0, new BlockMagmaBricksStairs());
			BlockModSlab.initSlab(magma_bricks, 0, new BlockMagmaBricksSlab(false), new BlockMagmaBricksSlab(true));
		}
		
		RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(magma_bricks, 4), 
				ProxyRegistry.newStack(Blocks.STONEBRICK), ProxyRegistry.newStack(Blocks.STONEBRICK), ProxyRegistry.newStack(Blocks.MAGMA), ProxyRegistry.newStack(Blocks.MAGMA));
	}
	
}
