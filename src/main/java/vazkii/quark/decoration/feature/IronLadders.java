package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockIronLadder;

public class IronLadders extends Feature {

	public static BlockIronLadder iron_ladder;

	public static boolean isBlockNotBrokenByWater(Block block) {
		return block == iron_ladder;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		iron_ladder = new BlockIronLadder();
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(iron_ladder, 16), 
				"I I", "III", "I I",
				'I', "ingotIron");
	}	
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
