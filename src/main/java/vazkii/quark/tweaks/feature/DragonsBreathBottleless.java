/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 01:34:38 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.init.Items;
import vazkii.quark.base.module.Feature;

public class DragonsBreathBottleless extends Feature {

	@Override
	@SuppressWarnings("ConstantConditions")
	public void postInit() {
		Items.DRAGON_BREATH.setContainerItem(null);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Effective Dragon Breath";
	}

}
