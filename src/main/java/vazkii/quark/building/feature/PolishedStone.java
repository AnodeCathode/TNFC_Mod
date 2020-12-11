/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [30/03/2016, 18:22:12 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockPolishedStone;

public class PolishedStone extends Feature {

	public static Block polished_stone;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		polished_stone = new BlockPolishedStone();

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(polished_stone, 2),
				"SS", "SS",
				'S', ProxyRegistry.newStack(Blocks.STONE_SLAB));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.STONE_SLAB, 6),
				"SSS",
				'S', ProxyRegistry.newStack(polished_stone));
		
		addOreDict("stonePolished", polished_stone);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
