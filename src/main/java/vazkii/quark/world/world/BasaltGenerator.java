/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [20/03/2016, 15:17:15 (GMT)]
 */
package vazkii.quark.world.world;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen.StoneInfo;

public class BasaltGenerator extends StoneInfoBasedGenerator implements IWorldGenerator {

	public BasaltGenerator(Supplier<StoneInfo> infoSupplier) {
		super(infoSupplier, Basalt.basalt.getDefaultState(), "basalt");
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		generate(chunkX, chunkZ, world);
	}
	
	@Override
	public boolean canPlaceBlock(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock(); 
		return block == Blocks.STONE || block == Blocks.NETHERRACK;
	}

}
