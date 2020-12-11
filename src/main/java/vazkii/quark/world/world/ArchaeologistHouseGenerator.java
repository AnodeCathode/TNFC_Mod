package vazkii.quark.world.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkGeneratorFlat;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.arl.util.RotationHandler;
import vazkii.quark.world.entity.EntityArchaeologist;
import vazkii.quark.world.feature.Archaeologist;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class ArchaeologistHouseGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(!(chunkGenerator instanceof ChunkGeneratorFlat) && Archaeologist.dims.canSpawnHere(world) && random.nextDouble() < Archaeologist.chance) {
			int x = chunkX * 16 + 8 + random.nextInt(16);
			int z = chunkZ * 16 + 8 + random.nextInt(16);
			int y = random.nextInt(Archaeologist.maxY - Archaeologist.minY) + Archaeologist.minY;
			
			BlockPos pos = new BlockPos(x, y, z);
			setHouseAt(random, world, pos);
		}
	}
	
	private void setHouseAt(Random random, World world, BlockPos pos) {
		if(world.isAirBlock(pos) && world instanceof WorldServer) {
			BlockPos down = pos.down();
			while(world.isAirBlock(down)) {
				down = down.down();
				if (world.isOutsideBuildHeight(down))
					return;
			}
			
			EnumFacing facing = EnumFacing.HORIZONTALS[random.nextInt(EnumFacing.HORIZONTALS.length)];
			BlockPos placePos = down.up();
			while(world.isAirBlock(placePos)) {
				placePos = placePos.offset(facing);
				
				if(world.isOutsideBuildHeight(placePos) || !world.isBlockLoaded(placePos))
					return;
			}
			
			IBlockState placeState = world.getBlockState(placePos);
			if(placeState.getMaterial() == Material.ROCK && placeState.isFullBlock()) {
				generateHouse((WorldServer) world, placePos, facing);
			}
		}

	}

	private void generateHouse(WorldServer world, BlockPos pos, EnumFacing face) {
		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().getTemplate(server, Archaeologist.HOUSE_STRUCTURE);
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(RotationHandler.getRotationFromFacing(face));
		
		BlockPos placePos = pos.offset(face, 7);
		template.addBlocksToWorld(world, placePos, settings);

		Map<BlockPos, String> dataBlocks = template.getDataBlocks(placePos, settings);
		for(Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			String s = entry.getValue();
			if(s.equals("villager")) {
				BlockPos villagerPos = entry.getKey();
				world.setBlockToAir(villagerPos);
				
				EntityArchaeologist e = new EntityArchaeologist(world);
				e.setPosition(villagerPos.getX(), villagerPos.getY(), villagerPos.getZ());
				world.spawnEntity(e);
			}
		}
	}
	
}
