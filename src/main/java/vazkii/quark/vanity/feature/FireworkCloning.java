/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [21/03/2016, 01:09:33 (GMT)]
 */
package vazkii.quark.vanity.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.recipe.FireworkCloningRecipe;

public class FireworkCloning extends Feature {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new FireworkCloningRecipe();
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
