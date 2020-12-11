package vazkii.quark.world.block;

import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkBush;
import vazkii.quark.world.feature.UndergroundBiomes;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockGlowshroom extends BlockQuarkBush implements IGrowable {

	protected static final AxisAlignedBB MUSHROOM_AABB = new AxisAlignedBB(0.2, 0, 0.2, 0.8, 0.6, 0.8);
	
	public BlockGlowshroom() {
		super("glowshroom");
		setLightLevel(0.9375F);
		setTickRandomly(true);
		setSoundType(SoundType.PLANT);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return MUSHROOM_AABB;
	}
	
	@Override
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == UndergroundBiomes.glowcelium;
	}
	
	@Override
	public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand) {
		if(rand.nextInt(UndergroundBiomes.glowshroomGrowthRate) == 0) {
			int i = 5;

			for(BlockPos targetPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
				if(worldIn.getBlockState(targetPos).getBlock() == this) {
					--i;

					if(i <= 0)
						return;
				}
			}

			BlockPos shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

			for(int k = 0; k < 4; ++k) {
				if (worldIn.isAirBlock(shiftedPos) && canBlockStay(worldIn, shiftedPos, getDefaultState()))
					pos = shiftedPos;

				shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			}

			if(worldIn.isAirBlock(shiftedPos) && canBlockStay(worldIn, shiftedPos, getDefaultState()))
				worldIn.setBlockState(shiftedPos, getDefaultState(), 2);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.randomDisplayTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(20) == 0)
			worldIn.spawnParticle(EnumParticleTypes.END_ROD, pos.getX() + 0.2 + rand.nextFloat() * 0.6, pos.getY() + 0.3F, pos.getZ() + 0.2 + rand.nextFloat() * 0.6, 0, 0, 0);
	}

	@Override
	public boolean canGrow(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
		return UndergroundBiomes.bigGlowshroomsEnabled;
	}

	@Override
	public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		return UndergroundBiomes.bigGlowshroomsEnabled && rand.nextFloat() < 0.4;
	}

	@Override
	public void grow(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		if(UndergroundBiomes.bigGlowshroomsEnabled) {
			worldIn.setBlockToAir(pos);
			if(!BlockHugeGlowshroom.setInPosition(worldIn, rand, pos, true))
				worldIn.setBlockState(pos, getDefaultState());
		}
	}

}
