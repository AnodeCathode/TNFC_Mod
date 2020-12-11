/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 16:06:27 (GMT)]
 */
package vazkii.quark.world.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.ClayGeneration;

import java.util.Random;

public class ClayGenerator implements IWorldGenerator {

	private final int clusterCount;
	private final WorldGenMinable generator;

	public ClayGenerator(int clusterSize, int clusterCount) {
		this.clusterCount = clusterCount;

		generator = new WorldGenMinable(Blocks.CLAY.getDefaultState(), clusterSize);
	}

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		for(int i = 0; i < clusterCount; i++) {
			int x = chunkX * 16 + rand.nextInt(16);
			int y = ClayGeneration.minHeight + rand.nextInt(ClayGeneration.maxHeight - ClayGeneration.minHeight);
			int z = chunkZ * 16 + rand.nextInt(16);

			generator.generate(world, rand, new BlockPos(x, y, z));
		}
	}

}
