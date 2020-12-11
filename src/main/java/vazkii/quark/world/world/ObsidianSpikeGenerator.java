package vazkii.quark.world.world;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.decoration.feature.BlazeLantern;
import vazkii.quark.world.feature.NetherObsidianSpikes;
import vazkii.quark.world.feature.NetherSmoker;

public class ObsidianSpikeGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.isNether() && random.nextFloat() < NetherObsidianSpikes.chunkChance) {
			for(int i = 0; i < NetherObsidianSpikes.triesPerChunk; i++) {
				int x = chunkX * 16 + random.nextInt(16) + 8;
				int z = chunkZ * 16 + random.nextInt(16) + 8;

				BlockPos pos = new BlockPos(x, 50, z);
				
				while(pos.getY() > 10) {
					IBlockState state = world.getBlockState(pos);
					if(state.getBlock() == Blocks.LAVA) {
						placeSpikeAt(world, pos);
						break;
					}
					pos = pos.down();
				}
			}
		}
	}
	
	public static void placeSpikeAt(World world, BlockPos pos) {
		int heightBelow = 10;
		int heightBottom = 3 + world.rand.nextInt(3);
		int heightMiddle = 2 + world.rand.nextInt(4);
		int heightTop = 2 + world.rand.nextInt(3);
		
		boolean addSpawner = false;
		if(world.rand.nextFloat() < NetherObsidianSpikes.bigSpikeChance) {
			heightBottom += 7;
			heightMiddle += 8;
			heightTop += 4;
			addSpawner = NetherObsidianSpikes.bigSpikeSpawners;
		}
		
		int checkHeight = heightBottom + heightMiddle + heightTop + 2;
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++)
				for(int k = 0; k < checkHeight; k++) {
					BlockPos checkPos = pos.add(i - 2, k, j - 2);
					if(!(world.isAirBlock(checkPos) || world.getBlockState(checkPos).getMaterial() == Material.LAVA))
						return;
				}
		
		IBlockState obsidian = Blocks.OBSIDIAN.getDefaultState();
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				for(int k = 0; k < heightBottom + heightBelow; k++) {
					BlockPos placePos = pos.add(i - 1, k - heightBelow, j - 1);

					if(world.getBlockState(placePos).getBlockHardness(world, placePos) != -1)
						world.setBlockState(placePos, obsidian);
				}
		
		for(int i = 0; i < heightMiddle; i++) {
			BlockPos placePos = pos.add(0, heightBottom + i, 0);
			
			world.setBlockState(placePos, obsidian);
			for(EnumFacing face : EnumFacing.HORIZONTALS)
				world.setBlockState(placePos.offset(face), obsidian);
		}
		
		for(int i = 0; i < heightTop; i++) {
			BlockPos placePos = pos.add(0, heightBottom + heightMiddle + i, 0);
			world.setBlockState(placePos, obsidian);
			
			if(addSpawner && i == 0) {
				world.setBlockState(placePos, BlazeLantern.blaze_lantern == null ? Blocks.GLOWSTONE.getDefaultState() : BlazeLantern.blaze_lantern.getDefaultState());
				
				placePos = placePos.down();
				world.setBlockState(placePos, Blocks.MOB_SPAWNER.getDefaultState());
				((TileEntityMobSpawner) world.getTileEntity(placePos)).getSpawnerBaseLogic().setEntityId(new ResourceLocation("minecraft", "blaze"));
				
				placePos = placePos.down();
				world.setBlockState(placePos, Blocks.CHEST.getDefaultState());
				((TileEntityChest) world.getTileEntity(placePos)).setLootTable(new ResourceLocation("minecraft", "chests/nether_bridge"), world.rand.nextLong());
			}
		}
	}

}
