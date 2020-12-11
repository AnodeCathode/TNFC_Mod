/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [24/03/2016, 16:04:11 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

import java.util.Random;

public class MushroomsInSwamps extends Feature {

	private final WorldGenerator bigMushroomGen = new WorldGenBigMushroom();

	public static double bigMushroomsPerChunk;

	@Override
	public void setupConfig() {
		bigMushroomsPerChunk = loadPropDouble("Big Mushroom count per chunk", "Must be an integer if above 1. If below 1, works as a chance to generate one per chunk.", 0.5);
	}

	@SubscribeEvent
	public void decorate(DecorateBiomeEvent.Decorate event) {
		World world = event.getWorld();
		if (event.getPlacementPos() != null) {
			Biome biome = world.getBiome(event.getPlacementPos());
			Random rand = event.getRand();

			if ((biome == Biomes.SWAMPLAND || biome == Biomes.MUTATED_SWAMPLAND) && event.getType() == EventType.BIG_SHROOM) {
				if (bigMushroomsPerChunk < 1 && rand.nextDouble() > bigMushroomsPerChunk)
					return;

				int amount = (int) Math.max(1, bigMushroomsPerChunk);
				for (int i = 0; i < amount; i++) {
					int x = rand.nextInt(16) + 8;
					int y = rand.nextInt(16) + 8;
					bigMushroomGen.generate(world, rand, world.getHeight(event.getPlacementPos().add(x, 0, y)));
				}

				event.setResult(Result.DENY);
			}
		}
	}

	@Override
	public boolean hasTerrainSubscriptions() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Big Mushrooms In Swamps";
	}

}
