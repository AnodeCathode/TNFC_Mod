package vazkii.quark.world.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.world.block.BlockCrystal;
import vazkii.quark.world.feature.CrystalCaves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrystalCaveGenerator implements IWorldGenerator {

	private final DimensionConfig dims;
	
	public CrystalCaveGenerator(DimensionConfig dims) {
		this.dims = dims;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(random.nextDouble() > CrystalCaves.crystalCaveRarity || !dims.canSpawnHere(world))
			return;

		int x = chunkX * 16 + random.nextInt(16);
		int z = chunkZ * 16 + random.nextInt(16);

		for(int i = 15; i < 50; i++) {
			BlockPos pos = new BlockPos(x, i, z);
			BlockPos belowPos = pos.down();
			IBlockState state = world.getBlockState(pos);
			IBlockState stateBelow = world.getBlockState(belowPos);
			if(state.getBlock().isAir(state, world, pos) && stateBelow.getBlock() == Blocks.STONE) {
				makeCrystalCaveAt(world, pos, random);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void makeCrystalCaveAt(World world, BlockPos source, Random rand) {
		double expandX = (rand.nextDouble() - 0.5) * 2;
		double expandY = (rand.nextDouble() - 0.5) * 0.1F;
		double expandZ = (rand.nextDouble() - 0.5) * 2;
		
		double curveAngle = rand.nextDouble() *  Math.PI * 2;
		double curveRatio = rand.nextDouble() * 0.25 + 0.1;
		double curveX = Math.cos(curveAngle) * curveRatio;
		double curveY = (rand.nextFloat() - 0.5F) * 0.05F;
		double curveZ = Math.sin(curveAngle) * curveRatio;

		BlockPos hollowingCenter = source;
		Vec3d expansion = new Vec3d(expandX, expandY, expandZ).normalize();
		Vec3d curvature = new Vec3d(curveX, curveY, curveZ);

		int color1 = rand.nextInt(BlockCrystal.Variants.values().length);
		int color2;
		do {
			color2 = rand.nextInt(BlockCrystal.Variants.values().length);
		} while(color2 == color1);

		IBlockState crystal1 = CrystalCaves.crystal.getStateFromMeta(color1);
		IBlockState crystal2 = CrystalCaves.crystal.getStateFromMeta(color2);

		int length = 12 + rand.nextInt(10);
		int size = 4 + rand.nextInt(3);
		for(int i = 0; i < length; i++) {
			hollowOut(world, hollowingCenter, rand, size, crystal1, crystal2);
			
			BlockPos currentCenter = hollowingCenter;
			hollowingCenter = currentCenter.add(expansion.x * size * 0.5, expansion.y * size * 0.5, expansion.z * size * 0.5);
			
			if(hollowingCenter.getY() < 10) {
				expansion = new Vec3d(expansion.x, -expansion.y, expansion.z);
				curvature = new Vec3d(curvature.x, -curvature.y, curvature.z);
			}
			
			expansion = expansion.add(curvature).normalize();
		}
	}

	private void hollowOut(World world, BlockPos source, Random rand, int width, IBlockState crystal1, IBlockState crystal2) {
		List<BlockPos> crystals = new ArrayList<>();
		
		int max = width * width;
		for(int i = -width; i <= width; i++)
			for(int j = -width; j <= width; j++)
				for(int k = -width; k <= width; k++) {
					BlockPos pos = source.add(i, j, k);
					int dist = i * i + j * j + k * k;

					if(dist < max) {
						IBlockState state = world.getBlockState(pos);

						if(state.getBlockHardness(world, pos) != -1)
							world.setBlockToAir(pos);
					} else if(dist - 1 < max)
						crystals.add(pos);
				}
		
		for(BlockPos pos : crystals) {
			if(rand.nextDouble() < CrystalCaves.crystalRate)
				makeCrystal(world, pos, rand, rand.nextBoolean() ? crystal1 : crystal2);
			else if(rand.nextDouble() < CrystalCaves.oreChance) {
				IBlockState stateAt = world.getBlockState(pos);
				Block blockAt = stateAt.getBlock();
				if(blockAt.isAir(stateAt, world, pos) || blockAt == CrystalCaves.crystal || stateAt.getBlockHardness(world, pos) == -1)
					continue;
				
				IBlockState oreState = Blocks.GOLD_ORE.getDefaultState();
				if(rand.nextInt(3) == 0) {
					if(rand.nextInt(3) == 0)
						oreState = Blocks.DIAMOND_ORE.getDefaultState();
					else oreState = Blocks.EMERALD_ORE.getDefaultState();
				}
				
				world.setBlockState(pos, oreState);
			}
		}
			
	}

	private void makeCrystal(World world, BlockPos source, Random rand, IBlockState crystal) {
		boolean up = rand.nextBoolean();
		EnumFacing shift = up ? EnumFacing.UP : EnumFacing.DOWN;
		
		BlockPos startPos = source;
		IBlockState state = world.getBlockState(startPos);
		if(state.getBlock() == CrystalCaves.crystal)
			return;
		
		int tests = 0;
		
		while(state.getBlock().isAir(state, world, startPos)) {
			startPos = startPos.offset(shift.getOpposite());
			state = world.getBlockState(startPos);

			tests++;
			if(tests >= 10)
				return;
		}

		int size = 3 + rand.nextInt(4);
		
		BlockPos pos = startPos;
		for(int i = 0; i < size; i++) {
			IBlockState stateAt = world.getBlockState(pos);
			if(stateAt.getBlockHardness(world, pos) == -1)
				break;
			
			world.setBlockState(pos, crystal);
			pos = pos.offset(shift);
		}
	}

}
