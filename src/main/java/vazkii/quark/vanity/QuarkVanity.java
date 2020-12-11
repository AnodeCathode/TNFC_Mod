/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:36:08 (GMT)]
 */
package vazkii.quark.vanity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.vanity.feature.BoatSails;
import vazkii.quark.vanity.feature.DyableElytra;
import vazkii.quark.vanity.feature.DyeItemNames;
import vazkii.quark.vanity.feature.EmoteSystem;
import vazkii.quark.vanity.feature.FireworkCloning;
import vazkii.quark.vanity.feature.SitInStairs;
import vazkii.quark.vanity.feature.WitchHat;

public class QuarkVanity extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new DyableElytra());
		registerFeature(new FireworkCloning());
		registerFeature(new EmoteSystem());
		registerFeature(new SitInStairs());
		registerFeature(new WitchHat());
		registerFeature(new BoatSails());
		registerFeature(new DyeItemNames());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.LEATHER_HELMET);
	}

}
