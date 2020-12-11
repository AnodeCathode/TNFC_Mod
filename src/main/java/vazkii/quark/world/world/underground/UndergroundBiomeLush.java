package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenShrub;
import vazkii.quark.base.module.ConfigHelper;

public class UndergroundBiomeLush extends BasicUndergroundBiome {

	private final WorldGenShrub shrubGen = new WorldGenShrub(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState());

	public static double grassChance, shrubChance, vineChance;

	public UndergroundBiomeLush() {
		super(Blocks.GRASS.getDefaultState(), null, null);
	}

	@Override
	public void finalFloorPass(World world, BlockPos pos) {
		if(world.rand.nextDouble() < grassChance)
			ItemDye.applyBonemeal(new ItemStack(Items.DYE, 1, 14), world, pos);

		if(world.rand.nextDouble() < shrubChance)
			shrubGen.generate(world, world.rand, pos.up());
	}

	@Override
	public void finalWallPass(World world, BlockPos pos) {
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos off = pos.offset(facing);
			BlockPos up = off.up();
			if(isCeiling(world, up, world.getBlockState(up)) && world.rand.nextDouble() < vineChance) {
				IBlockState stateAt = world.getBlockState(off); 
				boolean did = false;
				while(stateAt.getBlock().isAir(stateAt, world, off) && off.getY() > 0) {
					world.setBlockState(off, Blocks.VINE.getDefaultState().withProperty(BlockVine.getPropertyFor(facing.getOpposite()), true), 2);
					off = off.down();
					stateAt = world.getBlockState(off);
					did = true;
				}

				if(did)
					return;
			}
		}
	}

	@Override
	public void setupConfig(String category) {
		grassChance = ConfigHelper.loadLegacyPropChance("Grass Percentage Chance", category, "Grass Chance", "The chance grass will spawn", 0.05);
		shrubChance = ConfigHelper.loadLegacyPropChance("Shrub Percentage Chance", category, "Shrub Chance", "The chance shrubs will spawn", 0.01);
		vineChance = ConfigHelper.loadLegacyPropChance("Vine Percentage Chance", category, "Vine Chance", "The chance vines will spawn", 0.125);
	}

}
