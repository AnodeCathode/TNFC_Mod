/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 21:47:12 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockLitLamp;

public class LitLamp extends Feature {

	public static Block lit_lamp;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		lit_lamp = new BlockLitLamp();

		RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(lit_lamp), ProxyRegistry.newStack(Blocks.REDSTONE_LAMP), ProxyRegistry.newStack(Blocks.REDSTONE_TORCH));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
