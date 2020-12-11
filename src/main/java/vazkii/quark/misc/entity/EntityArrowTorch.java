/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [17/07/2016, 03:52:11 (GMT)]
 */
package vazkii.quark.misc.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.misc.feature.ExtraArrows;

import javax.annotation.Nonnull;

public class EntityArrowTorch extends EntityArrow {

	public EntityArrowTorch(World worldIn) {
		super(worldIn);
	}
	
	public EntityArrowTorch(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}
	
	public EntityArrowTorch(World worldIn, IPosition pos) {
		super(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(ExtraArrows.arrow_torch);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if(getEntityWorld().isRemote)
			spawnPotionParticles(1);
	}

	public void spawnPotionParticles(int particleCount) {
		if(particleCount > 0)
			for(int i = 0; i < particleCount; i++)
				getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.0D, 0.0D);
	}

	@Override
	protected void onHit(RayTraceResult rayTrace) {
		super.onHit(rayTrace);

		if(isDead)
			return;
		
		if(!getEntityWorld().isRemote) {
			if(rayTrace.typeOfHit != RayTraceResult.Type.BLOCK)
				return;
			BlockPos pos = rayTrace.getBlockPos();
			BlockPos finalPos = pos.offset(rayTrace.sideHit);
			IBlockState state = getEntityWorld().getBlockState(finalPos);
			Block block = state.getBlock();
			if((block.isAir(state, getEntityWorld(), finalPos) || block.isReplaceable(getEntityWorld(), finalPos)) && rayTrace.sideHit != EnumFacing.DOWN) {
				getEntityWorld().setBlockState(finalPos, Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, rayTrace.sideHit));
				setDead();
			}
		}
	}

}
