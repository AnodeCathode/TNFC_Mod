package vazkii.quark.base.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import vazkii.quark.base.lib.LibMisc;

import javax.annotation.Nonnull;

public class GuiButtonFeatureSettings extends GuiButton {

	public final String category;
	
	public GuiButtonFeatureSettings(int x, int y, String category) {
		super(0, x, y, 20, 20, "C");
		this.category = category;
	}
	
	@Override
	public void drawCenteredString(FontRenderer fontRendererIn, @Nonnull String text, int x, int y, int color) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
		drawTexturedModalRect(this.x + 2, this.y + 2, 32, 228, 16, 16);
	}

}
