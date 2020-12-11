/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 13, 2019, 22:36 AM (EST)]
 */
package vazkii.quark.base.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientReflectiveAccessor {
	private static final MethodHandle GET_BUTTON_POTION,
			GET_BUTTON_SELECTED,
			GET_GUI_Z,
			SET_BUTTON_HOVERED,
			GET_RENDERER_UPDATES,
			APPLY_BOBBING,
			CHAT_DRAWN_LINES,
			CHAT_SCROLL_POS,
			SETUP_ITEM_GUI_TRANSFORM,
			MINECRAFT_SYSTEM_TIME,
			ENTITY_LIVING_BASE_POTION_COLOR,
			RENDER_OUTLINE_MODE;

	static {
		try {
			Class buttonClazz = Class.forName("net.minecraft.client.gui.inventory.GuiBeacon$PowerButton");
			Field f = ObfuscationReflectionHelper.findField(buttonClazz, "field_184066_p"); // effect
			GET_BUTTON_POTION = MethodHandles.lookup().unreflectGetter(f)
					.asType(MethodType.methodType(Potion.class, GuiButton.class));

			buttonClazz = Class.forName("net.minecraft.client.gui.inventory.GuiBeacon$Button");
			f = ObfuscationReflectionHelper.findField(buttonClazz, "field_146142_r"); // selected
			GET_BUTTON_SELECTED = MethodHandles.lookup().unreflectGetter(f)
					.asType(MethodType.methodType(Boolean.TYPE, GuiButton.class));

			f = ObfuscationReflectionHelper.findField(Gui.class, "field_73735_i"); // zLevel
			GET_GUI_Z = MethodHandles.lookup().unreflectGetter(f);

			f = ObfuscationReflectionHelper.findField(GuiButton.class, "field_146123_n"); // hovered
			SET_BUTTON_HOVERED = MethodHandles.lookup().unreflectSetter(f);

			f = ObfuscationReflectionHelper.findField(EntityRenderer.class, "field_78529_t"); //rendererUpdateCount
			GET_RENDERER_UPDATES = MethodHandles.lookup().unreflectGetter(f);

			Method m = ObfuscationReflectionHelper.findMethod(EntityRenderer.class, "func_78475_f", Void.TYPE, Float.TYPE); // applyBobbing
			APPLY_BOBBING = MethodHandles.lookup().unreflect(m);

			f = ObfuscationReflectionHelper.findField(GuiNewChat.class, "field_146253_i"); // drawnChatLines
			CHAT_DRAWN_LINES = MethodHandles.lookup().unreflectGetter(f);

			f = ObfuscationReflectionHelper.findField(GuiNewChat.class, "field_146250_j"); // scrollPos
			CHAT_SCROLL_POS = MethodHandles.lookup().unreflectGetter(f);

			m = ObfuscationReflectionHelper.findMethod(RenderItem.class, "func_180452_a", Void.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE); // setupGuiTransform
			SETUP_ITEM_GUI_TRANSFORM = MethodHandles.lookup().unreflect(m);

			f = ObfuscationReflectionHelper.findField(Minecraft.class, "field_71423_H"); // systemTime
			MINECRAFT_SYSTEM_TIME = MethodHandles.lookup().unreflectGetter(f);

			f = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_184633_f"); // POTION_EFFECTS
			ENTITY_LIVING_BASE_POTION_COLOR = MethodHandles.lookup().unreflectGetter(f);

			f = ObfuscationReflectionHelper.findField(Render.class, "field_188301_f"); // renderOutlines
			RENDER_OUTLINE_MODE = MethodHandles.lookup().unreflectGetter(f);
		} catch (ClassNotFoundException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Potion getPotion(GuiButton button) {
		try {
			return (Potion) GET_BUTTON_POTION.invokeExact(button);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static boolean getButtonSelected(GuiButton button) {
		try {
			return (boolean) GET_BUTTON_SELECTED.invokeExact(button);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static float getZLevel(Gui gui) {
		try {
			return (float) GET_GUI_Z.invokeExact(gui);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static void setButtonHovered(GuiButton button, boolean hovered) {
		try {
			SET_BUTTON_HOVERED.invokeExact(button, hovered);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static int getUpdateCount(EntityRenderer renderer) {
		try {
			return (int) GET_RENDERER_UPDATES.invokeExact(renderer);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static void applyBobbing(EntityRenderer entityRenderer, float partialTicks) {
		try {
			APPLY_BOBBING.invokeExact(entityRenderer, partialTicks);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<ChatLine> getChatDrawnLines(GuiNewChat chat) {
		try {
			return (List<ChatLine>) CHAT_DRAWN_LINES.invokeExact(chat);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static int getScrollPos(GuiNewChat chat) {
		try {
			return (int) CHAT_SCROLL_POS.invokeExact(chat);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static void setupGuiTransform(RenderItem render, int x, int y, boolean isGui3d) {
		try {
			SETUP_ITEM_GUI_TRANSFORM.invokeExact(render, x, y, isGui3d);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static long lastSystemTime(Minecraft mc) {
		try {
			return (long) MINECRAFT_SYSTEM_TIME.invokeExact(mc);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	@SuppressWarnings("unchecked")
	public static DataParameter<Integer> potionEffectColor() {
		try {
			return (DataParameter<Integer>) ENTITY_LIVING_BASE_POTION_COLOR.invokeExact();
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static boolean renderOutlines(Render render) {
		try {
			return (boolean) RENDER_OUTLINE_MODE.invokeExact(render);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}
}
