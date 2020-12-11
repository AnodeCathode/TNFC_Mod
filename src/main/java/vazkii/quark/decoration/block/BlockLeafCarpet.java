/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 18:54:12 (GMT)]
 */
package vazkii.quark.decoration.block;

import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.block.BlockVariantLeaves;
import vazkii.quark.world.feature.TreeVariants;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Supplier;

public class BlockLeafCarpet extends BlockMetaVariants<BlockLeafCarpet.Variants> implements IBlockColorProvider, IQuarkBlock {

	protected static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.0625, 1);

	public BlockLeafCarpet() {
		super("leaf_carpet", Material.LEAVES, Variants.class);
		setHardness(0.2F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return (state, worldIn, pos, tintIndex) -> {
			IBlockState base = ((Variants) state.getValue(getVariantProp())).getBaseState();
			return Minecraft.getMinecraft().getBlockColors().colorMultiplier(base, worldIn, pos, tintIndex);
		};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> {
			ItemStack baseStack = Variants.values()[Math.min(Variants.values().length - 1, stack.getItemDamage())].getBaseStack();
			return Minecraft.getMinecraft().getItemColors().colorMultiplier(baseStack, tintIndex);
		};
	}
	
	@Override
	public boolean shouldDisplayVariant(int variant) {
		return !Variants.values()[variant].getBaseStack().isEmpty();
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
		return null;
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CARPET_AABB;
	}

	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
		return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

	public enum Variants implements IStringSerializable {
		OAK_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 0), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)),
		SPRUCE_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 1), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE)),
		BIRCH_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 2), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH)),
		JUNGLE_LEAF_CARPET(new ItemStack(Blocks.LEAVES, 1, 3), Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)),
		ACACIA_LEAF_CARPET(new ItemStack(Blocks.LEAVES2, 1, 0), Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)),
		DARK_OAK_LEAF_CARPET(new ItemStack(Blocks.LEAVES2, 1, 1), Blocks.LEAVES2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA)),
		SWAMP_LEAF_CARPET(() -> new ItemStack(TreeVariants.variant_leaves, 1, 0), () -> TreeVariants.variant_leaves.getDefaultState().withProperty(BlockVariantLeaves.VARIANT, BlockVariantLeaves.Variant.SWAMP_LEAVES)),
		SAKURA_LEAF_CARPET(() -> new ItemStack(TreeVariants.variant_leaves, 1, 1), () -> TreeVariants.variant_leaves.getDefaultState().withProperty(BlockVariantLeaves.VARIANT, BlockVariantLeaves.Variant.SAKURA_LEAVES));

		Variants(ItemStack baseStack, IBlockState baseState) {
			this(() -> baseStack, () -> baseState);
		}
		
		Variants(Supplier<ItemStack> baseStack, Supplier<IBlockState> baseState) {
			this.baseStack = baseStack;
			this.baseState = baseState;
		}

		public final Supplier<ItemStack> baseStack;
		public final Supplier<IBlockState> baseState;

		public ItemStack getBaseStack() {
			return baseStack.get();
		}

		public IBlockState getBaseState() {
			return baseState.get();
		}
		
		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	@Override
	public IStateMapper getStateMapper() {
		return null;
	}

}
