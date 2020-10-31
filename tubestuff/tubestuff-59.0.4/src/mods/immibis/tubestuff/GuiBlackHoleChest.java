package mods.immibis.tubestuff;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBlackHoleChest extends GuiContainer {
	private ContainerBlackHoleChest container;
	public GuiBlackHoleChest(EntityPlayer player, TileBlackHoleChest chest) {
		super(new ContainerBlackHoleChest(player, chest));
		this.container = (ContainerBlackHoleChest)inventorySlots;
		xSize = 242;
		ySize = 203; 
	}
	
	private void sendButton(int button)
	{
		container.sendButtonPressed(button);
	}
	
	@Override
	protected void mouseClicked(int real_x, int real_y, int button)
    {
		int x = (real_x - this.guiLeft);
		int y = (real_y - this.guiTop);
		if(button == 0)
		{
			if(x >= 194 && y >= 122 && x <= 212 && y <= 139)
				// previous page button
				sendButton(3);
			else if(x >= 220 && y >= 122 && x <= 238 && y <= 139)
				// next page button
				sendButton(4);
			else
				super.mouseClicked(real_x, real_y, button);
		}
		else
			super.mouseClicked(real_x, real_y, button);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(R.gui.bhc);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        
        if(container.chest.maxPages == -1)
        	container.chest.updateMaxPages();
        
        fontRendererObj.drawString(I18n.format("gui.tubestuff.bhc.page", container.page+1, container.chest.maxPages), 172 + l, 156 + i1, 0x404040);
	}
}
