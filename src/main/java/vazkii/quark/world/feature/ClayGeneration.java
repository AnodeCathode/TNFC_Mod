/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 16:03:14 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.ClayGenerator;

public class ClayGeneration extends Feature {

	public static int minHeight, maxHeight;
	public static int clusterSize, clusterCount;

	@Override
	public void setupConfig() {
		clusterSize = loadPropInt("Cluster size", "", 20);
		clusterCount = loadPropInt("Cluster count", "", 3);
		minHeight = loadPropInt("Min Height", "", 20);
		maxHeight = loadPropInt("Max Height", "", 60);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ClayGenerator(clusterSize, clusterCount), 0);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Underground Clay";
	}

}
