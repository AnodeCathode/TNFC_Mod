package vazkii.quark.base.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import javax.annotation.Nonnull;

public class GuiButtonColor extends GuiButton {
	
	private final int color;
		
	public GuiButtonColor(int i, int x, int y, int w, String text, int color) {
		super(i, x, y, w, 20, text);
		this.color = color;
	}
	
	@Override
	public void drawCenteredString(FontRenderer fontRendererIn, @Nonnull String text, int x, int y, int color) {
		super.drawCenteredString(fontRendererIn, text, x, y, this.color);
	}

}
