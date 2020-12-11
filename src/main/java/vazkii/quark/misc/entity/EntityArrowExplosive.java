/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [17/07/2016, 03:52:03 (GMT)]
 */
package vazkii.quark.misc.entity;

import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import vazkii.quark.misc.feature.ExtraArrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityArrowExplosive extends EntityArrow {

	public EntityArrowExplosive(World worldIn) {
		super(worldIn);
	}
	
	public EntityArrowExplosive(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}
	
	public EntityArrowExplosive(World worldIn, IPosition pos) {
		super(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(ExtraArrows.arrow_explosive);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();

		if(getEntityWorld().isRemote)
			spawnPotionParticles(4);
	}

	public void spawnPotionParticles(int particleCount) {
		if(particleCount > 0)
			for(int i = 0; i < particleCount; i++)
				getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, 0.0D, 0.0D, 0.0D);
	}

	@Override
	protected void onHit(RayTraceResult rayTrace) {
		super.onHit(rayTrace);

		if(!world.isRemote) {
			Explosion explosion = new Explosion(world, this, rayTrace.hitVec.x, rayTrace.hitVec.y, rayTrace.hitVec.z,
					(float) ExtraArrows.explosiveArrowPower, false, ExtraArrows.explosiveArrowDestroysBlocks) {
				@Nullable
				@Override
				public EntityLivingBase getExplosivePlacedBy() {
					if (shootingEntity instanceof EntityLivingBase)
						return (EntityLivingBase) shootingEntity;
					return null;
				}
			};

			if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
				explosion.doExplosionA();
				explosion.doExplosionB(true);
			}
		}

		setDead();
	}

}
