package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomePrismarine extends BasicUndergroundBiome {

	public static double seaLanternChance, waterChance;
	private IBlockState lanternState;
	public static boolean spawnElderPrismarine;
	
	public UndergroundBiomePrismarine() {
		super(Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState());
		lanternState = Blocks.SEA_LANTERN.getDefaultState();
	}
	
	public void update() {
		boolean elder = UndergroundBiomes.elderPrismarineEnabled && spawnElderPrismarine;
		
		IBlockState prismarineState = (elder ? UndergroundBiomes.elder_prismarine : Blocks.PRISMARINE).getDefaultState();
		ceilingState = floorState = wallState = prismarineState;
		
		lanternState = (elder ? UndergroundBiomes.elder_sea_lantern : Blocks.SEA_LANTERN).getDefaultState();
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		super.fillWall(world, pos, state);
		
		if(world.rand.nextDouble() < seaLanternChance)
			world.setBlockState(pos, lanternState, 2);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(world.rand.nextDouble() < waterChance && !isBorder(world, pos))
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void setupConfig(String category) {
		seaLanternChance = ConfigHelper.loadLegacyPropChance("Sea Lantern Percentage Chance", category, "Sea Lantern Chance", "The chance sea lanterns will spawn", 0.0085);
		waterChance = ConfigHelper.loadLegacyPropChance("Water Percentage Chance", category, "Water Chance", "The chance water will spawn", 0.25);
		spawnElderPrismarine = ModuleLoader.config.getBoolean("Spawn Elder Prismarine", category, true, "Set to false to spawn regular prismarine instead of elder prismarine (even if the block is enabled)");
	}
	
	
}
