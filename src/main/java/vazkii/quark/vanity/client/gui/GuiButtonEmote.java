/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [27/03/2016, 00:14:28 (GMT)]
 */
package vazkii.quark.vanity.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.client.gui.GuiButtonTranslucent;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.vanity.client.emotes.EmoteDescriptor;
import vazkii.quark.vanity.feature.EmoteSystem;

import javax.annotation.Nonnull;

public class GuiButtonEmote extends GuiButtonTranslucent {

	public final EmoteDescriptor desc;

	public GuiButtonEmote(int buttonId, int x, int y, EmoteDescriptor desc) {
		super(buttonId, x, y, EmoteSystem.EMOTE_BUTTON_WIDTH - 1, EmoteSystem.EMOTE_BUTTON_WIDTH - 1, "");
		this.desc = desc;
	}

	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partial) {
		super.drawButton(mc, mouseX, mouseY, partial);

		if(visible) {
			mc.getTextureManager().bindTexture(desc.texture);
			GlStateManager.color(1F, 1F, 1F);
			drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, 16, 16, 16, 16);

			ResourceLocation tierTexture = desc.getTierTexture();
			if (tierTexture != null) {
				mc.getTextureManager().bindTexture(tierTexture);
				drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, 16, 16, 16, 16);
			}
			
			boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			if(hovered) {
				String name = desc.getLocalizedName();
				
				mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
				int w = mc.fontRenderer.getStringWidth(name);
				int left = x - w;
				int top = y - 8;
				
				GlStateManager.pushMatrix();
				GlStateManager.color(1F, 1F, 1F);
				GlStateManager.translate(0, 0, 100);
				drawActualTexturedModalRect(left, top, 242, 9, 5, 17);
				for(int i = 0; i < w; i++)
					drawActualTexturedModalRect(left + i + 5, top, 248, 9, 1, 17);
				drawActualTexturedModalRect(left + w + 5, top, 250, 9, 6, 17);

				mc.fontRenderer.drawString(name, left + 5, top + 3, 0);
				GlStateManager.popMatrix();
			}
		}
	}

}
