package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ConfigHelper;

public class UndergroundBiomeOvergrown extends BasicUndergroundBiome {

	public static double rootChance, dirtChance;
	
	public UndergroundBiomeOvergrown() {
		super(Blocks.MOSSY_COBBLESTONE.getDefaultState(), Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false), null);
	}

	@Override
	public void finalCeilingPass(World world, BlockPos pos) {
		if(world.rand.nextDouble() < rootChance) {
			int count = 0;
			for(int i = 0; i < 20; i++) {
				BlockPos checkPos = pos.add(0, -i, 0);
				if(isFloor(world, checkPos, world.getBlockState(checkPos))) {
					count = i;
					break;
				}
			}
			
			for(int i = 0; i <= count; i++) {
				BlockPos placePos = pos.add(0, -i, 0);
				world.setBlockState(placePos, Blocks.LOG.getDefaultState());
			}
			
		}
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(world.rand.nextDouble() < dirtChance)
			world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
		else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void setupConfig(String category) {
		rootChance = ConfigHelper.loadLegacyPropChance("Root Percentage Chance", category, "Root Chance", "The chance roots will spawn", 0.025);
		dirtChance = ConfigHelper.loadLegacyPropChance("Dirt Percentage Chance", category, "Root Chance", "The chance dirt will spawn", 0.5);
	}

}
