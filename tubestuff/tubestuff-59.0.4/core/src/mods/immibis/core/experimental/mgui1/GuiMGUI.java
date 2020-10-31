package mods.immibis.core.experimental.mgui1;

import immibis.mgui.MClientControl;
import immibis.mgui.XYValue;
import immibis.mgui.control_impls.ClientFluidSlot;
import immibis.mgui.control_impls.ClientItemSlot;
import immibis.mgui.hosting.IControlRenderHook;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class GuiMGUI extends GuiContainer implements IControlRenderHook {
	public GuiMGUI(ContainerMGUIClient container) {
		super(container);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		ContainerMGUIClient c = (ContainerMGUIClient)inventorySlots;
		c.msd.cursorPos = new XYValue(var2, var3);
		
		ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		c.msd.screenWidth = res.getScaledWidth();
		c.msd.screenHeight = res.getScaledHeight();
		c.msd.renderHook = this;
		
		xSize = c.mainWindow.width;
		ySize = c.mainWindow.height;
		guiLeft = (c.msd.screenWidth - xSize) / 2;
		guiTop = (c.msd.screenHeight - ySize) / 2;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		c.mainWindow.render();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_,
		int p_146979_2_) {
		// TODO Auto-generated method stub
		super.drawGuiContainerForegroundLayer(p_146979_1_, p_146979_2_);
	}

	@Override
	public void preRender(MClientControl c) {
	}

	@Override
	public void postRender(MClientControl c) {
		if(c instanceof ClientItemSlot)
			renderItems((ClientItemSlot)c);
		if(c instanceof ClientFluidSlot)
			renderFluid((ClientFluidSlot)c);
	}
	
	private void renderFluid(ClientFluidSlot c) {
		if(c.amount <= 0 || c.maxAmount <= 0)
			return;
		IIcon icon = null;
		
		Fluid f = FluidRegistry.getFluid(c.fluidID);
		if(f != null)
			icon = f.getStillIcon();
		
		if(icon == null)
			icon = Blocks.fire.getIcon(0, 0);
		
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		
		XYValue controlPos = c.getPositionInWindow();
		
		if(c.direction == ClientFluidSlot.Direction.HORZ_RIGHT) {
			int pixels = (int)((double)c.amount * (c.width - ClientFluidSlot.BORDER_WIDTH*2) / c.maxAmount);
			
			int left = controlPos.x+ClientFluidSlot.BORDER_WIDTH;
			int right = left+pixels;
			int top = controlPos.y+ClientFluidSlot.BORDER_WIDTH;
			int bottom = top+c.height-ClientFluidSlot.BORDER_WIDTH*2;
			
			int curX = left;
			GL11.glBegin(GL11.GL_QUADS);
			float bottomv = icon.getInterpolatedV(Math.min(16, bottom - top));
			while(curX < right) {
				curX = Math.min(curX+16, right);
				
				GL11.glTexCoord2f(icon.getMinU(), bottomv);
				GL11.glVertex2f(left, bottom);
				GL11.glTexCoord2f(icon.getMaxU(), bottomv);
				GL11.glVertex2f(curX, bottom);
				GL11.glTexCoord2f(icon.getMaxU(), icon.getMaxV());
				GL11.glVertex2f(curX, top);
				GL11.glTexCoord2f(icon.getMinU(), icon.getMaxV());
				GL11.glVertex2f(left, top);
				
				left = curX;
			}
			GL11.glEnd();
			
		} else if(c.direction == ClientFluidSlot.Direction.VERT_UP) {
			int pixels = (int)((double)c.amount * (c.height - ClientFluidSlot.BORDER_WIDTH*2) / c.maxAmount);
			
			int left = controlPos.x+ClientFluidSlot.BORDER_WIDTH;
			int right = left+c.width-ClientFluidSlot.BORDER_WIDTH*2;
			int bottom = controlPos.y+c.height - ClientFluidSlot.BORDER_WIDTH;
			int top = bottom - pixels;
			
			int curY = bottom;
			GL11.glBegin(GL11.GL_QUADS);
			float rightu = icon.getInterpolatedU(Math.min(16, right - left));
			while(curY > top) {
				curY = Math.max(curY-16, top);
				
				GL11.glTexCoord2f(icon.getMinU(), icon.getMinV());
				GL11.glVertex2f(left, bottom);
				GL11.glTexCoord2f(rightu, icon.getMinV());
				GL11.glVertex2f(right, bottom);
				GL11.glTexCoord2f(rightu, icon.getMaxV());
				GL11.glVertex2f(right, top);
				GL11.glTexCoord2f(icon.getMinU(), icon.getMaxV());
				GL11.glVertex2f(left, top);
				
				bottom = curY;
			}
			GL11.glEnd();
		}
	}

	private void renderItems(ClientItemSlot c) {
		//Method renderSlot = ReflectionHelper.findMethod(GuiContainer.class, this, new String[] {"func_146977_a"}, Slot.class);;
		
		XYValue controlPos = c.getPositionInWorkspace();
		
		int slotIndex = 0;
		for(int y = 0; y < c.height/18; y++) {
			for(int x = 0; x < c.width/18; x++) {
				int combinedInventorySlotIndex = ((ContainerMGUIClient)inventorySlots).getContainerSlotIndexForControl(c.getIdentifier(), slotIndex);
				if(combinedInventorySlotIndex < 0)
					continue;

				Slot slotObject = (Slot)inventorySlots.inventorySlots.get(combinedInventorySlotIndex);
				slotObject.xDisplayPosition = x*18+1+controlPos.x-guiLeft;
				slotObject.yDisplayPosition = y*18+1+controlPos.y-guiTop;
				slotIndex++;
			}
		}
	}
}
