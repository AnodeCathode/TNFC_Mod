/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 21:13:03 (GMT)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockObsidianPressurePlate;
import vazkii.quark.base.module.Feature;

public class ObsidianPressurePlate extends Feature {

	public static Block obsidian_pressure_plate;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		obsidian_pressure_plate = new BlockObsidianPressurePlate();

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(obsidian_pressure_plate),
				"OO",
				'O', ProxyRegistry.newStack(Blocks.OBSIDIAN));
	}

}
