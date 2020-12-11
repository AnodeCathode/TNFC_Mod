package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.ModuleLoader;

public class UndergroundBiomeSlime extends BasicUndergroundBiome {

	public static double slimeBlockChance;
	public static boolean waterFloor;
	
	public UndergroundBiomeSlime() {
		super(Blocks.WATER.getDefaultState(), null, null);
	}
	
	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		IBlockState setState = Blocks.STAINED_HARDENED_CLAY.getDefaultState();
		switch(world.rand.nextInt(7)) {
		case 0: case 1: case 2:
			setState = setState.withProperty(BlockColored.COLOR, EnumDyeColor.GREEN);
			break;
		case 3: case 4: case 5:
			setState = setState.withProperty(BlockColored.COLOR, EnumDyeColor.LIME);
			break;
		case 6:
			setState = setState.withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
		}
		
		world.setBlockState(pos, setState, 2);
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		fillCeiling(world, pos, state);
	}

	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(waterFloor)
			world.setBlockState(pos, floorState, 3);
		else fillCeiling(world, pos, state);
		
		if(world.rand.nextDouble() < slimeBlockChance)
			world.setBlockState(pos, Blocks.SLIME_BLOCK.getDefaultState());
	}
	
	@Override
	public void setupConfig(String category) {
		slimeBlockChance = ConfigHelper.loadLegacyPropChance("Slime Block Percentage Chance", category, "Slime Block Chance", "The chance slime blocks will spawn", 0.085);
		waterFloor = ModuleLoader.config.getBoolean("Enable Water Floor", category, true, "");
	}
	
	
}
