/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 06, 2019, 15:23 AM (EST)]
 */
package vazkii.quark.world.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiArchaeologist extends GuiMerchant {
	public GuiArchaeologist(InventoryPlayer player, IMerchant merchant, World world) {
		super(player, merchant, world);
	}

	private int mouseX, mouseY;

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawHoveringText(List<String> textLines, int x, int y, @Nonnull FontRenderer font) {
		if (textLines.size() == 1 && x == mouseX && y == mouseY && textLines.get(0).equals(I18n.format("merchant.deprecated")))
			textLines.set(0, I18n.format("quarkmisc.item_out_of_stock"));

		super.drawHoveringText(textLines, x, y, font);
	}
}
