package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.ModuleLoader;

import java.util.Map;
import java.util.Map.Entry;

public class UndergroundBiomeSandstone extends BasicUndergroundBiome {

	public static final ResourceLocation HUSK_GRAVE_STRUCTURE = new ResourceLocation("quark", "husk_grave");

	public static double stalactiteChance, chiseledSandstoneChance, deadBushChance;
	public static boolean enableSand, allowGenInMesa;
	
	public UndergroundBiomeSandstone() {
		super(Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState());
	}
	
	@Override
	public boolean isValidBiome(Biome biome) {
		return allowGenInMesa || !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA);
	}
	
	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		if(world.rand.nextDouble() < stalactiteChance)
			world.setBlockState(pos.down(), ceilingState, 2);
		
		super.fillCeiling(world, pos, state);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(enableSand && world.rand.nextBoolean()) {
			world.setBlockState(pos, Blocks.SAND.getDefaultState(), 2);
			if(world.rand.nextDouble() < deadBushChance)
				world.setBlockState(pos.up(), Blocks.DEADBUSH.getDefaultState(), 2);
		} else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		if(world.rand.nextDouble() < chiseledSandstoneChance)
			world.setBlockState(pos, wallState.withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED), 2);
		else super.fillWall(world, pos, state);
	}
	
	@Override
	public boolean hasDungeon() {
		return true;
	}
	
	@Override
	public void spawnDungeon(WorldServer world, BlockPos pos, EnumFacing side) {
		if(side == null)
			side = EnumFacing.NORTH;
		
		switch(side) {
		case NORTH:
			pos = pos.add(3, -7, 6);
			break;
		case SOUTH:
			pos = pos.add(-3, -7, -6);
			break;
		case EAST:
			pos = pos.add(-6, -7, 3);
			break;
		case WEST:
			pos = pos.add(6, -7, -3);
			break;
		default: break; 
		}
		
		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().getTemplate(server, HUSK_GRAVE_STRUCTURE);
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(rotationFromFacing(side.getOpposite()));
		
		template.addBlocksToWorld(world, pos, settings);

		Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);
		for(Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			BlockPos dataPos = entry.getKey();
			switch(entry.getValue()) {
			case "spawner":
				world.setBlockState(dataPos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
				TileEntity spawner = world.getTileEntity(dataPos);

				if(spawner instanceof TileEntityMobSpawner)
					((TileEntityMobSpawner) spawner).getSpawnerBaseLogic().setEntityId(EntityList.getKey(EntityZombie.class));
				break;
			case "chest":
				IBlockState chestState = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
				world.setBlockState(dataPos, chestState);

				TileEntity chest = world.getTileEntity(dataPos);
				if(chest instanceof TileEntityLockableLoot)
					((TileEntityLockableLoot) chest).setLootTable(LootTableList.CHESTS_DESERT_PYRAMID, world.rand.nextLong());
				break;
			}
		}
	}
	
	@Override
	public void setupConfig(String category) {
		stalactiteChance = ConfigHelper.loadLegacyPropChance("Stalactite Percentage Chance", category, "Stalactite Chance", "The chance stalactites will spawn", 0.1);
		chiseledSandstoneChance = ConfigHelper.loadLegacyPropChance("Chiseled Sandstone Percentage Chance", category, "Chiseled Sandstone Chance", "The chance chiseled sandstone will spawn", 0.1);
		deadBushChance = ConfigHelper.loadLegacyPropChance("Dead Bush Percentage Chance", category, "Dead Bush Chance", "The chance dead bushes will spawn", 0.05);
		enableSand = ModuleLoader.config.getBoolean("Enable Sand Floors", category, true, "");
		allowGenInMesa = ModuleLoader.config.getBoolean("Allow in Mesa biomes", category, false, "");
	}
	
}
