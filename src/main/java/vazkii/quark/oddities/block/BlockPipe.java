package vazkii.quark.oddities.block;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import vazkii.arl.block.BlockModContainer;
import vazkii.quark.base.block.BlockQuarkWall;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.oddities.tile.TilePipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BlockPipe extends BlockModContainer implements IQuarkBlock {

	private static final AxisAlignedBB CENTER_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);

	private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 1, 0.6875);
	private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 1);
	private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
	private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 1, 0.6875, 0.6875);

	private static final AxisAlignedBB DOWN_FLARE_AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.325, 0.75);
	private static final AxisAlignedBB UP_FLARE_AABB = new AxisAlignedBB(0.25, 0.625, 0.25, 0.75, 0.75, 0.75);
	private static final AxisAlignedBB NORTH_FLARE_AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.325);
	private static final AxisAlignedBB SOUTH_FLARE_AABB = new AxisAlignedBB(0.25, 0.25, 0.625, 0.75, 0.75, 0.75);
	private static final AxisAlignedBB WEST_FLARE_AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.325, 0.75, 0.75);
	private static final AxisAlignedBB EAST_FLARE_AABB = new AxisAlignedBB(0.625, 0.25, 0.25, 0.75, 0.75, 0.75);

	private static final AxisAlignedBB DOWN_TERMINAL_AABB = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.125, 0.75);
	private static final AxisAlignedBB UP_TERMINAL_AABB = new AxisAlignedBB(0.25, 0.875, 0.25, 0.75, 1, 0.75);
	private static final AxisAlignedBB NORTH_TERMINAL_AABB = new AxisAlignedBB(0.25, 0.25, 0, 0.75, 0.75, 0.125);
	private static final AxisAlignedBB SOUTH_TERMINAL_AABB = new AxisAlignedBB(0.25, 0.25, 0.875, 0.75, 0.75, 1);
	private static final AxisAlignedBB WEST_TERMINAL_AABB = new AxisAlignedBB(0, 0.25, 0.25, 0.125, 0.75, 0.75);
	private static final AxisAlignedBB EAST_TERMINAL_AABB = new AxisAlignedBB(0.875, 0.25, 0.25, 1, 0.75, 0.75);

	public static final PropertyEnum<ConnectionType> DOWN = PropertyEnum.create("down", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> UP = PropertyEnum.create("up", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> NORTH = PropertyEnum.create("north", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> SOUTH = PropertyEnum.create("south", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> WEST = PropertyEnum.create("west", ConnectionType.class);
	public static final PropertyEnum<ConnectionType> EAST = PropertyEnum.create("east", ConnectionType.class);
	public static final PropertyBool ENABLED = PropertyBool.create("enabled");

	@SuppressWarnings("unchecked")
	private static final PropertyEnum<ConnectionType>[] CONNECTIONS = new PropertyEnum[] {
			DOWN, UP, NORTH, SOUTH, WEST, EAST
	};

	private static final AxisAlignedBB[] SIDE_BOXES = new AxisAlignedBB[] {
			DOWN_AABB, UP_AABB, NORTH_AABB, SOUTH_AABB, WEST_AABB, EAST_AABB
	};

	private static final AxisAlignedBB[] FLARE_BOXES = new AxisAlignedBB[] {
			DOWN_FLARE_AABB, UP_FLARE_AABB, NORTH_FLARE_AABB,
			SOUTH_FLARE_AABB, WEST_FLARE_AABB, EAST_FLARE_AABB
	};

	private static final AxisAlignedBB[] TERMINAL_BOXES = new AxisAlignedBB[] {
			DOWN_TERMINAL_AABB, UP_TERMINAL_AABB, NORTH_TERMINAL_AABB,
			SOUTH_TERMINAL_AABB, WEST_TERMINAL_AABB, EAST_TERMINAL_AABB
	};

	private static final Map<IBlockState, EnumFacing> FLARE_STATES = Maps.newHashMap();

	public BlockPipe() {
		super("pipe", Material.GLASS);
		setHardness(3.0F);
		setResistance(10.0F);
		setSoundType(SoundType.GLASS);
		setCreativeTab(CreativeTabs.REDSTONE);

		setHarvestLevel("pickaxe", 1);

		setDefaultState(getDefaultState()
				.withProperty(DOWN, ConnectionType.NONE).withProperty(UP, ConnectionType.NONE)
				.withProperty(NORTH, ConnectionType.NONE).withProperty(SOUTH, ConnectionType.NONE)
				.withProperty(WEST, ConnectionType.NONE).withProperty(EAST, ConnectionType.NONE)
				.withProperty(ENABLED, true));

		stateLoop: for (IBlockState state : blockState.getValidStates()) {
			EnumFacing onlySide = null;

			for (EnumFacing facing : EnumFacing.VALUES) {
				if (state.getValue(CONNECTIONS[facing.getIndex()]) != ConnectionType.NONE) {
					if (onlySide == null)
						onlySide = facing;
					else
						continue stateLoop;
				}
			}

			if (onlySide != null)
				FLARE_STATES.put(state, onlySide.getOpposite());
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		boolean flag = !worldIn.isBlockPowered(pos);

		if(flag != state.getValue(ENABLED))
			worldIn.setBlockState(pos, state.withProperty(ENABLED, flag), 2 | 4);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

		state = getActualState(state, source, pos);

		double minX = connectionHeight(state, EnumFacing.WEST);
		double minY = connectionHeight(state, EnumFacing.DOWN);
		double minZ = connectionHeight(state, EnumFacing.NORTH);
		double maxX = connectionHeight(state, EnumFacing.EAST);
		double maxY = connectionHeight(state, EnumFacing.UP);
		double maxZ = connectionHeight(state, EnumFacing.SOUTH);

		boolean downFlared = isFlared(state, EnumFacing.DOWN);
		boolean upFlared = isFlared(state, EnumFacing.UP);
		boolean northFlared = isFlared(state, EnumFacing.NORTH);
		boolean southFlared = isFlared(state, EnumFacing.SOUTH);
		boolean westFlared = isFlared(state, EnumFacing.WEST);
		boolean eastFlared = isFlared(state, EnumFacing.EAST);

		if(downFlared || upFlared || northFlared || southFlared) {
			maxX = Math.max(maxX, 0.75);
			minX = Math.min(minX, 0.25);
		}

		if(northFlared || southFlared || westFlared || eastFlared) {
			maxY = Math.max(maxY, 0.75);
			minY = Math.min(minY, 0.25);
		}

		if(downFlared || upFlared || westFlared || eastFlared) {
			maxZ = Math.max(maxZ, 0.75);
			minZ = Math.min(minZ, 0.25);
		}

		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Nullable
	@Override
	@SuppressWarnings("deprecation")
	public RayTraceResult collisionRayTrace(IBlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
		List<AxisAlignedBB> boxes = new ArrayList<>();
		addCollisionBoxToList(blockState, worldIn, pos, new AxisAlignedBB(pos), boxes, null, false);
		for (AxisAlignedBB bb : boxes) {
			if (rayTrace(pos, start, end, bb.offset(-pos.getX(), -pos.getY(), -pos.getZ())) != null)
				return super.collisionRayTrace(blockState, worldIn, pos, start, end);
		}
		return null;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos) {
		return getBoundingBox(state, worldIn, pos).offset(pos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if(!isActualState)
			state = state.getActualState(worldIn, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, CENTER_AABB);
		for(EnumFacing side : EnumFacing.VALUES) {
			ConnectionType type = getType(state, side);

			if (type != null && type.isSolid)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, SIDE_BOXES[side.ordinal()]);

			if (type == null)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, FLARE_BOXES[side.ordinal()]);
			else if (type == ConnectionType.TERMINAL)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, TERMINAL_BOXES[side.ordinal()]);
		}
	}

	public static double connectionHeight(IBlockState state, EnumFacing side) {
		ConnectionType type = getType(state, side);
		int direction = side.getAxisDirection().getOffset();
		double base = 0.5 + 0.1875 * direction;
		return base + direction * (type != null && type.isSolid ? 0.3125 : (type == null || type.isFlared ? 0.0625 : 0));
	}

	public static boolean isFlared(IBlockState state, EnumFacing side) {
		ConnectionType type = getType(state, side);
		return type == null || type.isFlared;
	}

	public static ConnectionType getType(IBlockState state, EnumFacing side) {
		if (FLARE_STATES.containsKey(state) && FLARE_STATES.get(state) == side)
			return null;
		PropertyEnum<ConnectionType> prop = CONNECTIONS[side.ordinal()];
		return state.getValue(prop);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[] { ENABLED };
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, UP, DOWN, NORTH, SOUTH, WEST, EAST, ENABLED);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(ENABLED) ? 0b0 : 1);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ENABLED, (meta & 0b1) != 1);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState actualState = state;

		for(EnumFacing facing : EnumFacing.VALUES) {
			PropertyEnum<ConnectionType> prop = CONNECTIONS[facing.ordinal()];
			ConnectionType type = getConnectionTo(worldIn, pos, facing);

			actualState = actualState.withProperty(prop, type);
		}

		return actualState;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile instanceof TilePipe)
			return ((TilePipe) tile).getComparatorOutput();
		return 0;
	}

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if(tileentity instanceof TilePipe)
			((TilePipe) tileentity).dropAllItems();

			super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TilePipe();
	}

	private ConnectionType getConnectionTo(IBlockAccess world, BlockPos pos, EnumFacing face) {
		pos = pos.offset(face);
		TileEntity tile = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
		if(tile != null) {
			if(tile instanceof TilePipe)
				return ConnectionType.PIPE;
			else if(tile instanceof IInventory || (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) 
					&& tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) != null))
				return ConnectionType.TERMINAL;
		}

		IBlockState stateAt = world.getBlockState(pos);
		Block blockAt = stateAt.getBlock();
		if((face.getAxis() == Axis.Y && (blockAt instanceof BlockWall || blockAt instanceof BlockQuarkWall))
				|| ((blockAt instanceof BlockPistonBase || blockAt instanceof BlockPistonExtension) && stateAt.getValue(BlockDirectional.FACING) == face.getOpposite()))
				return ConnectionType.PROP;

		return ConnectionType.NONE;
	}

	public enum ConnectionType implements IStringSerializable {

		NONE(false, false, false),
		PIPE(true, true, false),
		TERMINAL(true, true, true),
		PROP(true, false, false);

		ConnectionType(boolean isSolid, boolean allowsItems, boolean isFlared) {
			this.isSolid = isSolid;
			this.allowsItems = allowsItems;
			this.isFlared = isFlared;
		}

		public final boolean isSolid, allowsItems, isFlared;

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}

	}

}
