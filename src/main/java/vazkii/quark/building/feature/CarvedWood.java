/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [30/03/2016, 18:42:41 (GMT)]
 */
package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockCarvedWood;

public class CarvedWood extends Feature {

	public static BlockMod carvedWood;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		carvedWood = new BlockCarvedWood();

		for(int i = 0; i < 6; i++)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(carvedWood, 2, i),
					"WW", "WW",
					'W', ProxyRegistry.newStack(Blocks.WOODEN_SLAB, 1, i));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
