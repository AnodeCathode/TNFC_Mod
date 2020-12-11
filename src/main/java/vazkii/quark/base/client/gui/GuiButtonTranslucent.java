/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [27/03/2016, 00:02:53 (GMT)]
 */
package vazkii.quark.base.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GuiButtonTranslucent extends GuiButton {

	public GuiButtonTranslucent(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}

	@Override
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		drawRect(x, y, x + width, y + height, Integer.MIN_VALUE);
	}
	
	public void drawActualTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625f;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, this.zLevel)
				.tex(textureX * f, (textureY + height) * f).endVertex();
		bufferbuilder.pos(x + width, y + height, this.zLevel)
				.tex((textureX + width) * f, (textureY + height) * f).endVertex();
		bufferbuilder.pos(x + width, y, this.zLevel)
				.tex((textureX + width) * f, textureY * f).endVertex();
		bufferbuilder.pos(x, y, this.zLevel)
				.tex(textureX * f, textureY * f).endVertex();
		tessellator.draw();
	}

}
