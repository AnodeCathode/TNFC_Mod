package vazkii.quark.world.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.block.BlockVariantSapling;
import vazkii.quark.world.feature.TreeVariants;
import vazkii.quark.world.world.tree.WorldGenSakuraTree;

import java.util.Random;
import java.util.Set;

public class SakuraTreeGenerator implements IWorldGenerator {

	private final WorldGenSakuraTree treeGen = new WorldGenSakuraTree(false);
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		double chance = TreeVariants.sakuraChance;
		
		while(random.nextDouble() < chance) {
			int x = chunkX * 16 + random.nextInt(16) + 8;
			int z = chunkZ * 16 + random.nextInt(16) + 8;
			BlockPos xzPos = new BlockPos(x, 0 , z);
			
			Biome biome = world.getBiome(xzPos);
			Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biome);
			if(types.contains(BiomeDictionary.Type.MOUNTAIN) && !types.contains(BiomeDictionary.Type.FOREST)) {
				BlockPos pos = world.getTopSolidOrLiquidBlock(xzPos).down();
				IBlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				boolean snow = (block == Blocks.SNOW_LAYER);
				
				if(snow) {
					pos = pos.down();
					state = world.getBlockState(pos);
					block = state.getBlock();
				}
				
				if(block.canSustainPlant(state, world, pos, EnumFacing.UP, ((BlockVariantSapling) TreeVariants.variant_sapling))) {
					BlockPos placePos = pos.up();
					if(snow)
						world.setBlockToAir(placePos);
					
					treeGen.generate(world, random, placePos);
				}
			}
			
			chance -= 1F;
		}
	}

}
