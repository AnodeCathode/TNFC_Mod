package tnfcmod.client.gui.inventory;


import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import tnfcmod.inventory.ContainerPlowCartTFC;
import tnfcmod.objects.entities.EntityPlowCartTFC;

import static tnfcmod.tnfcmod.MODID;

public class GuiPlowCartTFC extends GuiContainer {
    private static final ResourceLocation PLOW_GUI_TEXTURES = new ResourceLocation(MODID, "textures/gui/container/plow.png");

    private final InventoryPlayer playerInventory;
    private final IInventory plowInventory;

    public GuiPlowCartTFC(InventoryPlayer playerInv, IInventory plowInv, EntityPlowCartTFC plowIn, EntityPlayer player) {
        super(new ContainerPlowCartTFC(playerInv, plowInv, plowIn, player));
        this.playerInventory = playerInv;
        this.plowInventory = plowInv;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = this.plowInventory.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(PLOW_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
