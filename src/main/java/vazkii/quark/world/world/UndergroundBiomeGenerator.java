package vazkii.quark.world.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.world.feature.UndergroundBiomes.UndergroundBiomeInfo;

import java.util.*;

public class UndergroundBiomeGenerator extends MultiChunkFeatureGenerator {

	public final UndergroundBiomeInfo info;
	
	private final long seedXor;
	
	public UndergroundBiomeGenerator(UndergroundBiomeInfo info) {
		this.info = info;
		
		seedXor = info.biome.getClass().toString().hashCode();
	}
	
	@Override
	public boolean canGenerate(World world) {
		return info.enabled && info.dims.canSpawnHere(world);
	}
	
	@Override
	public int getFeatureRadius() {
		return (int) Math.ceil(Math.max(info.minXSize + info.xVariation, info.minZSize + info.zVariation));
	}

	@Override
	public void generateChunkPart(BlockPos src, Random random, int chunkX, int chunkZ, World world) {
		int radiusX = info.minXSize + random.nextInt(info.xVariation);
		int radiusY = info.minYSize + random.nextInt(info.yVariation);
		int radiusZ = info.minZSize + random.nextInt(info.zVariation);
		apply(world, src, random, chunkX, chunkZ, radiusX, radiusY, radiusZ);
	}

	@Override
	public BlockPos[] getSourcesInChunk(Random random, int chunkX, int chunkZ) {
		if(info.rarity > 0 && random.nextInt(info.rarity) == 0) {
			return new BlockPos[] {
				new BlockPos(chunkX * 16 + random.nextInt(16), info.minY + random.nextInt(info.maxY - info.minY), chunkZ * 16 + random.nextInt(16))
			};
		}
		
		return new BlockPos[0];
	}
	
	@Override
	public long modifyWorldSeed(long seed) {
		return seed ^ seedXor;
	}
	
	@Override
	public boolean isSourceValid(World world, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		return BiomeTypeConfigHandler.biomeTypeIntersectCheck(info.types, biome) && info.biome.isValidBiome(biome);
	}
	
	public void apply(World world, BlockPos center, Random random, int chunkX, int chunkZ, int radiusX, int radiusY, int radiusZ) {
		int centerX = center.getX();
		int centerY = center.getY();
		int centerZ = center.getZ();

		double radiusX2 = radiusX * radiusX;
		double radiusY2 = radiusY * radiusY;
		double radiusZ2 = radiusZ * radiusZ;
		
		UndergroundBiomeGenerationContext context = new UndergroundBiomeGenerationContext();

		forEachChunkBlock(chunkX, chunkZ, center.getY() - radiusY, center.getY() + radiusY, (pos) -> {
			int x = pos.getX() - center.getX();
			int y = pos.getY() - center.getY();
			int z = pos.getZ() - center.getZ();
			
			double distX = x * x;
			double distY = y * y;
			double distZ = z * z;
			boolean inside = distX / radiusX2 + distY / radiusY2 + distZ / radiusZ2 <= 1;
			
			if(inside)
				info.biome.fill(world, center.add(x, y, z), context);
		});

		context.floorList.forEach(pos -> info.biome.finalFloorPass(world, pos));
		context.ceilingList.forEach(pos -> info.biome.finalCeilingPass(world, pos));
		context.wallMap.keySet().forEach(pos -> info.biome.finalWallPass(world, pos));
		context.insideList.forEach(pos -> info.biome.finalInsidePass(world, pos));
		
		if(info.biome.hasDungeon() && world instanceof WorldServer && random.nextDouble() < info.biome.dungeonChance) {
			List<BlockPos> candidates = new ArrayList<>(context.wallMap.keySet());
			candidates.removeIf(pos -> {
				BlockPos down = pos.down();
				IBlockState state = world.getBlockState(down);
				return info.biome.isWall(world, down, state) || state.getBlock().isAir(state, world, down);
			});
			
			if(!candidates.isEmpty()) {
				BlockPos pos = candidates.get(world.rand.nextInt(candidates.size()));
				
				EnumFacing border = context.wallMap.get(pos);
				if(border != null)
					info.biome.spawnDungeon((WorldServer) world, pos, border);
			}
		}
	}
	
	public static class UndergroundBiomeGenerationContext {
		
		public final List<BlockPos> floorList = new LinkedList<>();
		public final List<BlockPos> ceilingList = new LinkedList<>();
		public final List<BlockPos> insideList = new LinkedList<>();
		
		public final Map<BlockPos, EnumFacing> wallMap = new HashMap<>();
		
	}
}
