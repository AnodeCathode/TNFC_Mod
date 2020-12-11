/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 04:04:47 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockNetherBrickFenceGate;

public class NetherBrickFenceGate extends Feature {

	public static Block nether_brick_fence_gate;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		nether_brick_fence_gate = new BlockNetherBrickFenceGate();

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(nether_brick_fence_gate, 2),
				"NBN", "NBN",
				'N', ProxyRegistry.newStack(Blocks.NETHER_BRICK_FENCE),
				'B', ProxyRegistry.newStack(Blocks.NETHER_BRICK));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
