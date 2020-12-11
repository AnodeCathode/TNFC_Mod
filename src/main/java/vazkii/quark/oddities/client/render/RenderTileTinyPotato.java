/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jul 18, 2014, 10:48:46 PM (GMT)]
 */
package vazkii.quark.oddities.client.render;

import java.util.Calendar;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.oddities.block.BlockTinyPotato;
import vazkii.quark.oddities.client.model.ModelTinyPotato;
import vazkii.quark.oddities.feature.TinyPotato;
import vazkii.quark.oddities.item.ItemBlockTinyPotato;
import vazkii.quark.oddities.tile.TileTinyPotato;

public class RenderTileTinyPotato extends TileEntitySpecialRenderer<TileTinyPotato> {
	private static final ResourceLocation texture = new ResourceLocation(LibMisc.MOD_ID, "textures/blocks/tiny_potato/tiny_potato.png");
	private static final ResourceLocation textureGrayscale = new ResourceLocation(LibMisc.MOD_ID, "textures/blocks/tiny_potato/tiny_potato_gray.png");
	private static final ResourceLocation textureHalloween = new ResourceLocation(LibMisc.MOD_ID, "textures/blocks/tiny_potato/tiny_potato_halloween.png");
	private static final ResourceLocation textureAngery = new ResourceLocation(LibMisc.MOD_ID, "textures/blocks/tiny_potato/angry_potato.png");

	private static final ResourceLocation textureGoldHat = new ResourceLocation("minecraft", "textures/models/armor/gold_layer_1.png");
	private static final ResourceLocation textureDiamondHat = new ResourceLocation("minecraft", "textures/models/armor/diamond_layer_1.png");
	
	private static final ModelTinyPotato model = new ModelTinyPotato();
	private static final ModelBiped armorModel = new ModelBiped(1F);

