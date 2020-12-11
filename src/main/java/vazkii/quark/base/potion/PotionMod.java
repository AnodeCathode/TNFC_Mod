/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 17:24:22 (GMT)]
 */
package vazkii.quark.base.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;

import javax.annotation.Nonnull;

public class PotionMod extends Potion {

	public static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/misc/potions.png");

	protected final String bareName;

	private final int iconX;
	private final int iconY;

	public PotionMod(String name, boolean badEffect, int color, int iconIndex) {
		super(badEffect, color);
		iconX = iconIndex % 8;
		iconY = iconIndex / 8;
		setPotionName(name);
		setRegistryName(new ResourceLocation(LibMisc.PREFIX_MOD + name));
		ProxyRegistry.register(this);
		bareName = name;
	}

	@Nonnull
	@Override
	public String getName() {
		return "quark.potion." + bareName + ".name";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
		GlStateManager.color(1f, 1f, 1f, alpha);
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		gui.drawTexturedModalRect(x + 3, y + 3, iconX * 18, 198 + iconY * 18, 18, 18);
		GlStateManager.color(1f, 1f, 1f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
		gui.drawTexturedModalRect(x + 6, y + 7, iconX * 18, 198 + iconY * 18, 18, 18);
	}
}
