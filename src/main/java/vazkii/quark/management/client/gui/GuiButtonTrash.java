package vazkii.quark.management.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vazkii.arl.util.RenderHelper;
import vazkii.quark.base.client.IParentedGui;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.management.feature.DeleteItems;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class GuiButtonTrash extends GuiButton implements IParentedGui {

	public final GuiScreen parent;
	public final int shiftX, shiftY;
	private final boolean needsShift;
	public boolean ready;

	public GuiButtonTrash(GuiScreen parent, int id, int shiftX, int shiftY, boolean needsShift) {
		super(id, 0, 0, 16, 16, "");
		this.parent = parent;
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.needsShift = needsShift;
	}
	
	@Override
	public void drawButton(@Nonnull Minecraft par1Minecraft, int par2, int par3, float partial) {
		hovered = par2 >= x && par3 >= y && par2 < x + width && par3 < y + height;
		int k = getHoverState(hovered);

		int u = 0;
		int v = 192;
		
		boolean canDelete = false;
		boolean open = false;
		
		if(parent instanceof GuiContainer) {
			EntityPlayer player = par1Minecraft.player;
			ItemStack hovered = player.inventory.getItemStack();
			canDelete = DeleteItems.canItemBeDeleted(hovered);
			open = canDelete && (!needsShift || GuiScreen.isShiftKeyDown());
			
			if(open)
				u += 16;
		}
		
		par1Minecraft.renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
		GlStateManager.color(1F, 1F, 1F, 1F);
		drawIcon(u, v);
		
		ready = false;
		if(k == 2 && canDelete) {
			if(open)
				ready = true;
			
			GlStateManager.pushMatrix();
			String tooltip = I18n.format(open ? "quarkmisc.trashButtonOpen" : "quarkmisc.trashButtonShift");
			int len = Minecraft.getMinecraft().fontRenderer.getStringWidth(tooltip);
			int tooltipShift = 2;
			List<String> tooltipList = Collections.singletonList(tooltip);

			GlStateManager.enableDepth();
			RenderHelper.renderTooltip(par2 + tooltipShift, par3 + 8, tooltipList);
			GlStateManager.disableDepth();
			GlStateManager.popMatrix();
		}
	}

	protected void drawIcon(int u, int v) {
		drawTexturedModalRect(x, y, u, v, 16, 16);
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn) {
		// NO-OP
	}

	
	@Override
	public GuiScreen getParent() {
		return parent;
	}
	
}
