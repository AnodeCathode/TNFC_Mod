/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 15, 2019, 05:46 AM (EST)]
 */
package vazkii.quark.automation.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.automation.tile.TileInductor;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockRedstoneInductor extends BlockMod implements IQuarkBlock, IBlockColorProvider {
	protected static final AxisAlignedBB REDSTONE_DIODE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final PropertyBool LOCKED = PropertyBool.create("locked");
	public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);

	public BlockRedstoneInductor() {
		super("redstone_inductor", Material.CIRCUITS);

		setDefaultState(blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(POWERED, false)
				.withProperty(LOCKED, false));
		setCreativeTab(CreativeTabs.REDSTONE);
		setSoundType(SoundType.WOOD);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return (state, worldIn, pos, tintIndex) ->
				tintIndex == 1 ? BlockRedstoneWire.colorMultiplier(state.getValue(POWER)) : -1;
	}

	@Override
	public IProperty[] getIgnoredProperties() {
		return new IProperty[] { POWER };
	}

	@Override
	public IItemColor getItemColor() {
		return null;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileInductor();
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(POWER, getActiveSignal(worldIn, pos));
	}

	private boolean shouldBeLocked(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing direction = state.getValue(FACING);
		EnumFacing side1 = direction.rotateY();
		EnumFacing side2 = direction.rotateYCCW();
		return this.getLockingPowerOnSide(worldIn, pos.offset(direction), direction) > 0 ||
				this.getLockingPowerOnSide(worldIn, pos.offset(side1), side1) > 0 ||
				this.getLockingPowerOnSide(worldIn, pos.offset(side2), side2) > 0;
	}

	protected int getLockingPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = worldIn.getBlockState(pos);

		if (BlockRedstoneRepeater.isDiode(iblockstate))
			return worldIn.getStrongPower(pos, side);
		else
			return 0;
	}

	@Override
	public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
		// NO-OP
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		boolean isLocked = isLocked(state);
		boolean willBeLocked = shouldBeLocked(worldIn, pos, state);

		EnumFacing side = state.getValue(FACING);

		IBlockState finalState = state.withProperty(LOCKED, willBeLocked);

		if (!isLocked || !willBeLocked) {
			int power = this.calculateInputStrength(worldIn, pos, state);
			TileEntity tile = worldIn.getTileEntity(pos);
			int prev = 0;

			if (tile instanceof TileInductor) {
				TileInductor inductor = (TileInductor) tile;
				prev = inductor.getOutputSignal();
				inductor.setOutputSignal(power);
			}

			state = state.withProperty(POWER, power);
			finalState = finalState.withProperty(POWER, power);

			if (prev != power) {
				boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
				boolean isPowered = this.isPowered(state);

				if (isPowered && !shouldBePowered) {
					finalState = finalState.withProperty(POWERED, false);
				} else if (!isPowered && shouldBePowered) {
					finalState = finalState.withProperty(POWERED, true);
				}
			}
		}

		worldIn.setBlockState(pos, finalState, 3);
		worldIn.notifyNeighborsOfStateExcept(pos.offset(side, -1), this, side);
	}

	protected int getActiveSignal(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world instanceof ChunkCache ? ((ChunkCache)world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
		return tile instanceof TileInductor ? ((TileInductor)tile).getOutputSignal() : 0;
	}

	protected void updateState(World world, BlockPos pos) {
		world.scheduleBlockUpdate(pos, this, 1, -1);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, POWERED, LOCKED, POWER);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(FACING).getHorizontalIndex()) + (state.getValue(POWERED) ? 0b0100 : 0) + (state.getValue(LOCKED) ? 0b1000 : 0);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing face = EnumFacing.byHorizontalIndex(meta & 0b0011);
		boolean powered = (meta & 0b0100) != 0;
		boolean locked = (meta & 0b1000) != 0;
		return getDefaultState().withProperty(FACING, face).withProperty(POWERED, powered).withProperty(LOCKED, locked);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return REDSTONE_DIODE_AABB;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && super.canPlaceBlockAt(worldIn, pos);
	}

	public boolean canBlockStay(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
	}

	protected boolean isLocked(IBlockState state) {
		return state.getValue(LOCKED);
	}

	protected boolean isPowered(IBlockState state) {
		return state.getValue(POWERED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return blockState.getWeakPower(blockAccess, pos, side);
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if(!isPowered(blockState))
			return 0;
		else
			return blockState.getValue(FACING) == side ? getActiveSignal(blockAccess, pos) : 0;
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(canBlockStay(worldIn, pos))
			updateState(worldIn, pos);
		else {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);

			for(EnumFacing enumfacing : EnumFacing.values())
				worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
		}
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing direction = state.getValue(FACING);
		EnumFacing side1 = direction.rotateY();
		EnumFacing side2 = direction.rotateYCCW();

		return Math.min(15, calculateInputStrength(worldIn, pos, direction) + calculateInputStrength(worldIn, pos, side1) + calculateInputStrength(worldIn, pos, side2));
	}

	protected int calculateInputStrength(World worldIn, BlockPos pos, EnumFacing side) {
		BlockPos blockpos = pos.offset(side);
		IBlockState state = worldIn.getBlockState(blockpos);
		if (BlockRedstoneRepeater.isDiode(state))
			return 0;

		int i = worldIn.getRedstonePower(blockpos, side);

		if(i >= 15)
			return i;
		else
			return Math.max(i, state.getBlock() == Blocks.REDSTONE_WIRE ? state.getValue(BlockRedstoneWire.POWER) : 0);
	}

	protected boolean shouldBePowered(World world, BlockPos pos, IBlockState currState) {
		return calculateInputStrength(world, pos, currState) > 0;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(shouldBePowered(worldIn, pos, state))
			worldIn.scheduleUpdate(pos, this, 1);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		BlockRedstoneRandomizer.notify(this, worldIn, pos, state);
	}

	@Override
	public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		BlockRedstoneRandomizer.notify(this, worldIn, pos, state);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
		if(super.rotateBlock(world, pos, axis)) {
			IBlockState state = world.getBlockState(pos);
			state = state.withProperty(POWERED, false);
			world.setBlockState(pos, state);

			world.scheduleUpdate(pos, this, 1);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
