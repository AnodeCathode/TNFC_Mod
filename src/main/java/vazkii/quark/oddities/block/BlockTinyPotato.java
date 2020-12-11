/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jul 18, 2014, 7:58:08 PM (GMT)]
 */
package vazkii.quark.oddities.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.oddities.item.ItemBlockTinyPotato;
import vazkii.quark.oddities.tile.TileTinyPotato;

import javax.annotation.Nonnull;

public class BlockTinyPotato extends BlockMod implements IQuarkBlock {

	public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.375, 0, 0.375, 0.625, 0.375, 0.625);

	public BlockTinyPotato() {
		super("tiny_potato", Material.CLOTH);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setHardness(0.25F);
		setDefaultState(getDefaultState()
				.withProperty(FACING, EnumFacing.SOUTH));
	}

	@Override
	public ItemBlock createItemBlock(ResourceLocation res) {
		return new ItemBlockTinyPotato(this, res);
	}

	@Nonnull
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileEntity inv = world.getTileEntity(pos);

		if (inv instanceof IInventory)
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) inv);

		super.breakBlock(world, pos, state);
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing par6, float par7, float par8, float par9) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileTinyPotato) {
			((TileTinyPotato) tile).interact(player, hand, player.getHeldItem(hand), par6);
			world.spawnParticle(EnumParticleTypes.HEART, pos.getX() + AABB.minX + Math.random() * (AABB.maxX - AABB.minX), pos.getY() + AABB.maxY, pos.getZ() + AABB.minZ + Math.random() * (AABB.maxZ - AABB.minZ), 0, 0 ,0);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileTinyPotato) {
			if (stack.hasDisplayName())
				((TileTinyPotato) te).name = stack.getDisplayName();
			((TileTinyPotato) te).angery = ItemNBTHelper.getBoolean(stack, "angery", false);
		}
	}

	@Override
	public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
		if (willHarvest) {
			// Copy of super.removedByPlayer but don't set to air yet
			// This is so getDrops below will have a TE to work with
			onBlockHarvested(world, pos, state, player);
			return true;
		} else {
			return super.removedByPlayer(state, world, pos, player, false);
		}
	}

	@Override
	public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, TileEntity te, ItemStack stack) {
		super.harvestBlock(world, player, pos, state, te, stack);
		// Now delete the block and TE
		world.setBlockToAir(pos);
	}

	@Override
	public void getDrops(@Nonnull NonNullList<ItemStack> list, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
		TileEntity tile = world.getTileEntity(pos);

		if(tile != null) {
			ItemStack stack = new ItemStack(this);
			String name = ((TileTinyPotato) tile).name;
			if(!name.isEmpty())
				stack.setStackDisplayName(name);
			if (((TileTinyPotato) tile).angery)
				ItemNBTHelper.setBoolean(stack, "angery", true);
			list.add(stack);
		}
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

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileTinyPotato();
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return BlockFaceShape.UNDEFINED;
	}
}
