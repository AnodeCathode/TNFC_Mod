package vazkii.quark.world.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.ObsidianSpikeGenerator;

public class NetherObsidianSpikes extends Feature {
	
	public static double chunkChance, bigSpikeChance;
	public static int triesPerChunk;
	public static boolean bigSpikeSpawners;
	
	@Override
	public void setupConfig() {
		chunkChance = loadPropChance("Chance Per Chunk", "The chance for a chunk to contain spikes (1 is 100%, 0 is 0%)", 0.04);
		triesPerChunk = loadPropInt("Tries Per Chunk", "Should a chunk have spikes, how many would the generator try to place", 4);
		bigSpikeChance = loadPropChance("Big Spike Chance", "The chance for a spike to be big (1 is 100%, 0 is 0%)", 0.1);
		bigSpikeSpawners = loadPropBool("Big Spikes Have Spawners", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ObsidianSpikeGenerator(), 0);
	}
	
}
