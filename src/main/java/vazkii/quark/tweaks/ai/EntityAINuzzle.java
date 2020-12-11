/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 30, 2019, 20:50 AM (EST)]
 */
package vazkii.quark.tweaks.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.SoundEvent;

public class EntityAINuzzle extends EntityAIBase {

	private final EntityTameable creature;
	private EntityLivingBase owner;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private int timeUntilRebuildPath;
	private final float maxDist;
	private final float whineDist;
	private int whineCooldown;
	private float oldWaterCost;
	private final SoundEvent whine;

	public EntityAINuzzle(EntityTameable creature, double followSpeed, float maxDist, float whineDist, SoundEvent whine) {
		this.creature = creature;
		this.followSpeed = followSpeed;
		this.petPathfinder = creature.getNavigator();
		this.maxDist = maxDist;
		this.whineDist = whineDist;
		this.whine = whine;
		this.setMutexBits(3);

		if (!(creature.getNavigator() instanceof PathNavigateGround) && !(creature.getNavigator() instanceof PathNavigateFlying))
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
	}

	public boolean shouldExecute() {
		if (!EntityAIWantLove.needsPets(creature))
			return false;

		EntityLivingBase entitylivingbase = this.creature.getOwner();

		if (entitylivingbase == null ||
				(entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator()) ||
				this.creature.isSitting())
			return false;
		else {
			this.owner = entitylivingbase;
			return true;
		}
	}

	public boolean shouldContinueExecuting() {
		if (!EntityAIWantLove.needsPets(creature))
			return false;
		return !this.petPathfinder.noPath() && this.creature.getDistanceSq(this.owner) > (this.maxDist * this.maxDist) && !this.creature.isSitting();
	}

	public void startExecuting() {
		this.timeUntilRebuildPath = 0;
		this.whineCooldown = 10;
		this.oldWaterCost = this.creature.getPathPriority(PathNodeType.WATER);
		this.creature.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	public void resetTask() {
		this.owner = null;
		this.petPathfinder.clearPath();
		this.creature.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	public void updateTask() {
		this.creature.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, this.creature.getVerticalFaceSpeed());

		if (!this.creature.isSitting()) {
			if (--this.timeUntilRebuildPath <= 0) {
				this.timeUntilRebuildPath = 10;

				this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed);
			}
		}

		if (creature.getDistanceSq(owner) < whineDist) {
			if (--this.whineCooldown <= 0) {
				this.whineCooldown = 80 + creature.getRNG().nextInt(40);
				creature.playSound(whine, 1F, 0.5F + (float) Math.random() * 0.5F);
			}
		}
	}
}
