package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomeSpiderNest extends BasicUndergroundBiome {

	public static double floorCobwebChance, ceilingCobwebChance, caveSpiderSpawnerChance, nestCobwebChance, nestCobwebRange, cobbedstoneChance;
	
	public UndergroundBiomeSpiderNest() {
		super(Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState());
	}

	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		super.fillCeiling(world, pos, state);
		placeCobweb(world, pos, EnumFacing.DOWN, ceilingCobwebChance);
	}
	
	private void placeCobweb(World world, BlockPos pos, EnumFacing off, double chance) {
		if(world.rand.nextDouble() < chance) {
			BlockPos placePos = off == null ? pos : pos.offset(off);
			world.setBlockState(placePos, Blocks.WEB.getDefaultState());
		}
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		if(UndergroundBiomes.cobbedstoneEnabled && world.rand.nextDouble() < cobbedstoneChance)
			world.setBlockState(pos, UndergroundBiomes.cobbedstoneState, 2);
		else super.fillWall(world, pos, state);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(UndergroundBiomes.cobbedstoneEnabled && world.rand.nextDouble() < cobbedstoneChance)
			world.setBlockState(pos, UndergroundBiomes.cobbedstoneState, 2);
		else super.fillFloor(world, pos, state);
		
		placeCobweb(world, pos, EnumFacing.UP, floorCobwebChance);
	}
	
	@Override
	public boolean hasDungeon() {
		return true;
	}

	@Override
	public void spawnDungeon(WorldServer world, BlockPos pos, EnumFacing face) {
		BlockPos spawnerPos = pos.offset(face);
		world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState());
		
		Class<? extends Entity> e = EntitySpider.class;
		if(world.rand.nextDouble() < caveSpiderSpawnerChance)
			e = EntityCaveSpider.class;
		TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(spawnerPos);
		if (spawner != null)
		spawner.getSpawnerBaseLogic().setEntityId(EntityList.getKey(e));
		
		int range = 3;
		for(int x = -range; x < range + 1; x++)
			for(int y = -range; y < range + 1; y++)
				for(int z = -range; z < range + 1; z++) {
					BlockPos cobwebPos = spawnerPos.add(x, y, z);
					IBlockState stateAt = world.getBlockState(cobwebPos);
					if(stateAt.getBlock().isAir(stateAt, world, cobwebPos) || stateAt.getBlock().isReplaceable(world, cobwebPos))
						placeCobweb(world, cobwebPos, null, nestCobwebChance);
				}
	}
	
	@Override
	public void setupConfig(String category) {
		floorCobwebChance = ConfigHelper.loadLegacyPropChance("Floor Cobweb Percentage Chance", category, "Floor Cobweb Chance", "The chance cobwebs will spawn", 0.033);
		ceilingCobwebChance = ConfigHelper.loadLegacyPropChance("Ceiling Cobweb Percentage Chance", category, "Ceiling Cobweb Chance", "The chance ceiling cobwebs will spawn", 0.1);
		caveSpiderSpawnerChance = ConfigHelper.loadLegacyPropChance("Cave Spider Spawner Percentage Chance", category, "Cave Spider Spawner Chance", "The chance for a spider spawner to be a cave spider spawner instead", 0.25);
		nestCobwebChance = ConfigHelper.loadLegacyPropChance("Nest Cobweb Percentage Chance", category, "Nest Cobweb Chance", "The chance cobwebs will spawn in nests", 0.5);
		nestCobwebRange = ModuleLoader.config.getInt("Nest Cobweb Range", category, 3, 0, Integer.MAX_VALUE, "The range for cobwebs to be spawned in spider nests");
		cobbedstoneChance = ConfigHelper.loadPropChance("Cobbedstone Chance", category, "The chance for cobbedstone to replace cobblestone in the floor and walls", 0.3);
	}

}

