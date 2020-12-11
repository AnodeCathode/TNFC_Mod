/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 19:03:56 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockLeafCarpet;

public class LeafCarpets extends Feature {

	public static Block leaf_carpet;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		leaf_carpet = new BlockLeafCarpet();
	}

	@Override
	public void postPreInit() {
		BlockLeafCarpet.Variants[] variants = BlockLeafCarpet.Variants.values();
		for(int i = 0; i < variants.length; i++) {
			ItemStack stack = variants[i].getBaseStack();
			if(!stack.isEmpty())
				RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(leaf_carpet, 3, i),
						"LL",
						'L', stack.copy());
		}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
