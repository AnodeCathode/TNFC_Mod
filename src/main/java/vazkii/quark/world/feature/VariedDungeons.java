package vazkii.quark.world.feature;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class VariedDungeons extends Feature {

	public static DimensionConfig dimConfig;
	public static ResourceLocation lootTable;
	public static int tries;

	@Override
	public void setupConfig() {
		dimConfig = new DimensionConfig(configCategory);

		String lootTableStr = loadPropString("Custom Loot Table", "Set this to anything other than an empty line to load a custom loot table for the dungeons.", "");
		lootTable = lootTableStr.isEmpty() ? null : new ResourceLocation(lootTableStr);
		
		tries = loadPropInt("Tries Per Chunk", "How many times per chunk will the generator try to place a dungeon. The higher, the more dungeons you'll get", 7);
	}

	@SubscribeEvent
	public void onDungeonSpawn(PopulateChunkEvent.Populate event) {
		if(event.getType() != EventType.DUNGEON)
			return;

		if (!dimConfig.canSpawnHere(event.getWorld()))
			return;

		int i = event.getChunkX() * 16;
		int j = event.getChunkZ() * 16;

		BlockPos blockpos = new BlockPos(i, 0, j);
		World world = event.getWorld();
		Random rand = event.getRand();

		if(world instanceof WorldServer)
			for(int k = 0; k < tries; k++) {
				int x = rand.nextInt(16) + 8;
				int y = rand.nextInt(256);
				int z = rand.nextInt(16) + 8;
				BlockPos generatePos = blockpos.add(x, y, z);
				if(couldDungeonGenerate(world, rand, generatePos)) {
					placeDungeonAt((WorldServer) world, rand, generatePos);
					break;
				}
			}
	}

	public boolean couldDungeonGenerate(World worldIn, Random rand, BlockPos position) {
		int i = 3;
		int j = rand.nextInt(2) + 2;
		int k = -j - 1;
		int l = j + 1;
		int i1 = -1;
		int j1 = 4;
		int k1 = rand.nextInt(2) + 2;
		int l1 = -k1 - 1;
		int i2 = k1 + 1;
		int j2 = 0;

		for(int k2 = k; k2 <= l; ++k2) {
			for(int l2 = -1; l2 <= 4; ++l2) {
				for(int i3 = l1; i3 <= i2; ++i3) {
					BlockPos blockpos = position.add(k2, l2, i3);
					Material material = worldIn.getBlockState(blockpos).getMaterial();
					boolean flag = material.isSolid();

					if(l2 == -1 && !flag)
						return false;

					if(l2 == 4 && !flag)
						return false;

					if((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up()))
						++j2;
				}
			}
		}

		return j2 >= 1 && j2 <= 5;
	}

	public void placeDungeonAt(WorldServer world, Random rand, BlockPos position) {
		int dungeonType = rand.nextInt(10);

		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().getTemplate(server, new ResourceLocation("quark", "dungeon_" + dungeonType));
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);

		BlockPos size = template.getSize();
		for(int x = 0; x < size.getX(); x++)
			for(int y = 0; y < size.getY(); y++)
				for(int z = 0; z < size.getZ(); z++) {
					BlockPos checkPos = position.add(Template.transformedBlockPos(settings, new BlockPos(x, y, z)));
					IBlockState checkState = world.getBlockState(checkPos);
					if(checkState.getBlockHardness(world, checkPos) == -1 || world.canBlockSeeSky(checkPos))
						return; // Obstructed or exposed, can't generate here
				}

		template.addBlocksToWorld(world, position, settings);

		int spawners = 0;
		List<BlockPos> chests = new ArrayList<>();
		Map<BlockPos, String> dataBlocks = template.getDataBlocks(position, settings);

		for(Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			BlockPos pos = entry.getKey();
			String data = entry.getValue();

			if(data.equals("spawner")) 
			{
				spawners++;
				world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
				TileEntity tile = world.getTileEntity(pos);

				if(tile instanceof TileEntityMobSpawner) 
				{
					if(DungeonTweaksCompat.isLoaded) 
					{
						DungeonTweaksCompat.fireDungeonSpawn(tile, world, rand);
					}
					else 
					{
						((TileEntityMobSpawner) tile).getSpawnerBaseLogic().setEntityId(DungeonHooks.getRandomDungeonMob(rand));
					}
				}
			}
			else if(data.equals("chest"))
				chests.add(pos);
		}

		int maxChests = spawners * 2 + rand.nextInt(spawners + 2);
		while(chests.size() > maxChests) {
			int i = rand.nextInt(chests.size());
			BlockPos chestPos = chests.get(i);
			chests.remove(i);
			world.setBlockToAir(chestPos);
		}

		for(BlockPos pos : chests) {
			world.setBlockState(pos, Blocks.CHEST.correctFacing(world, pos, Blocks.CHEST.getDefaultState()), 2);
			TileEntity tile = world.getTileEntity(pos);

			if(tile instanceof TileEntityChest) {
				if(lootTable == null)
					((TileEntityChest) tile).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
				else ((TileEntityChest) tile).setLootTable(lootTable, rand.nextLong());
			}
		}
	}


	@Override
	public boolean hasTerrainSubscriptions() {
		return true;
	}

}
