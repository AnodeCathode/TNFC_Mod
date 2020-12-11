package vazkii.quark.decoration.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockGrate extends BlockMod implements IQuarkBlock {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0F, 0.9375, 0F, 1F, 1F, 1F);
	private static final AxisAlignedBB AABB_SPAWN_BLOCK = new AxisAlignedBB(0F, 1F, 0F, 1F, 1.0625F, 1F);
	
	public BlockGrate() {
		super("grate", Material.IRON);
		
		setHardness(5.0F);
		setResistance(10.0F);
		setLightOpacity(0);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}
	
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if(entityIn != null && !(entityIn instanceof EntityItem)) {
			super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);

			if (!(entityIn instanceof EntityPlayer))
				addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_SPAWN_BLOCK);

			if (entityIn instanceof EntityAnimal)
				addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoundingBox(state, worldIn, pos).expand(0, entityIn.stepHeight + 0.125, 0));
		}
	}

	@Nullable
	@Override
	public PathNodeType getAiPathNodeType(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EntityLiving entity) {
		if (entity instanceof EntityAnimal)
			return PathNodeType.DAMAGE_OTHER;
		return null;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullBlock(IBlockState state) {
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
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Nonnull
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
		if (side.getAxis() == EnumFacing.Axis.Y)
			return super.shouldSideBeRendered(blockState, blockAccess, pos, side);

		IBlockState state = blockAccess.getBlockState(pos.offset(side));
		Block block = state.getBlock();

		return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

}
