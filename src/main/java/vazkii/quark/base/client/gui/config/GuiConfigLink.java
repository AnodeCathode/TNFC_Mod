package vazkii.quark.base.client.gui.config;

import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;

public class GuiConfigLink extends GuiConfirmOpenLink {
	
	private final GuiScreen parent;

	public GuiConfigLink(GuiScreen parentScreenIn, String url) {
		super(parentScreenIn, url, 0, true);
		parent = parentScreenIn;
	}
	
	@Override 
	protected void keyTyped(char typedChar, int keyCode) {
		if(keyCode == 1) // Esc
			returnToParent();
	}
	
	void returnToParent() {
		mc.displayGuiScreen(parent);

		if(mc.currentScreen == null)
			mc.setIngameFocus();
	}

}
