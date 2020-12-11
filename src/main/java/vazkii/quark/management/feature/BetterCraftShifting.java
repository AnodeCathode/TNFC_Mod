package vazkii.quark.management.feature;

import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BetterCraftShifting extends Feature {

	public static boolean enableCraftingTable, enableVillager;
	
	@Override
	public void setupConfig() {
		enableCraftingTable = loadPropBool("Enable Crafting Table", "", true);
		enableVillager = loadPropBool("Enable Villager", "", true);
	}
	
	public static int getMaxInventoryBoundaryCrafting(int min, int max) {
		if(!ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) || !enableCraftingTable || min != 37 || max != 46)
			return max;

		return 10;
	}
	
	public static int getMaxInventoryBoundaryVillager(int min, int max) {
		if(!ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) || !enableVillager || min != 30 || max != 39)
			return max;
		
		return 1;
	}

	public static int getMinInventoryBoundaryCrafting(int min, int max) {
		if(!ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) || !enableCraftingTable || min != 37 || max != 46)
			return min;

		return 1;
	}

	public static int getMinInventoryBoundaryVillager(int min, int max) {
		if(!ModuleLoader.isFeatureEnabled(BetterCraftShifting.class) || !enableVillager || min != 30 || max != 39)
			return min;

		return 0;
	}
	
}
