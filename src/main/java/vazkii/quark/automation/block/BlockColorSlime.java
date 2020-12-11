package vazkii.quark.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.api.INonSticky;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.tweaks.feature.SpringySlime;

import javax.annotation.Nonnull;
import java.util.Locale;

public class BlockColorSlime extends BlockMetaVariants<BlockColorSlime.Variants> implements IQuarkBlock, INonSticky {

	public BlockColorSlime() {
		super("color_slime", Material.CLAY, Variants.class);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setSoundType(SoundType.SLIME);
		setDefaultSlipperiness(0.8F);
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		if(entityIn.isSneaking())
			super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
		else
			entityIn.fall(fallDistance, 0.0F);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (ModuleLoader.isFeatureEnabled(SpringySlime.class))
			SpringySlime.collideWithSlimeBlock(pos, entityIn);
	}

	@Override
	public void onLanded(World worldIn, Entity entityIn) {
		if (ModuleLoader.isFeatureEnabled(SpringySlime.class)) {
			entityIn.motionY = 0.0;
		} else {

			if (entityIn.isSneaking())
				super.onLanded(worldIn, entityIn);

			else if (entityIn.motionY < 0.0D) {
				entityIn.motionY = -entityIn.motionY;

				if (!(entityIn instanceof EntityLivingBase))
					entityIn.motionY *= 0.8D;
			}
		}
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
		if(Math.abs(entityIn.motionY) < 0.1D && !entityIn.isSneaking()) {
			double d0 = 0.4D + Math.abs(entityIn.motionY) * 0.2D;
			entityIn.motionX *= d0;
			entityIn.motionZ *= d0;
		}

		super.onEntityWalk(worldIn, pos, entityIn);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("deprecation")
	public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();

		return block != this && super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean isStickyBlock(IBlockState state) {
		return true;
	}

	@Override
	public boolean canStickToBlock(World world, BlockPos pistonPos, BlockPos pos, BlockPos slimePos, IBlockState state, IBlockState slimeState, EnumFacing direction) {
		Variants ourVariant = state.getValue(variantProp);
		Block block = slimeState.getBlock();
		
		if(block == this) {
			Variants otherVariant = slimeState.getValue(variantProp);
			if(!ourVariant.sticksTo(otherVariant) && ourVariant != otherVariant)
				return false;
		}
		
		if(block == Blocks.SLIME_BLOCK)
			return ourVariant.sticksToGreen;

		return true;
	}

	public enum Variants implements IStringSerializable {

		SLIME_RED(false, 3, 4), // 0
		SLIME_BLUE(false, 2, 3), // 1
		SLIME_CYAN(true, 1), // 2
		SLIME_MAGENTA(false, 0, 1), // 3
		SLIME_YELLOW(true, 0); // 4

		Variants(boolean sticksToGreen, int... sticksTo) {
			this.sticksToGreen = sticksToGreen;
			this.sticksTo = sticksTo;
		}

		public final boolean sticksToGreen;
		private final int[] sticksTo;

		boolean sticksTo(Variants otherVariant) {
			int ord = otherVariant.ordinal();
			for(int i : sticksTo)
				if(i == ord)
					return true;

			return false;
		}

		@Override
		public String getName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

}
