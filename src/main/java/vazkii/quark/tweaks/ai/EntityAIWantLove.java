/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 30, 2019, 20:44 AM (EST)]
 */
package vazkii.quark.tweaks.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.tweaks.feature.PatTheDogs;

public class EntityAIWantLove extends EntityAIBase {

	private static final String PET_TIME = "quark:PetTime";

	public static void setPetTime(EntityTameable entity) {
		entity.getEntityData().setLong(PET_TIME, entity.world.getTotalWorldTime());
	}

	public static boolean needsPets(EntityTameable entity) {
		if (PatTheDogs.dogsWantLove <= 0 || !entity.isTamed())
			return false;

		long lastPetAt = entity.getEntityData().getLong(PET_TIME);
		long sinceLastPet = entity.world.getTotalWorldTime() - lastPetAt;

		return sinceLastPet > PatTheDogs.dogsWantLove;
	}

	private final EntityTameable creature;
	private EntityLivingBase leapTarget;
	public final float leapUpMotion;

	public EntityAIWantLove(EntityTameable creature, float leapMotion) {
		this.creature = creature;
		this.leapUpMotion = leapMotion;
		this.setMutexBits(5);
	}

	public boolean shouldExecute() {
		if (!needsPets(creature))
			return false;

		this.leapTarget = this.creature.getOwner();

		if (this.leapTarget == null)
			return false;
		else {
			double distanceToTarget = this.creature.getDistanceSq(this.leapTarget);

			return 4 <= distanceToTarget && distanceToTarget <= 16 &&
					this.creature.onGround && this.creature.getRNG().nextInt(5) == 0;
		}
	}

	public boolean shouldContinueExecuting() {
		if (!EntityAIWantLove.needsPets(creature))
			return false;
		return !this.creature.onGround;
	}

	public void startExecuting() {
		double dX = this.leapTarget.posX - this.creature.posX;
		double dZ = this.leapTarget.posZ - this.creature.posZ;
		float leapMagnitude = MathHelper.sqrt(dX * dX + dZ * dZ);

		if (leapMagnitude >= 0.0001) {
			this.creature.motionX += dX / leapMagnitude * 0.4 + this.creature.motionX * 0.2;
			this.creature.motionZ += dZ / leapMagnitude * 0.4 + this.creature.motionZ * 0.2;
		}

		this.creature.motionY = this.leapUpMotion;
	}
}
