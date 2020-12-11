package vazkii.quark.world.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.CaveRoots;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static vazkii.quark.base.block.BlockQuarkPane.mirror;
import static vazkii.quark.base.block.BlockQuarkPane.rotate;

public class BlockRoots extends BlockMod implements IQuarkBlock, IShearable, IGrowable {

	public static final PropertyBool UP = BlockVine.UP;
	public static final PropertyBool NORTH = BlockVine.NORTH;
	public static final PropertyBool EAST = BlockVine.EAST;
	public static final PropertyBool SOUTH = BlockVine.SOUTH;
	public static final PropertyBool WEST = BlockVine.WEST;
	public static final PropertyBool[] ALL_FACES = BlockVine.ALL_FACES;

	protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);

	private final Random rng;
	
	public BlockRoots(String name) {
		super(name, Material.VINE);
		setDefaultState(blockState.getBaseState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
		setTickRandomly(true);
		setCreativeTab(CreativeTabs.DECORATIONS);
		
		rng = new Random();
	}

	public BlockRoots() {
		this("roots");
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if(!worldIn.isRemote && worldIn.rand.nextInt(2) == 0)
			grow(worldIn, rand, pos, state);
	}
	
	public static void growMany(World world, BlockPos pos, IBlockState state, float stopChance, boolean avoidCascade) {
		BlockPos next = pos;
		
		do {
			next = growAndReturnLastPos(world, next, state, avoidCascade);
		} while(next != null && world.rand.nextFloat() >= stopChance);
	}

	public static BlockPos growAndReturnLastPos(World world, BlockPos pos, IBlockState state, boolean avoidCascade) {
		BlockPos down = pos.down();
		
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			PropertyBool prop = getPropertyFor(facing);
			if(state.getValue(prop)) {
				BlockPos ret = growInFacing(world, down, facing);
				if(ret != null) {
					if(!avoidCascade || world.isBlockLoaded(ret)) {
						IBlockState setState = nextState(world.rand).withProperty(prop, true);
						world.setBlockState(ret, setState);
						return ret;
					}
				}
				break;
			}
		}
		
		return null;
	}
	
	public static BlockPos growInFacing(World world, BlockPos pos, EnumFacing facing) {
		if(!world.isAirBlock(pos))
			return null;
		
		BlockPos check = pos.offset(facing);
		if(isAcceptableNeighbor(world, check, facing.getOpposite()))
			return pos;
		
		pos = check;
		if(!world.isAirBlock(check))
			return null;
		
		check = pos.offset(facing);
		if(isAcceptableNeighbor(world, check, facing.getOpposite()))
			return pos;
		
		return null;
	}
	
	@Override
	public boolean canGrow(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
		return worldIn.getLight(pos) < 7;
	}
	
	@Override
	public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		return rand.nextFloat() < 0.4;
	}

	@Override
	public void grow(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		growAndReturnLastPos(worldIn, pos, state, false);
	}
	
	private static IBlockState nextState(Random rand) {
		if(!CaveRoots.enableFlowers || rand.nextFloat() > CaveRoots.flowerChance)
			return CaveRoots.roots.getDefaultState();

		switch(rand.nextInt(3)) {
		case 0:  return CaveRoots.roots_blue_flower.getDefaultState();
		case 1:  return CaveRoots.roots_black_flower.getDefaultState();
		default: return CaveRoots.roots_white_flower.getDefaultState();
		}
	}
	
	protected ItemStack getRootDrop() {
		return new ItemStack(CaveRoots.root);
	}
	
	protected float getDropChance() {
		return CaveRoots.rootDropChance;
	}

	// VANILLA COPY PASTA AHEAD ============================================================================================================

	@Override
	@Nullable
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
		return NULL_AABB;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = state.getActualState(source, pos);
		int i = 0;
		AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB;

		if(state.getValue(NORTH)) {
			axisalignedbb = NORTH_AABB;
			++i;
		}

		if(state.getValue(EAST)) {
			axisalignedbb = EAST_AABB;
			++i;
		}

		if(state.getValue(SOUTH)) {
			axisalignedbb = SOUTH_AABB;
			++i;
		}

		if(state.getValue(WEST)) {
			axisalignedbb = WEST_AABB;
			++i;
		}

		return i == 1 ? axisalignedbb : FULL_BLOCK_AABB;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		BlockPos blockpos = pos.up();
		return state.withProperty(UP, worldIn.getBlockState(blockpos).getBlockFaceShape(worldIn, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos pos) {
		return true;
	}

	@Override
	public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
		return side != EnumFacing.DOWN && side != EnumFacing.UP && canAttachTo(worldIn, pos, side);
	}

	public boolean canAttachTo(World world, BlockPos pos, EnumFacing side) {
		Block block = world.getBlockState(pos.up()).getBlock();
		return isAcceptableNeighbor(world, pos.offset(side.getOpposite()), side) && (block == Blocks.AIR || block instanceof BlockRoots || isAcceptableNeighbor(world, pos.up(), EnumFacing.UP));
	}

	private static boolean isAcceptableNeighbor(World world, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = world.getBlockState(pos);
		return iblockstate.getBlockFaceShape(world, pos, side) == BlockFaceShape.SOLID && iblockstate.getMaterial() == Material.ROCK;
	}

	private boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state) {
		IBlockState originalState = state;

		for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
			PropertyBool propertybool = getPropertyFor(enumfacing);

			if(state.getValue(propertybool) && !canAttachTo(worldIn, pos, enumfacing.getOpposite())) {
				IBlockState stateAt = worldIn.getBlockState(pos.up());

				if(stateAt.getBlock() != this || !stateAt.getValue(propertybool))
					state = state.withProperty(propertybool, Boolean.FALSE);
			}
		}

		if(getNumGrownFaces(state) == 0)
			return false;
		else {
			if(originalState != state)
				worldIn.setBlockState(pos, state, 2);

			return true;
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote && !recheckGrownSides(worldIn, pos, state)) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState iblockstate = getDefaultState().withProperty(UP, false).withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false);
		return facing.getAxis().isHorizontal() ? iblockstate.withProperty(getPropertyFor(facing.getOpposite()), true) : iblockstate;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
		if(rng.nextFloat() < getDropChance())
			return NonNullList.withSize(1, getRootDrop());
		return NonNullList.withSize(0, ItemStack.EMPTY);
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if(!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
			player.addStat(StatList.getBlockStats(this));
			spawnAsEntity(worldIn, pos, new ItemStack(this, 1, 0));
		}
		else super.harvestBlock(worldIn, player, pos, state, te, stack);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(SOUTH, (meta & 1) != 0)
				.withProperty(WEST, (meta & 2) != 0)
				.withProperty(NORTH, (meta & 4) != 0)
				.withProperty(EAST, (meta & 8) != 0);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		if (state.getValue(SOUTH))
			i |= 1;
		if (state.getValue(WEST))
			i |= 2;
		if (state.getValue(NORTH))
			i |= 4;
		if (state.getValue(EAST))
			i |= 8;

		return i;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, UP, NORTH, EAST, SOUTH, WEST);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
		return rotate(state, rot, NORTH, SOUTH, EAST, WEST);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
		return mirror(state, mirrorIn, NORTH, SOUTH, EAST, WEST);
	}

	public static PropertyBool getPropertyFor(EnumFacing side) {
		switch (side)
		{
		case UP:
			return UP;
		case NORTH:
			return NORTH;
		case SOUTH:
			return SOUTH;
		case WEST:
			return WEST;
		case EAST:
			return EAST;
		default:
			throw new IllegalArgumentException(side + " is an invalid choice");
		}
	}

	public static int getNumGrownFaces(IBlockState state) {
		int i = 0;

		for(PropertyBool propertybool : ALL_FACES)
			if (state.getValue(propertybool))
				++i;

		return i;
	}

	@Override 
	public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
		return true; 
	}
	
	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return Lists.newArrayList(new ItemStack(this, 1));
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

}
