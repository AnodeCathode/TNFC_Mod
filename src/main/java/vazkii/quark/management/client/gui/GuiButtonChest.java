/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 15:59:35 (GMT)]
 */
package vazkii.quark.management.client.gui;

import com.google.common.collect.BiMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import vazkii.arl.util.RenderHelper;
import vazkii.quark.base.client.IParentedGui;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class GuiButtonChest extends GuiButton implements IParentedGui {

	public final Action action;
	public final GuiScreen parent;
	
	public final int shiftX, shiftY;
	
	private Predicate<GuiScreen> enabledPredicate = null;
	private boolean ender = false;
	
	public GuiButtonChest(GuiScreen parent, Action action, int id, int par2, int par3, int left, int top) {
		super(id, par2 + left, par3 + top, 16, 16, "");
		this.action = action;
		this.parent = parent;
		this.shiftX = par2;
		this.shiftY = par3;
	}

	public GuiButtonChest(GuiScreen parent, Action action, int id, int par2, int par3, int left, int top, Predicate<GuiScreen> enabledPredicate) {
		this(parent, action, id, par2, par3, left, top);
		this.enabledPredicate = enabledPredicate;
	}

	@Override
	public void drawButton(@Nonnull Minecraft par1Minecraft, int par2, int par3, float partial) {
		if(par1Minecraft.player.isSpectator())
			enabled = false;
		
		if(enabledPredicate != null)
			enabled = enabledPredicate.test(parent);

		if(enabled) {
			hovered = par2 >= x && par3 >= y && par2 < x + width && par3 < y + height;
			int k = getHoverState(hovered);

			int u = action.u;
			int v = action.v;

			if(action == Action.DROPOFF && GuiScreen.isShiftKeyDown() != StoreToChests.invert)
				u = 32;

			if(k == 2)
				u += 16;
			
			par1Minecraft.renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
			GlStateManager.color(1F, 1F, 1F, 1F);
			draw(u, v);
			
			if(k == 2) {
				if(action != Action.RESTOCK && !action.isSortAction())
					FavoriteItems.hovering = true;
				
				GlStateManager.pushMatrix();
				String tooltip; 
				if(action == Action.DROPOFF && (GuiScreen.isShiftKeyDown() != StoreToChests.invert))
					tooltip = I18n.format("quarkmisc.chestButton." + action.name().toLowerCase() + ".shift");
					else tooltip = I18n.format("quarkmisc.chestButton." + action.name().toLowerCase());
				int len = Minecraft.getMinecraft().fontRenderer.getStringWidth(tooltip);
				
				int tooltipShift = action == Action.DROPOFF ? 0 : -len - 24;
				
				List<String> tooltipList = new ArrayList<>();
				tooltipList.add(tooltip);
				BiMap<IParentedGui, KeyBinding> map = ModKeybinds.keyboundButtons.inverse();
				if(map.containsKey(this)) {
					KeyBinding key = map.get(this);
					if(key.getKeyCode() != 0) {
						String press = I18n.format("quarkmisc.keyboundButton", TextFormatting.GRAY, GameSettings.getKeyDisplayString(key.getKeyCode()));
						tooltipList.add(press);
						
						if(action != Action.DROPOFF) {
							int len2 = par1Minecraft.fontRenderer.getStringWidth(press);
							if(len2 > len)
								tooltipShift = -len2 - 24;
						}
					}
				}

				GlStateManager.enableDepth();
				GlStateManager.translate(0, 0, par1Minecraft.getRenderItem().zLevel + 300);
				RenderHelper.renderTooltip(par2 + tooltipShift, par3 + 8, tooltipList);
				GlStateManager.popMatrix();
				GlStateManager.disableDepth();
			}
		}
	}

	private void draw(int u, int v) {
		drawChest();
		drawIcon(u, v);
	}

	protected void drawChest() {
		drawIcon(ender ? 32 : 0, 128);
	}
	
	protected void drawIcon(int u, int v) {
		drawTexturedModalRect(x, y, u, v, 16, 16);
	}
	
	public void setEnder(boolean ender) {
		this.ender = ender;
	}
	
	@Override
	public GuiScreen getParent() {
		return parent;
	}

	public enum Action {

		DROPOFF(0, 0),
		DEPOSIT(0, 0),
		SMART_DEPOSIT(32, 0),
		RESTOCK(64, 0),
		EXTRACT(64, 16),
		SORT(0, 16),
		SORT_PLAYER(0, 16);
		
		Action(int u, int v) {
			this.u = u;
			this.v = v;
		}
		
		public final int u, v;
		
		public boolean isSortAction() {
			return this == SORT || this == SORT_PLAYER;
		}

	}

}
