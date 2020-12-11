package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.BrokenPortalGenerator;

public class BrokenNetherPortals extends Feature {

	public static double chance;
	
	DimensionConfig dims;
	
	@Override
	public void setupConfig() {
		chance = loadPropChance("Spawn Chance", "The chance a broken portal will try to spawn per chunk (1 is 100%, 0 is 0%)", 0.02);
		
		dims = new DimensionConfig(configCategory, "-1");
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new BrokenPortalGenerator(dims), 1);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
