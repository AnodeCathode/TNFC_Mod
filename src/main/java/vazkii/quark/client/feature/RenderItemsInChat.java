/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 29, 2019, 14:37 AM (EST)]
 */
package vazkii.quark.client.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

import java.util.List;

public class RenderItemsInChat extends Feature {

	private static int chatX, chatY;

	public static ITextComponent createStackComponent(ITextComponent component) {
		if (!ModuleLoader.isFeatureEnabled(RenderItemsInChat.class))
			return component;
		ITextComponent out = new TextComponentString("   ");
		out.setStyle(component.getStyle().createDeepCopy());
		return out.appendSibling(component);
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void getChatPos(RenderGameOverlayEvent.Chat event) {
		chatX = event.getPosX();
		chatY = event.getPosY();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderSymbols(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiIngame gameGui = mc.ingameGUI;
		GuiNewChat chatGui = gameGui.getChatGUI();
		if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {
			int updateCounter = gameGui.getUpdateCounter();
			List<ChatLine> lines = ClientReflectiveAccessor.getChatDrawnLines(chatGui);
			int shift = ClientReflectiveAccessor.getScrollPos(chatGui);

			int idx = shift;

			while (idx < lines.size() && (idx - shift) < chatGui.getLineCount()) {
				ChatLine line = lines.get(idx);
				String before = "";

				String currentText = TextFormatting.getTextWithoutFormattingCodes(line.getChatComponent().getUnformattedComponentText());
				if (currentText != null && currentText.startsWith("   "))
					render(mc, chatGui, updateCounter, before, line, idx - shift, line.getChatComponent());
				before += currentText;

				for (ITextComponent sibling : line.getChatComponent().getSiblings()) {
					currentText = TextFormatting.getTextWithoutFormattingCodes(sibling.getUnformattedComponentText());
					if (currentText != null && currentText.startsWith("   "))
						render(mc, chatGui, updateCounter, before, line, idx - shift, sibling);
					before += currentText;
				}

				idx++;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private static void render(Minecraft mc, GuiNewChat chatGui, int updateCounter, String before, ChatLine line, int lineHeight, ITextComponent component) {
		Style style = component.getStyle();
		HoverEvent hoverEvent = style.getHoverEvent();
		if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
			ItemStack stack = ItemStack.EMPTY;

			try {
				NBTTagCompound textValue = JsonToNBT.getTagFromJson(hoverEvent.getValue().getUnformattedText());
				stack = new ItemStack(textValue);
			} catch (NBTException ignored) {
				// NO-OP
			}

			if (stack.isEmpty())
				stack = new ItemStack(Blocks.BARRIER); // for invalid icon

			int timeSinceCreation = updateCounter - line.getUpdatedCounter();
			if (chatGui.getChatOpen()) timeSinceCreation = 0;

			if (timeSinceCreation < 200) {
				float chatOpacity = mc.gameSettings.chatOpacity * 0.9f + 0.1f;
				float fadeOut = MathHelper.clamp((1 - timeSinceCreation / 200f) * 10, 0, 1);
				float alpha = fadeOut * fadeOut * chatOpacity;

				int x = chatX + 3 + mc.fontRenderer.getStringWidth(before);
				int y = chatY - mc.fontRenderer.FONT_HEIGHT * lineHeight;

				if (alpha > 0) {
					RenderHelper.enableGUIStandardItemLighting();
					ALPHA_VALUE = ((int) (alpha * 255) << 24);

					renderItemIntoGUI(mc, mc.getRenderItem(), stack, x, y);

					ALPHA_VALUE = -1;
					RenderHelper.disableStandardItemLighting();
				}
			}
		}
	}

	public static int transformColor(int src) {
		if (ALPHA_VALUE == -1)
			return src;
		return (src & RGB_MASK) | ALPHA_VALUE;
	}

	public static final int RGB_MASK = 0x00FFFFFF;
	private static int ALPHA_VALUE = -1;

	@SideOnly(Side.CLIENT)
	private static void renderItemIntoGUI(Minecraft mc, RenderItem render, ItemStack stack, int x, int y) {
		renderItemModelIntoGUI(mc, render, stack, x, y, render.getItemModelWithOverrides(stack, null, null));
	}

	@SideOnly(Side.CLIENT)
	private static void renderItemModelIntoGUI(Minecraft mc, RenderItem render, ItemStack stack, int x, int y, IBakedModel model) {
		TextureManager textureManager = mc.getTextureManager();

		GlStateManager.pushMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(-4, -4, -4);
		ClientReflectiveAccessor.setupGuiTransform(render, x, y, model.isGui3d());
		GlStateManager.scale(0.65, 0.65, 0.65);
		model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
		render.renderItem(stack, model);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}
}
