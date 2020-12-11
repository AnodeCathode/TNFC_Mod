/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 05, 2019, 17:39 AM (EST)]
 */
package vazkii.quark.base.util;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommonReflectiveAccessor {

	private static final MethodHandle BOW_IS_ARROW, NAVIGATOR_SPEED, DISPENSER_SUCCESS;

	static {
		try {
			Method m = ObfuscationReflectionHelper.findMethod(ItemBow.class, "func_185058_h_", Boolean.TYPE, ItemStack.class); // isArrow
			BOW_IS_ARROW = MethodHandles.lookup().unreflect(m);

			Field f = ObfuscationReflectionHelper.findField(PathNavigate.class, "field_75511_d");
			NAVIGATOR_SPEED = MethodHandles.lookup().unreflectGetter(f);

			f = ObfuscationReflectionHelper.findField(Bootstrap.BehaviorDispenseOptional.class, "field_190911_b");
			DISPENSER_SUCCESS = MethodHandles.lookup().unreflectGetter(f);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isArrow(ItemBow bow, ItemStack arrow) {
		try {
			return (boolean) BOW_IS_ARROW.invokeExact(bow, arrow);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static double getSpeed(PathNavigate navigator) {
		try {
			return (double) NAVIGATOR_SPEED.invokeExact(navigator);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static boolean getSuccess(Bootstrap.BehaviorDispenseOptional behavior) {
		try {
			return (boolean) DISPENSER_SUCCESS.invokeExact(behavior);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
