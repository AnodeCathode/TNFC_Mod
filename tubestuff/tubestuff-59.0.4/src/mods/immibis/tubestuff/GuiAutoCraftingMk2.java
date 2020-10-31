package mods.immibis.tubestuff;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAutoCraftingMk2 extends GuiContainer {
	private ContainerAutoCraftingMk2 container;
	public GuiAutoCraftingMk2(EntityPlayer player, TileAutoCraftingMk2 table) {
		super(new ContainerAutoCraftingMk2(player, table));
		container = (ContainerAutoCraftingMk2)inventorySlots;
		
		xSize = 186;
		ySize = 249; 
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(R.gui.act2);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
        
        stackButton.displayString = I18n.format("gui.tubestuff.act2.button.stack" + (container.tile.craftMany ? "On" : "Off"));
	}
	
	private GuiButton stackButton, oreDictButton, clearButton;
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		
		clearButton = new GuiButton(2, guiLeft + 156, guiTop + 100, 20, 20, I18n.format("gui.tubestuff.act2.button.clear"));
		stackButton = new GuiButton(0, guiLeft + 156, guiTop + 122, 20, 20, I18n.format("gui.tubestuff.act2.button.stack" + (container.tile.craftMany ? "On" : "Off")));
		oreDictButton = new GuiButton(1, guiLeft + 156, guiTop + 144, 20, 20, I18n.format("gui.tubestuff.act2.button.oredict"));
		
		buttonList.add(stackButton);
		buttonList.add(oreDictButton);
		buttonList.add(clearButton);
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		((ContainerAutoCraftingMk2)inventorySlots).sendButtonPressed(par1GuiButton.id);
	}
}
