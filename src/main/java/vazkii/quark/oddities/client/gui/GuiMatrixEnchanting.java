package vazkii.quark.oddities.client.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.network.message.MessageMatrixEnchanterOperation;
import vazkii.quark.oddities.client.gui.button.GuiButtonMatrixEnchantingPlus;
import vazkii.quark.oddities.feature.MatrixEnchanting;
import vazkii.quark.oddities.inventory.ContainerMatrixEnchanting;
import vazkii.quark.oddities.inventory.EnchantmentMatrix;
import vazkii.quark.oddities.inventory.EnchantmentMatrix.Piece;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GuiMatrixEnchanting extends GuiContainer {

	public static final ResourceLocation BACKGROUND = new ResourceLocation(LibMisc.MOD_ID, "textures/misc/matrix_enchanting.png");

	protected final InventoryPlayer playerInv;
	protected final TileMatrixEnchanter enchanter;

	protected GuiButton plusButton;
	protected PieceList pieceList;
	protected Piece hoveredPiece;

	protected int selectedPiece = -1;
	protected int gridHoverX, gridHoverY;
	protected List<Integer> listPieces = null;
	
	public GuiMatrixEnchanting(InventoryPlayer playerInv, TileMatrixEnchanter enchanter) {
		super(new ContainerMatrixEnchanting(playerInv, enchanter));
		this.playerInv = playerInv;
		this.enchanter = enchanter;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		selectedPiece = -1;
		addButton(plusButton = new GuiButtonMatrixEnchantingPlus(guiLeft + 86, guiTop + 63));
		pieceList = new PieceList(this, 29, 64, guiTop + 11, guiLeft + 139, 22);
		updateButtonStatus();
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		updateButtonStatus();
		
		if(enchanter.matrix == null)
			selectedPiece = -1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		int i = guiLeft;
		int j = guiTop;
		drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

		if(enchanter.charge > 0 && MatrixEnchanting.chargePerLapis > 0) {
			int maxHeight = 18;
			int barHeight = (int) (((float) enchanter.charge / MatrixEnchanting.chargePerLapis) * maxHeight);
			drawTexturedModalRect(i + 7, j + 32 + maxHeight - barHeight, 50, 176 + maxHeight - barHeight, 4, barHeight);
		}

		if(enchanter.matrix != null && enchanter.matrix.canGeneratePiece(enchanter.bookshelfPower, enchanter.enchantability)) {
			int x = i + 74;
			int y = j + 58;
			int xpCost = enchanter.matrix.getNewPiecePrice();
			int xpMin = enchanter.matrix.getMinXpLevel(enchanter.bookshelfPower);
			boolean has = enchanter.matrix.validateXp(mc.player, enchanter.bookshelfPower);
			drawTexturedModalRect(x, y, 0, ySize, 10, 10);
			String text = String.valueOf(xpCost);

			if(!has && mc.player.experienceLevel < xpMin) {
				fontRenderer.drawStringWithShadow("!", x + 6, y + 3, 0xFF0000);
				text = I18n.format("quarkmisc.matrixMin", xpMin);
			}

			x -= (fontRenderer.getStringWidth(text) - 5);
			y += 3;
			fontRenderer.drawString(text, x - 1, y, 0);
			fontRenderer.drawString(text, x + 1, y, 0);
			fontRenderer.drawString(text, x, y + 1, 0);
			fontRenderer.drawString(text, x, y - 1, 0);
			fontRenderer.drawString(text, x, y, has ? 0xc8ff8f : 0xff8f8f);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(enchanter.getDisplayName().getUnformattedText(), 12, 5, 4210752);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
		
		if(enchanter.matrix != null) {
			listPieces = enchanter.matrix.benchedPieces;
			renderMatrixGrid(enchanter.matrix);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(enchanter.matrix != null) {
			RenderHelper.disableStandardItemLighting();
			pieceList.drawScreen(mouseX, mouseY, ClientTicker.partialTicks);
		}

		if(hoveredPiece != null) {
			List<String> tooltip = new LinkedList<>();
			tooltip.add(hoveredPiece.enchant.getTranslatedName(hoveredPiece.level));

			if(hoveredPiece.influence > 0)
				tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixInfluence", (int) (hoveredPiece.influence * MatrixEnchanting.influencePower * 100)));

			int max = hoveredPiece.getMaxXP();
			if(max > 0)
				tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixUpgrade", hoveredPiece.xp, max));
			
			if(gridHoverX == -1) {
				tooltip.add("");
				tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixLeftClick"));
				tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixRightClick"));
			} else if(selectedPiece != -1) {
				Piece p = getPiece(selectedPiece);
				if(p != null && p.enchant == hoveredPiece.enchant && hoveredPiece.level < hoveredPiece.enchant.getMaxLevel()) {
					tooltip.add("");
					tooltip.add(TextFormatting.GRAY + I18n.format("quarkmisc.matrixMerge"));
				}
			}
			drawHoveringText(tooltip, mouseX, mouseY);
		} else renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		
		if(enchanter.matrix != null)
			pieceList.handleMouseInput(mouseX, mouseY);
		
		int gridMouseX = mouseX - guiLeft - 86;
		int gridMouseY = mouseY - guiTop - 11;
		
		gridHoverX = gridMouseX < 0 ? -1 : gridMouseX / 10;
		gridHoverY = gridMouseY < 0 ? -1 : gridMouseY / 10;
		if(gridHoverX < 0 || gridHoverX > 4 || gridHoverY < 0 || gridHoverY > 4) {
			gridHoverX = -1;
			gridHoverY = -1;
			hoveredPiece = null;
		} else if(enchanter.matrix != null) {
			int hover = enchanter.matrix.matrix[gridHoverX][gridHoverY];
			hoveredPiece = getPiece(hover);
		}
		
		super.handleMouseInput();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(enchanter.matrix == null)
			return;
		
		if(mouseButton == 0 && gridHoverX != -1) { // left click
			int hover = enchanter.matrix.matrix[gridHoverX][gridHoverY];

			if(selectedPiece != -1) {
				if(hover == -1)
					place(selectedPiece, gridHoverX, gridHoverY);
				else merge(selectedPiece);
			} else {
				remove(hover);
				if(!isShiftKeyDown())
					selectedPiece = hover;
			}
		} else if(mouseButton == 1 && selectedPiece != -1)
			rotate(selectedPiece);
	}
	
	private void renderMatrixGrid(EnchantmentMatrix matrix) {
		mc.getTextureManager().bindTexture(BACKGROUND);
		GlStateManager.pushMatrix();
		GlStateManager.translate(86, 11, 0);
		
		for(int i : matrix.placedPieces) {
			Piece piece = getPiece(i);
			if (piece != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(piece.x * 10, piece.y * 10, 0);
				renderPiece(piece, 1F);
				GlStateManager.popMatrix();
			}
		}
		
		if(selectedPiece != -1 && gridHoverX != -1) {
			Piece piece = getPiece(selectedPiece);
			if(piece != null && !(hoveredPiece != null && piece.enchant == hoveredPiece.enchant && hoveredPiece.level < hoveredPiece.enchant.getMaxLevel())) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(gridHoverX * 10, gridHoverY * 10, 0);
				
				float a = 0.2F;
				if(matrix.canPlace(piece, gridHoverX, gridHoverY))
					a = (float) ((Math.sin(ClientTicker.total * 0.2) + 1) * 0.4 + 0.4);
				
				renderPiece(piece, a);
				GlStateManager.popMatrix();
			}
		}
		
		if(hoveredPiece == null && gridHoverX != -1)
			renderHover(gridHoverX, gridHoverY);
			
		GlStateManager.popMatrix();
	}
	
	private void renderPiece(Piece piece, float a) {
		float r = ((piece.color >> 16) & 0xFF) / 255F;
		float g = ((piece.color >> 8) & 0xFF) / 255F;
		float b = (piece.color & 0xFF) / 255F;
		
		boolean hovered = hoveredPiece == piece;
		
		for(int[] block : piece.blocks)
			renderBlock(block[0], block[1], piece.type, r, g, b, a, hovered);
		
		GlStateManager.color(1F, 1F, 1F);
	}
	
	private void renderBlock(int x, int y, int type, float r, float g, float b, float a, boolean hovered) {
		GlStateManager.color(r, g, b, a);
		drawTexturedModalRect(x * 10, y * 10, 11 + type * 10, ySize, 10, 10);
		if(hovered)
			renderHover(x, y);
	}
	
	private void renderHover(int x, int y) {
		drawRect(x * 10, y * 10, x * 10 + 10, y * 10 + 10, 0x66FFFFFF);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == plusButton)
			add();
	}
	
	public void add() {
		send(TileMatrixEnchanter.OPER_ADD, 0, 0, 0);
	}
	
	public void place(int id, int x, int y) {
		send(TileMatrixEnchanter.OPER_PLACE, id, x, y);
		selectedPiece = -1;
		click();
	}
	
	public void remove(int id) {
		send(TileMatrixEnchanter.OPER_REMOVE, id, 0, 0);
	}
	
	public void rotate(int id) {
		send(TileMatrixEnchanter.OPER_ROTATE, id, 0, 0);
	}
	
	public void merge(int id) {
		int hover = enchanter.matrix.matrix[gridHoverX][gridHoverY];
		Piece p = getPiece(hover);
		Piece p1 = getPiece(selectedPiece);
		if(p != null && p1 != null && p.enchant == p1.enchant && p.level < p.enchant.getMaxLevel()) {
			send(TileMatrixEnchanter.OPER_MERGE, hover, id, 0);
			selectedPiece = -1;
			click();
		}
	}	
	
	private void send(int operation, int arg0, int arg1, int arg2) {
		MessageMatrixEnchanterOperation message = new MessageMatrixEnchanterOperation(operation, arg0, arg1, arg2);
		NetworkHandler.INSTANCE.sendToServer(message);
	}
	
	private void click() {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
	
	private void updateButtonStatus() {
		plusButton.enabled = (enchanter.matrix != null 
				&& enchanter.charge > 0
				&& enchanter.matrix.validateXp(mc.player, enchanter.bookshelfPower)
				&& enchanter.matrix.canGeneratePiece(enchanter.bookshelfPower, enchanter.enchantability));
	}
	
	private Piece getPiece(int id) {
		EnchantmentMatrix matrix = enchanter.matrix;
		if(matrix != null)
			return matrix.pieces.get(id);
		
		return null;
	}
	
	public static class PieceList extends GuiScrollingList {

		private final GuiMatrixEnchanting parent;
		private int mouseX, mouseY;
		
		public PieceList(GuiMatrixEnchanting parent, int width, int height, int top, int left, int entryHeight) {
			super(parent.mc, width, height, top, top + height, left, entryHeight, parent.width, parent.height);
			this.parent = parent;
		}
		
		@Override
		protected int getSize() {
			return parent.listPieces == null ? 0 : parent.listPieces.size();
		}

		@Override
		protected void elementClicked(int index, boolean doubleClick) {
			int id = parent.listPieces.get(index);
			if(parent.selectedPiece == id)
				parent.selectedPiece = -1;
			else parent.selectedPiece = id;
		}

		@Override
		protected boolean isSelected(int index) {
			int id = parent.listPieces.get(index);
			return parent.selectedPiece == id;
		}

		@Override
		protected void drawBackground() {
			// NO-OP
		}
		
		@Override
		public void handleMouseInput(int mouseX, int mouseY) throws IOException {
			super.handleMouseInput(mouseX, mouseY);
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}

		@Override
		protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
			int id = parent.listPieces.get(slotIdx);
			
			Piece piece = parent.getPiece(id);
			if(piece != null) {
				if(mouseX >= left && mouseX < left + listWidth - 7 && mouseY >= slotTop && mouseY <= slotTop + slotHeight && mouseY < bottom)
					parent.hoveredPiece = piece;
				
				parent.mc.getTextureManager().bindTexture(BACKGROUND);
				GlStateManager.pushMatrix();
				GlStateManager.translate(left + (listWidth - 7) / 2f, slotTop + slotHeight / 2f, 0);
				GlStateManager.scale(0.5, 0.5, 0.5);
				GlStateManager.translate(-4, -8, 0);
				parent.renderPiece(piece, 1F);
				GlStateManager.popMatrix();
			}
		}
		
	}
	
}
