package vazkii.quark.management.client.gui;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class GuiButtonShulker extends GuiButtonChest {

	public GuiButtonShulker(GuiShulkerBox parent, Action action, int id, int par2, int par3, int left, int top) {
		super(parent, action, id, par2, par3, left, top);
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	protected void drawChest() {
		Minecraft mc = Minecraft.getMinecraft();
		BlockPos pos = mc.objectMouseOver.getBlockPos();
		if (pos != null) {
			TileEntity tile = mc.world.getTileEntity(pos);
			if (tile instanceof TileEntityShulkerBox) {
				TileEntityShulkerBox shulker = (TileEntityShulkerBox) tile;
				EnumDyeColor dye = ((BlockShulkerBox) shulker.getBlockType()).getColor();
				int color = ItemDye.DYE_COLORS[dye.getDyeDamage()];
				Color colorObj = new Color(color).brighter();
				GlStateManager.color(colorObj.getRed() / 255F, colorObj.getGreen() / 255F, colorObj.getBlue() / 255F);
				super.drawIcon(16, 128);
				GlStateManager.color(1F, 1F, 1F);
				return;
			}
		}

		super.drawChest();
	}

}
