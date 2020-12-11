/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 20:14 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityMoveHelper;

public class MovementHelperZigZag extends EntityMoveHelper {
	public MovementHelperZigZag(EntityLiving living) {
		super(living);
	}

	@Override
	protected float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
		return targetAngle + (targetAngle - sourceAngle) * 0.825f;
	}
}
