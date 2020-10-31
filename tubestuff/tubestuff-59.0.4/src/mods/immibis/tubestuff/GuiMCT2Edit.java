package mods.immibis.tubestuff;

import mods.immibis.core.api.util.BaseGuiContainer;
import net.minecraft.inventory.Slot;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiMCT2Edit extends BaseGuiContainer<ContainerMCT2Edit> {
	
	private static final boolean SNAPPY_SCROLLING = false;
	
	private float scrollPos; // measured in recipes

	public GuiMCT2Edit(ContainerMCT2Edit c) {
		super(c, 186, 215, R.gui.mct2);
	}
	
	private int dwheel;
	
	private boolean drawnFrame = false;
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawnFrame = false;
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		prepareSlotPositions(true);
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	protected void mouseClickMove(int par1, int par2, int par3, long par4) {
		prepareSlotPositions(true);
		super.mouseClickMove(par1, par2, par3, par4);
	}
	
	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3) {
		prepareSlotPositions(true);
		super.mouseMovedOrUp(par1, par2, par3);
	}
	
	/*@Override
	protected void drawSlotInventory(Slot par1Slot) {
		if(!drawnFrame && par1Slot.inventory != container.getInv()) {
			mc.renderEngine.bindTexture(R.gui.mct2);
			GL11.glColor3f(1, 1, 1);
			GL11.glDepthFunc(GL11.GL_ALWAYS);
			drawFrame();
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			drawnFrame = true;
		}
		super.drawSlotInventory(par1Slot);
	}*/
	
	private void drawRecipeScroller() {
		// draw scrollable area background
		drawTexturedModalRect(13+guiLeft, 10+guiTop, 13, 69, 148, 59);
		drawTexturedModalRect(13+guiLeft, 69+guiTop, 13, 69, 148, 59);
		
		// draw recipes
		for(int k = 0; k < TileMCT2.numRecipes; k++) {
			int rx = 13;
			int ry = 10 + (int)((k - scrollPos) * 59);
			drawTexturedModalRect(rx+guiLeft, ry+guiTop, 13, 10, 148, 59);
		}
	}
	
	private void prepareSlotPositions(boolean forClick) {
		for(int k = 0; k < TileMCT2.numRecipes; k++) {
			int rx = 13;
			int ry = 10 + (int)((k - scrollPos) * 59);
			for(int sy = 0; sy < 3; sy++)
				for(int sx = 0; sx < 3; sx++) {
					Slot slot = container.getRecipeSlot(k, sx + sy*3);
					slot.xDisplayPosition = rx + sx*18 + 18;
					slot.yDisplayPosition = ry + sy*18 + 4;
					if(forClick && slot.yDisplayPosition > 128-16 && slot.yDisplayPosition < ySize)
						slot.yDisplayPosition = -10000000;
				}
		}
	}
	
	private void drawFrame() {
		drawTexturedModalRect(0, 0, 0, 0, 186, 10);
		drawTexturedModalRect(0, 10, 0, 10, 13, 205);
		drawTexturedModalRect(13, 128, 13, 128, 173, 87);
		drawTexturedModalRect(161, 10, 161, 10, 25, 118);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
		x -= guiLeft; y -= guiTop;
		
		mc.renderEngine.bindTexture(R.gui.mct2);
		GL11.glColor3f(1, 1, 1);
		prepareSlotPositions(false);
		drawRecipeScroller();
		
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0);
		
		
		//drawRecipeScroller();
		//drawFrame();
		
		// draw and process scrollbar
		{
			int maxScroll = Math.max(0, TileMCT2.numRecipes - 2);
			if(Mouse.isButtonDown(0) && x >= 164 && y >= 0 && x < 173 && y < 138) {
				float temp = (y - 10) / 118.0f * maxScroll;
				
				scrollPos = SNAPPY_SCROLLING ? (int)(temp + 0.5f) : temp;
			}
			scrollPos -= Math.signum(dwheel) * 0.5; dwheel = 0;
			scrollPos = Math.max(0, Math.min(scrollPos, maxScroll));
			drawTexturedModalRect(164, 10 + (int)(scrollPos * 107 / maxScroll), 186, 0, 9, 11);
		}
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		
		dwheel += Mouse.getDWheel();
	}
}
