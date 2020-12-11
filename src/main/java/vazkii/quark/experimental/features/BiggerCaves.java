/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [10/06/2016, 23:40:24 (GMT)]
 */
package vazkii.quark.experimental.features;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.experimental.world.BigCaveGenerator;

public class BiggerCaves extends Feature {

	public static float overallCaveSizeVariance, overallCaveSizeBase;

	public static float bigCaveSizeVariance, bigCaveSizeBase;

	public static boolean generateHugeCaves;
	public static int hugeCaveMaxY, hugeCaveChance;
	public static float hugeCaveSizeVariance, hugeCaveSizeBase;

	@Override
	public void setupConfig() {
		overallCaveSizeVariance = (float) loadPropDouble("Overall Cave Size Variance", "Vanilla value is 3", 7);
		overallCaveSizeBase = (float) loadPropDouble("Overall Cave Size Minimum", "Vanilla value is 0", 0);

		bigCaveSizeVariance = (float) loadPropDouble("Big Cave Size Variance", "Vanilla value is 3", 8);
		bigCaveSizeBase = (float) loadPropDouble("Big Cave Size Minimum", "Vanilla value is 1", 1);

		generateHugeCaves = loadPropBool("Huge Caves Enabled", "", true);
		hugeCaveMaxY = loadPropInt("Huge Cave Maximum Y Level", "", 32);
		hugeCaveChance = loadPropInt("Huge Cave Chance", "Given the value of this config as X, in average, 1 in X caves will be a huge cave", 1800);
		hugeCaveSizeVariance = (float) loadPropDouble("Huge Cave Size Variance", "", 6);
		hugeCaveSizeBase = (float) loadPropDouble("Huge Cave Size Minimum", "", 14);
	}

	@SubscribeEvent
	public void getCaveGenerator(InitMapGenEvent event) {
		if(event.getType() == EventType.CAVE)
			event.setNewGen(new BigCaveGenerator());
	}

	@Override
	public boolean hasTerrainSubscriptions() {
		return true;
	}

}
