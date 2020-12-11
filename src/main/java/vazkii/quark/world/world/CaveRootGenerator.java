package vazkii.quark.world.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.block.BlockRoots;
import vazkii.quark.world.feature.CaveRoots;

public class CaveRootGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(!CaveRoots.dimensions.canSpawnHere(world) || chunkGenerator instanceof ChunkGeneratorFlat)
			return;
		
		for(int i = 0; i < CaveRoots.chunkAttempts; i++) {
			int x = chunkX * 16 + 8 + random.nextInt(16);
			int z = chunkZ * 16 + 8 + random.nextInt(16);
			int y = random.nextInt(CaveRoots.maxY - CaveRoots.minY) + CaveRoots.minY;
			
			BlockPos pos = new BlockPos(x, y, z);
			if(world.isAirBlock(pos)) {
				for(EnumFacing facing : EnumFacing.HORIZONTALS) {
					BlockPos target = pos.offset(facing);
					if(CaveRoots.roots.canPlaceBlockOnSide(world, pos, facing) && world.isBlockLoaded(pos)) {
						IBlockState state = CaveRoots.roots.getDefaultState().withProperty(BlockRoots.getPropertyFor(facing.getOpposite()), true);
						world.setBlockState(pos, state);
						BlockRoots.growMany(world, pos, state, 0.4F, true);
					}
				}
			}
		}
	}
	
}
