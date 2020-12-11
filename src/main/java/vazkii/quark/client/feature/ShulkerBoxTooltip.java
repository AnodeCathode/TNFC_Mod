package vazkii.quark.client.feature;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.feature.RightClickAddToShulkerBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShulkerBoxTooltip extends Feature {

	public static final ResourceLocation WIDGET_RESOURCE = new ResourceLocation("quark", "textures/misc/shulker_widget.png");

	public static boolean useColors, requireShift;

	public static List<ResourceLocation> shulkerBoxes;
	public static boolean dropoffAnyShulkerBox;

	@Override
	public void setupConfig() {
		String[] shulkerArr = loadPropStringList("Shulker Boxes",
				"Blocks which should be interpreted as Shulker Boxes.",
				RightClickAddToShulkerBox.getBasicShulkerBoxes());
		shulkerBoxes = Arrays.stream(shulkerArr).map(ResourceLocation::new).collect(Collectors.toList());

		dropoffAnyShulkerBox = loadPropBool("Dropoff to Any Shulker Box",
				"Allow anything with 'shulker_box' in its item identifier to be treated as a shulker box?", true);

		useColors = loadPropBool("Use Colors", "", true);
		requireShift = loadPropBool("Needs Shift to be visible", "", false);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void makeTooltip(ItemTooltipEvent event) {
		if(RightClickAddToShulkerBox.isShulkerBox(event.getItemStack(), shulkerBoxes, dropoffAnyShulkerBox) && event.getItemStack().hasTagCompound()) {
			Minecraft mc = Minecraft.getMinecraft();

			NBTTagCompound cmp = ItemNBTHelper.getCompound(event.getItemStack(), "BlockEntityTag", true);
			if (cmp != null) {
				if (!cmp.hasKey("id", Constants.NBT.TAG_STRING)) {
					cmp = cmp.copy();
					cmp.setString("id", "minecraft:shulker_box");
				}
				TileEntity te = TileEntity.create(mc.world, cmp);
				if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
					List<String> tooltip = event.getToolTip();
					List<String> tooltipCopy = new ArrayList<>(tooltip);

					for (int i = 1; i < tooltipCopy.size(); i++) {
						String s = tooltipCopy.get(i);
						if (!s.startsWith("\u00a7") || s.startsWith("\u00a7o"))
							tooltip.remove(s);
					}

					if (requireShift && !GuiScreen.isShiftKeyDown())
						tooltip.add(1, I18n.format("quarkmisc.shulkerBoxShift"));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderTooltip(RenderTooltipEvent.PostText event) {
		if(RightClickAddToShulkerBox.isShulkerBox(event.getStack(), shulkerBoxes, dropoffAnyShulkerBox) && event.getStack().hasTagCompound() && (!requireShift || GuiScreen.isShiftKeyDown())) {
			Minecraft mc = Minecraft.getMinecraft();

			NBTTagCompound cmp = ItemNBTHelper.getCompound(event.getStack(), "BlockEntityTag", true);
			if (cmp != null) {
				if (!cmp.hasKey("id", Constants.NBT.TAG_STRING)) {
					cmp = cmp.copy();
					cmp.setString("id", "minecraft:shulker_box");
				}
				TileEntity te = TileEntity.create(mc.world, cmp);
				if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
					ItemStack currentBox = event.getStack();
					int currentX = event.getX() - 5;
					int currentY = event.getY() - 70;

					IItemHandler capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					assert capability != null;

					int size = capability.getSlots();
					int[] dims = { Math.min(size, 9), Math.max(size / 9, 1) };
					for (int[] testAgainst : TARGET_RATIOS) {
						if (testAgainst[0] * testAgainst[1] == size) {
							dims = testAgainst;
							break;
						}
					}

					int texWidth = CORNER * 2 + EDGE * dims[0];

					if (currentY < 0)
						currentY = event.getY() + event.getLines().size() * 10 + 5;

					ScaledResolution res = new ScaledResolution(mc);
					int right = currentX + texWidth;
					if (right > res.getScaledWidth())
						currentX -= (right - res.getScaledWidth());

					GlStateManager.pushMatrix();
					RenderHelper.enableStandardItemLighting();
					GlStateManager.enableRescaleNormal();
					GlStateManager.color(1F, 1F, 1F);
					GlStateManager.translate(0, 0, 700);
					mc.getTextureManager().bindTexture(WIDGET_RESOURCE);

					RenderHelper.disableStandardItemLighting();

					int color = -1;

					if (useColors && ((ItemBlock) currentBox.getItem()).getBlock() instanceof BlockShulkerBox) {
						EnumDyeColor dye = ((BlockShulkerBox) ((ItemBlock) currentBox.getItem()).getBlock()).getColor();
						color = ItemDye.DYE_COLORS[dye.getDyeDamage()];
					}

					renderTooltipBackground(mc, currentX, currentY, dims[0], dims[1], color);

					RenderItem render = mc.getRenderItem();

					RenderHelper.enableGUIStandardItemLighting();
					GlStateManager.enableDepth();
					for (int i = 0; i < size; i++) {
						ItemStack itemstack = capability.getStackInSlot(i);
						int xp = currentX + 6 + (i % 9) * 18;
						int yp = currentY + 6 + (i / 9) * 18;

						if (!itemstack.isEmpty()) {
							render.renderItemAndEffectIntoGUI(itemstack, xp, yp);
							render.renderItemOverlays(mc.fontRenderer, itemstack, xp, yp);
						}

						if (!ChestSearchBar.namesMatch(itemstack, ChestSearchBar.text)) {
							GlStateManager.disableDepth();
							Gui.drawRect(xp, yp, xp + 16, yp + 16, 0xAA000000);
						}
					}

					GlStateManager.disableDepth();
					GlStateManager.disableRescaleNormal();
					GlStateManager.popMatrix();
				}
			}
		}
	}

	private static final int[][] TARGET_RATIOS = new int[][] {
			{ 1, 1 },
			{ 9, 3 },
			{ 9, 5 },
			{ 9, 6 },
			{ 9, 8 },
			{ 9, 9 },
			{ 12, 9 }
	};

	private static final int CORNER = 5;
	private static final int BUFFER = 1;
	private static final int EDGE = 18;


	public static void renderTooltipBackground(Minecraft mc, int x, int y, int width, int height, int color) {
		mc.getTextureManager().bindTexture(WIDGET_RESOURCE);
		GlStateManager.color(((color & 0xFF0000) >> 16) / 255f,
				((color & 0x00FF00) >> 8) / 255f,
				(color & 0x0000FF) / 255f);

		RenderHelper.disableStandardItemLighting();

		Gui.drawModalRectWithCustomSizedTexture(x, y,
				0, 0,
				CORNER, CORNER, 256, 256);
		Gui.drawModalRectWithCustomSizedTexture(x + CORNER + EDGE * width, y + CORNER + EDGE * height,
				CORNER + BUFFER + EDGE + BUFFER, CORNER + BUFFER + EDGE + BUFFER,
				CORNER, CORNER, 256, 256);
		Gui.drawModalRectWithCustomSizedTexture(x + CORNER + EDGE * width, y,
				CORNER + BUFFER + EDGE + BUFFER, 0,
				CORNER, CORNER, 256, 256);
		Gui.drawModalRectWithCustomSizedTexture(x, y + CORNER + EDGE * height,
				0, CORNER + BUFFER + EDGE + BUFFER,
				CORNER, CORNER, 256, 256);
		for (int row = 0; row < height; row++) {
			Gui.drawModalRectWithCustomSizedTexture(x, y + CORNER + EDGE * row,
					0, CORNER + BUFFER,
					CORNER, EDGE, 256, 256);
			Gui.drawModalRectWithCustomSizedTexture(x + CORNER + EDGE * width, y + CORNER + EDGE * row,
					CORNER + BUFFER + EDGE + BUFFER, CORNER + BUFFER,
					CORNER, EDGE, 256, 256);
			for (int col = 0; col < width; col++) {
				if (row == 0) {
					Gui.drawModalRectWithCustomSizedTexture(x + CORNER + EDGE * col, y,
							CORNER + BUFFER, 0,
							EDGE, CORNER, 256, 256);
					Gui.drawModalRectWithCustomSizedTexture(x + CORNER + EDGE * col, y + CORNER + EDGE * height,
							CORNER + BUFFER, CORNER + BUFFER + EDGE + BUFFER,
							EDGE, CORNER, 256, 256);
				}

				Gui.drawModalRectWithCustomSizedTexture(x + CORNER + EDGE * col, y + CORNER + EDGE * row,
						CORNER + BUFFER, CORNER + BUFFER,
						EDGE, EDGE, 256, 256);
			}
		}

		GlStateManager.color(1F, 1F, 1F);
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
