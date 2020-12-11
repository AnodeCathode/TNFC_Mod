package vazkii.quark.base.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;

import javax.annotation.Nonnull;

public class GuiButtonModule extends GuiButton {

	private boolean projectRedCrashing = false;
	
	public final Module module;
	
	public GuiButtonModule(int x, int y, Module module) {
		super(0, x, y, 150, 20, I18n.format("quark.config.module." + module.name));
		this.module = module;
	}
	
	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		
		if(visible && !projectRedCrashing) {
			ItemStack stack = module.getIconStack();
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableDepth();
			
			try {
				mc.getRenderItem().renderItemIntoGUI(stack, x + 6, y + 2);
			} catch(NullPointerException e) {
				projectRedCrashing = true;
			}
		}
	}
	
	@Override
	public void drawCenteredString(FontRenderer fontRendererIn, @Nonnull String text, int x, int y, int color) {
		super.drawCenteredString(fontRendererIn, text, x + 14, y, color);
	}

}