	@SuppressWarnings("MagicConstant")
	public static boolean isTheSpookDay(World world) {
		Calendar calendar = world.getCurrentDate();

		return calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DAY_OF_MONTH) == 31;
	}

	@Override
	public void render(@Nonnull TileTinyPotato potato, double x, double y, double z, float partialTicks, int destroyStage, float unused) {
		if(!potato.getWorld().isBlockLoaded(potato.getPos(), false) || potato.getWorld().getBlockState(potato.getPos()).getBlock() != TinyPotato.tiny_potato)
			return;

		IBlockState potatoState = potato.getWorld().getBlockState(potato.getPos());

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(x, y, z);

		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(isTheSpookDay(potato.getWorld()) ? textureHalloween : texture);
		String name = potato.name.toLowerCase().trim();

		GlStateManager.translate(0.5F, 1.5F, 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		float rotY = potatoState.getValue(BlockTinyPotato.FACING).getHorizontalAngle();
		GlStateManager.rotate(rotY, 0F, 1F, 0F);

		float jump = potato.jumpTicks;
		if (jump > 0)
			jump -= partialTicks;

		float up = (float) -Math.abs(Math.sin(jump / 10 * Math.PI)) * 0.2F;
		float rotZ = (float) Math.sin(jump / 10 * Math.PI) * 2;

		GlStateManager.translate(0F, up, 0F);
		GlStateManager.rotate(rotZ, 0F, 0F, 1F);

		GlStateManager.pushMatrix();
		switch (name) {
		case "pahimar":
			GlStateManager.scale(1F, 0.3F, 1F);
			GlStateManager.translate(0F, 3.5F, 0F);
			break;
		case "kyle hyde":
			mc.renderEngine.bindTexture(textureGrayscale);
			break;
		case "dinnerbone":
		case "grumm":
			GlStateManager.rotate(180F, 0F, 0F, 1F);
			GlStateManager.translate(0F, -2.625F, 0F);
			break;
		case "aureylian":
			GlStateManager.color(1F, 0.5F, 1F);
			break;
		}

		if (potato.angery)
			mc.renderEngine.bindTexture(textureAngery);


		boolean render = !(name.equals("mami") || name.equals("soaryn") || name.equals("eloraam") && jump != 0);
		if (render)
			model.render();

		GlStateManager.popMatrix();

		int patronTier = ContributorRewardHandler.getTier(name);
		if(patronTier > 0) {
			GlStateManager.pushMatrix();
			float s = 1F / 35F;
			GlStateManager.scale(s, s, s);
			GlStateManager.translate(0, 46, 0);
			mc.renderEngine.bindTexture(patronTier > 1 ? textureDiamondHat : textureGoldHat);
			
			armorModel.bipedHead.render(1F);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 0, 0, 1);
		GlStateManager.translate(0F, -1F, 0F);
		float s = 1F / 3.5F;
		GlStateManager.scale(s, s, s);

		EnumFacing potatoFacing = potato.getWorld().getBlockState(potato.getPos()).getValue(BlockTinyPotato.FACING);

		for(int i = 0; i < potato.getSizeInventory(); i++) {
			ItemStack stack = potato.getStackInSlot(i);
			if(stack.isEmpty())
				continue;

			GlStateManager.pushMatrix();
			EnumFacing side = EnumFacing.class.getEnumConstants()[i];
			if(side.getAxis() != Axis.Y) {
				float sideAngle = side.getHorizontalAngle() - potatoFacing.getHorizontalAngle();
				side = EnumFacing.fromAngle(sideAngle);
			}

			boolean block = stack.getItem() instanceof ItemBlock;
			boolean mySon = stack.getItem() instanceof ItemBlockTinyPotato;

			switch(side) {
				case UP:
					if(mySon)
						GlStateManager.translate(0F, 0.6F, 0.5F);
					else if(block)
						GlStateManager.translate(0F, 0.3F, 0.5F);
					GlStateManager.translate(0F, -0.5F, -0.4F);
					break;
				case DOWN:
					GlStateManager.translate(0, -2.3, -0.88);
					if(mySon)
						GlStateManager.translate(0, .65, 0.6);
					else if(block)
						GlStateManager.translate(0, 1, 0.6);
					break;
				case NORTH:
					GlStateManager.translate(0, -1.9, 0.02);
					if(mySon)
						GlStateManager.translate(0, 1, 0.6);
					else if(block)
						GlStateManager.translate(0, 1, 0.6);
					break;
				case SOUTH:
					GlStateManager.translate(0, -1.6, -0.89);
					if(mySon)
						GlStateManager.translate(0, 1.4, 0.5);
					else if(block)
						GlStateManager.translate(0, 1.0, 0.5);
					break;
				case EAST:
					if(mySon)
						GlStateManager.translate(-0.4F, 0.65F, 0F);
					else if(block)
						GlStateManager.translate(-0.4F, 0.8F, 0F);
					else GlStateManager.rotate(-90F, 0F, 1F, 0F);
					GlStateManager.translate(-0.3F, -1.9F, 0.04F);
					break;
				case WEST:
					if(mySon)
						GlStateManager.translate(1F, 0.65F, 1F);
					else if(block)
						GlStateManager.translate(1F, 0.8F, 1F);
					else GlStateManager.rotate(-90F, 0F, 1F, 0F);
					GlStateManager.translate(-0.3F, -1.9F, -0.92F);
					break;
			}

			if (mySon)
				GlStateManager.scale(1.1, 1.1, 1.1);
			else if(block)
				GlStateManager.scale(0.5, 0.5, 0.5);

			if (block && side == EnumFacing.NORTH)
				GlStateManager.rotate(180F, 0, 1, 0);
			renderItem(stack);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();

		GlStateManager.rotate(-rotZ, 0F, 0F, 1F);
		GlStateManager.rotate(-rotY, 0F, 1F, 0F);
		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.scale(1F, -1F, -1F);

		RayTraceResult pos = mc.objectMouseOver;
		if (!name.isEmpty() && pos != null && potato.getPos().equals(pos.getBlockPos())) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0F, -0.6F, 0F);
			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			float f = 1.6F;
			float f1 = 0.016666668F * f;
			GlStateManager.scale(-f1, -f1, f1);
			GlStateManager.disableLighting();
			GlStateManager.translate(0.0F, 0F / f1, 0.0F);
			GlStateManager.depthMask(false);
			GlStateManager.enableBlend();
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder worldrenderer = tessellator.getBuffer();
			GlStateManager.disableTexture2D();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			int i = mc.fontRenderer.getStringWidth(potato.name) / 2;
			worldrenderer.pos(-i - 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			worldrenderer.pos(-i - 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			worldrenderer.pos(i + 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			worldrenderer.pos(i + 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
			GlStateManager.depthMask(true);
			mc.fontRenderer.drawString(potato.name, -mc.fontRenderer.getStringWidth(potato.name) / 2, 0, 0xFFFFFF);
			if (name.equals("pahimar") || name.equals("soaryn")) {
				GlStateManager.translate(0F, 14F, 0F);
				String str = name.equals("pahimar") ? "[WIP]" : "(soon)";
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GlStateManager.disableTexture2D();
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
				i = mc.fontRenderer.getStringWidth(str) / 2;
				worldrenderer.pos(-i - 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				worldrenderer.pos(-i - 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				worldrenderer.pos(i + 1, 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				worldrenderer.pos(i + 1, -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				tessellator.draw();
				GlStateManager.enableTexture2D();
				GlStateManager.depthMask(true);
				mc.fontRenderer.drawString(str, -mc.fontRenderer.getStringWidth(str) / 2, 0, 0xFFFFFF);
			}

			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.scale(1F / -f1, 1F / -f1, 1F / f1);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();
	}

	private void renderItem(ItemStack stack) {
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.HEAD);
	}

	private void renderBlock(Block block) {
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(block.getDefaultState(), 1.0F);
	}
}
