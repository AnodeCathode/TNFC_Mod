package vazkii.quark.world.world.underground;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.world.UndergroundBiomeGenerator.UndergroundBiomeGenerationContext;

import java.util.function.Predicate;

public abstract class UndergroundBiome {

	public double dungeonChance;
	
	public static final Predicate<IBlockState> STONE_PREDICATE = state -> {
		if(state != null) {
			Block block = state.getBlock();
			if(block == Blocks.STONE) {
				BlockStone.EnumType type = state.getValue(BlockStone.VARIANT);
				return type.isNatural();
			}
			
			return block == RevampStoneGen.limestone || block == RevampStoneGen.marble;
		}

		return false;
	};

	public void fill(World world, BlockPos pos, UndergroundBiomeGenerationContext context) {
		IBlockState state = world.getBlockState(pos);
		if(state.getBlockHardness(world, pos) == -1 || world.canBlockSeeSky(pos))
			return;

		if(isFloor(world, pos, state)) {
			context.floorList.add(pos);
			fillFloor(world, pos, state);
		} else if(isCeiling(world, pos, state)) {
			context.ceilingList.add(pos);
			fillCeiling(world, pos, state);
		} else if(isWall(world, pos, state)) {
			context.wallMap.put(pos, getBorderSide(world, pos));
			fillWall(world, pos, state);
		} else if(isInside(state)) {
			context.insideList.add(pos);
			fillInside(world, pos, state);
		}
	}

	public abstract void fillFloor(World world, BlockPos pos, IBlockState state);
	public abstract void fillCeiling(World world, BlockPos pos, IBlockState state);
	public abstract void fillWall(World world, BlockPos pos, IBlockState state);
	public abstract void fillInside(World world, BlockPos pos, IBlockState state);
	
	public void finalFloorPass(World world, BlockPos pos) {
		// NO-OP
	}

	public void finalCeilingPass(World world, BlockPos pos) {
		// NO-OP
	}

	public void finalWallPass(World world, BlockPos pos) {
		// NO-OP
	}

	@SuppressWarnings({"unused", "EmptyMethod"})
	public void finalInsidePass(World world, BlockPos pos) {
		// NO-OP
	}
	
	public final void setupBaseConfig(String category) {
		if(hasDungeon())
			dungeonChance = ConfigHelper.loadLegacyPropChance("Dungeon Spawn Percentage Chance", category,
					"Dungeon Spawn Chance",
					"The chance that dungeons will spawn any given chunk of the biome. The lower the value, the fewer dungeons will spawn.",
					getDungeonChance());
		
		setupConfig(category);
	}

	public void setupConfig(String category) {
		// NO-OP
	}
	
	public boolean isValidBiome(Biome biome) {
		return true;
	}
	
	public boolean hasDungeon() {
		return false;
	}
 
	public float getDungeonChance() {
		return 0.05F;
	}
	
	public void spawnDungeon(WorldServer world, BlockPos pos, EnumFacing face) {
		// NO-OP
	}

	public boolean isFloor(World world, BlockPos pos, IBlockState state) {
		if(!state.isFullBlock() || !state.isOpaqueCube())
			return false;

		BlockPos upPos = pos.up();
		return world.isAirBlock(upPos) || world.getBlockState(upPos).getBlock().isReplaceable(world, upPos);
	}

	public boolean isCeiling(World world, BlockPos pos, IBlockState state) {
		if(!state.isFullBlock() || !state.isOpaqueCube())
			return false;

		BlockPos downPos = pos.down();
		return world.isAirBlock(downPos) || world.getBlockState(downPos).getBlock().isReplaceable(world, downPos);
	}

	public boolean isWall(World world, BlockPos pos, IBlockState state) {
		if(!state.isFullBlock() || !state.isOpaqueCube() || !STONE_PREDICATE.test(state))
			return false;

		return isBorder(world, pos);
	}

	public EnumFacing getBorderSide(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(facing);
			IBlockState stateAt = world.getBlockState(offsetPos);
			
			if(state != stateAt && world.isAirBlock(offsetPos) || stateAt.getBlock().isReplaceable(world, offsetPos))
				return facing;
		}

		return null;
	}
	
	public boolean isBorder(World world, BlockPos pos) {
		return getBorderSide(world, pos) != null;
	}

	public boolean isInside(IBlockState state) {
		return STONE_PREDICATE.test(state);
	}
	
	public static Rotation rotationFromFacing(EnumFacing facing) {
		switch(facing) {
		case SOUTH:
			return Rotation.CLOCKWISE_180;
		case WEST:
			return Rotation.COUNTERCLOCKWISE_90;
		case EAST:
			return Rotation.CLOCKWISE_90;
		default:
			return Rotation.NONE;
		}
	}

}
