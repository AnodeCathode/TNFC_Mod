/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Aug 09, 2019, 09:59 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIPassenger extends EntityAIBase {
	private final EntityLiving entity;

	public EntityAIPassenger(EntityLiving entity) {
		this.entity = entity;
		setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		return entity.isRiding();
	}
}
