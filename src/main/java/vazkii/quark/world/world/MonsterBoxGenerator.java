package vazkii.quark.world.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.MonsterBoxes;

import java.util.Random;

public class MonsterBoxGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(!MonsterBoxes.dimensions.canSpawnHere(world) || chunkGenerator instanceof ChunkGeneratorFlat)
			return;
		
		float chance =  MonsterBoxes.chunkChance;
		
		while(world.rand.nextFloat() <= chance) {
			int x = chunkX * 16 + 8 + random.nextInt(16);
			int y = MonsterBoxes.minY + random.nextInt(MonsterBoxes.maxY - MonsterBoxes.minY + 1);
			int z = chunkZ * 16 + 8 + random.nextInt(16);
			
			BlockPos pos = new BlockPos(x, y, z);
			if(world.isAirBlock(pos)) {
				BlockPos testPos = pos;
				IBlockState testState;
				do {
					testPos = testPos.down();
					testState = world.getBlockState(testPos);
				} while(testState.getMaterial() != Material.ROCK && testPos.getY() >= MonsterBoxes.minY);
				
				BlockPos placePos = testPos.up();
				if(testPos.getY() >= MonsterBoxes.minY && world.isAirBlock(placePos))
					world.setBlockState(placePos, MonsterBoxes.monster_box.getDefaultState());
			}
			
			chance -=MonsterBoxes.chunkChance;
		}
	}

}
