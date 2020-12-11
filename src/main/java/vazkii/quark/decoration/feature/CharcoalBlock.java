/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/06/2016, 12:05:49 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCharcoal;

public class CharcoalBlock extends Feature {

	public static Block charcoal_block;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		charcoal_block = new BlockCharcoal();

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(charcoal_block),
				"CCC", "CCC", "CCC",
				'C', ProxyRegistry.newStack(Items.COAL, 1, 1));
		RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Items.COAL, 9, 1), ProxyRegistry.newStack(charcoal_block));

		addOreDict("blockCharcoal", charcoal_block);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "actuallyadditions", "mekanism" };
	}
	
}
