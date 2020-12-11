/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [10/06/2016, 18:05:33 (GMT)]
 */
package vazkii.quark.experimental;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.experimental.client.TextureDump;
import vazkii.quark.experimental.features.*;

public class QuarkExperimental extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new BiggerCaves(), false);
		registerFeature(new PrintSuppressor(), false);
		registerFeature(new ColoredLights(), false);
		registerFeature(new ReactiveCursor(), false);
		registerFeature(new ParrotsSayDespacito(), false);
		registerFeature(new CollateralPistonMovement(), false);
		registerFeature(new FramedBlocks(), false);
		registerFeature(new TextureDump(), "Dump texture atlases", false);
		registerFeature(new BetterNausea(), false);
		registerFeature(new CustomSplashes(), false);
		registerFeature(new RejectMods(), false);
	}

	@Override
	public String getModuleDescription() {
		return "Experimental Features. All features in this module are disabled by default. Use at your own risk.";
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Blocks.TNT);
	}

}
