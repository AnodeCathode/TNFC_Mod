/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:34:52 (GMT)]
 */
package tnfcmod.qfc;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import tnfcmod.qfc.features.Crabs;
import tnfcmod.qfc.features.Frogs;
import tnfcmod.qfc.module.Module;

public class QfcWorld extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new Frogs());
		registerFeature(new Crabs());

	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Blocks.GRASS);
	}

}
