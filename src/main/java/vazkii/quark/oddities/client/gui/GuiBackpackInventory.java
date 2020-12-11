package vazkii.quark.oddities.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.network.message.MessageHandleBackpack;
import vazkii.quark.oddities.feature.Backpacks;
import vazkii.quark.oddities.inventory.ContainerBackpack;

public class GuiBackpackInventory extends GuiInventory {
	
	private static final ResourceLocation BACKPACK_INVENTORY_BACKGROUND = new ResourceLocation(LibMisc.MOD_ID, "textures/misc/backpack_gui.png");
	
	private final EntityPlayer player;
	private GuiButton recipeButton;
	private int recipeButtonY;
	
	private boolean closeHack = false;
	
	public GuiBackpackInventory(EntityPlayer player) {
		super(player);
		
		this.player = player;
		inventorySlots = new ContainerBackpack(player);
	}
	
	@Override
	public void initGui() {
		ySize = 224;
		super.initGui();
		
		for(GuiButton button : buttonList)
			if(button.id == 10) {
				button.y -= 29;
				
				recipeButton = button;
				recipeButtonY = button.y;
			}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		
		recipeButton.y = recipeButtonY;
		
		if(!Backpacks.isEntityWearingBackpack(player)) {
			ItemStack curr = player.inventory.getItemStack();
			ContainerBackpack.saveCraftingInventory(player);
			closeHack = true;
			NetworkHandler.INSTANCE.sendToServer(new MessageHandleBackpack(false));
			mc.displayGuiScreen(new GuiInventory(player));
			player.inventory.setItemStack(curr);
		}
	}
	
	@Override
	public void onGuiClosed() {
		if(closeHack) {
			closeHack = false;
			return;
		}
			
		super.onGuiClosed();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(BACKPACK_INVENTORY_BACKGROUND);
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
		drawEntityOnScreen(i + 51, j + 75, 30, (float)(i + 51) - mouseX, (float)(j + 75 - 50) - mouseY, this.mc.player);
	}
	
}
