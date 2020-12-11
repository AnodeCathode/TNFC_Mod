/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 12:17 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import vazkii.quark.world.entity.EntityFoxhound;

public class EntityAISleep extends EntityAIBase {

	private final EntityFoxhound foxhound;
	private boolean isSleeping;

	public EntityAISleep(EntityFoxhound foxhound) {
		this.foxhound = foxhound;
		this.setMutexBits(2);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.foxhound.isTamed() || this.foxhound.isInWater() || !this.foxhound.onGround)
			return false;
		else {
			EntityLivingBase entitylivingbase = this.foxhound.getOwner();

			if (entitylivingbase == null) return true;
			else
				return (!(this.foxhound.getDistanceSq(entitylivingbase) < 144.0D) || entitylivingbase.getRevengeTarget() == null) && this.isSleeping;
		}
	}

	@Override
	public void startExecuting() {
		this.foxhound.getNavigator().clearPath();
		this.foxhound.getAISit().setSitting(true);
		this.foxhound.setSitting(true);
		this.foxhound.setSleeping(true);
	}

	@Override
	public void resetTask() {
		this.foxhound.getAISit().setSitting(false);
		this.foxhound.setSitting(false);
		this.foxhound.setSleeping(false);
	}

	public void setSleeping(boolean sitting)
	{
		this.isSleeping = sitting;
	}
}
