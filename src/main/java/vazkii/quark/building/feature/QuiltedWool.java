package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockQuiltedWool;

public class QuiltedWool extends Feature {

	public static Block quilted_wool;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		quilted_wool = new BlockQuiltedWool();
		
		for(int i = 0; i < 16; i++)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(quilted_wool, 3, i), 
					" S ", "WWW", " S ",
					'S', ProxyRegistry.newStack(Items.STRING),
					'W', ProxyRegistry.newStack(Blocks.WOOL, 1, i));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	
}
