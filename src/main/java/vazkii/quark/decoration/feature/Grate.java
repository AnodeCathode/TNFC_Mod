package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockGrate;

// this feature is pretty grate
public class Grate extends Feature {

	public static Block grate;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		grate = new BlockGrate();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(grate),
				"BB", "BB",
				'B', ProxyRegistry.newStack(Blocks.IRON_BARS));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
