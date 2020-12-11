/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Aug 02, 2019, 08:37 AM (EST)]
 */
package vazkii.quark.base.util;

import net.minecraft.entity.Entity;

public class EntityOpacityHandler {

	public static boolean isEntityInsideOpaqueBlock(Entity entity) {
		return !entity.noClip && entity.world.getBlockState(entity.getPosition()).causesSuffocation();

	}
}
