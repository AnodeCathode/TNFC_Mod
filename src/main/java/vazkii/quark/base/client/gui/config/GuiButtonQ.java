package vazkii.quark.base.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.client.gui.GuiButtonColor;
import vazkii.quark.base.lib.LibMisc;

import javax.annotation.Nonnull;

public class GuiButtonQ extends GuiButtonColor {

	public GuiButtonQ(int x, int y) {
		super(-82392, x, y, 20, "q", 0x48ddbc);
	}

	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		
		if(ContributorRewardHandler.localPatronTier > 0) {
			GlStateManager.color(1F, 1F, 1F);
			int tier = Math.min(4, ContributorRewardHandler.localPatronTier);
			int u = 256 - tier * 9;
			int v = 26;
			
			mc.renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
			drawTexturedModalRect(x - 2, y - 2, u, v, 9, 9);
		}
		
	}
	
}
