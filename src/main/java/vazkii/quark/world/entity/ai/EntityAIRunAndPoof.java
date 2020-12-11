/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 11, 2019, 18:35 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.entity.EntityStoneling;

import java.util.List;
import java.util.function.Predicate;

public class EntityAIRunAndPoof<T extends Entity> extends EntityAIBase {

	private final Predicate<Entity> canBeSeenSelector;
	protected EntityStoneling entity;
	private final double farSpeed;
	private final double nearSpeed;
	protected T closestLivingEntity;
	private final float avoidDistance;
	private Path path;
	private final PathNavigate navigation;
	private final Class<T> classToAvoid;
	private final java.util.function.Predicate<T> avoidTargetSelector;

	public EntityAIRunAndPoof(EntityStoneling entity, Class<T> classToAvoid, float avoidDistance, double farSpeed, double nearSpeed) {
		this(entity, classToAvoid, t -> true, avoidDistance, farSpeed, nearSpeed);
	}

	public EntityAIRunAndPoof(EntityStoneling entity, Class<T> classToAvoid, java.util.function.Predicate<T> avoidTargetSelector, float avoidDistance, double farSpeed, double nearSpeed) {
		this.canBeSeenSelector = target -> target != null && target.isEntityAlive() && entity.getEntitySenses().canSee(target) && !entity.isOnSameTeam(target);
		this.entity = entity;
		this.classToAvoid = classToAvoid;
		this.avoidTargetSelector = avoidTargetSelector;
		this.avoidDistance = avoidDistance;
		this.farSpeed = farSpeed;
		this.nearSpeed = nearSpeed;
		this.navigation = entity.getNavigator();
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (entity.isPlayerMade())
			return false;

		List<T> entities = this.entity.world.getEntitiesWithinAABB(this.classToAvoid, this.entity.getEntityBoundingBox().grow(this.avoidDistance, 3.0D, this.avoidDistance),
				entity -> EntitySelectors.CAN_AI_TARGET.apply(entity) && this.canBeSeenSelector.test(entity) && this.avoidTargetSelector.test(entity));

		if (entities.isEmpty())
			return false;
		else {
			this.closestLivingEntity = entities.get(0);
			Vec3d target = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, this.closestLivingEntity.getPositionVector());

			if (target != null && this.closestLivingEntity.getDistanceSq(target.x, target.y, target.z) < this.closestLivingEntity.getDistanceSq(this.entity))
				return false;
			else {
				if (target != null)
					this.path = this.navigation.getPathToXYZ(target.x, target.y, target.z);
				return target == null || this.path != null;
			}
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (this.path == null || this.navigation.noPath()) {
			return false;
		}

		BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();

		for (int i = 0; i < 8; ++i) {
			int j = MathHelper.floor(entity.posY + (i % 2 - 0.5F) * 0.1F + entity.getEyeHeight());
			int k = MathHelper.floor(entity.posX + ((i >> 1) % 2 - 0.5F) * entity.width * 0.8F);
			int l = MathHelper.floor(entity.posZ + ((i >> 2) % 2 - 0.5F) * entity.width * 0.8F);

			if (pos.getX() != k || pos.getY() != j || pos.getZ() != l) {
				pos.setPos(k, j, l);

				if (entity.world.getBlockState(pos).getMaterial().blocksMovement()) {
					pos.release();
					return false;
				}
			}
		}

		pos.release();
		return true;
	}

	@Override
	public void startExecuting() {
		if (this.path != null)
			this.navigation.setPath(this.path, this.farSpeed);
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, QuarkSounds.ENTITY_STONELING_MEEP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
	}

	@Override
	public void resetTask() {
		this.closestLivingEntity = null;

		World world = entity.world;

		if (world instanceof WorldServer) {
			WorldServer ws = (WorldServer) world;
			ws.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY, entity.posZ, 40, 0.5, 0.5, 0.5, 0.1);
			ws.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entity.posX, entity.posY, entity.posZ, 20, 0.5, 0.5, 0.5, 0);
		}
		for (Entity passenger : entity.getRecursivePassengers())
			if (!(passenger instanceof EntityPlayer))
				passenger.setDead();
		entity.setDead();
	}

	@Override
	public void updateTask() {
		if (this.entity.getDistanceSq(this.closestLivingEntity) < 49.0D)
			this.entity.getNavigator().setSpeed(this.nearSpeed);
		else
			this.entity.getNavigator().setSpeed(this.farSpeed);
	}

}
