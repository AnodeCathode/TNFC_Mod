/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 29, 2019, 14:42 AM (EST)]
 */
package vazkii.quark.experimental.features;

import net.minecraftforge.fml.relauncher.Side;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

import java.util.Arrays;
import java.util.Map;

public class RejectMods extends Feature {

	public static String[] rejected;

	@SuppressWarnings("ConstantConditions")
	public static boolean accepts(Map<String, String> mods, Side remoteSide) {
		if (!LibMisc.VERSION.equals("GRADLE:VERSION-GRADLE:BUILD") && !mods.get(LibMisc.MOD_ID).equals(LibMisc.VERSION))
			return false;
		if (!ModuleLoader.isFeatureEnabled(RejectMods.class))
			return true;

		return remoteSide == Side.SERVER || Arrays.stream(rejected).anyMatch(mods.keySet()::contains);
	}

	@Override
	public void setupConfig() {
		rejected = loadPropStringList("Rejected Mods", "Mods which aren't allowed on this server.", new String[0]);
	}
}
