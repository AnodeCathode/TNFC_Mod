/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 14, 2019, 19:51 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import vazkii.quark.world.entity.EntityCrab;

public class EntityAIRave extends EntityAIBase {
	private final EntityCrab crab;

	public EntityAIRave(EntityCrab crab) {
		this.crab = crab;
		this.setMutexBits(5);

	}

	@Override
	public boolean shouldExecute() {
		return crab.isRaving();
	}

	@Override
	public void startExecuting() {
		this.crab.getNavigator().clearPath();
	}
}
