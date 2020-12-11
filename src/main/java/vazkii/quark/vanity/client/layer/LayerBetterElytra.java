/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [21/03/2016, 00:14:23 (GMT)]
 */
package vazkii.quark.vanity.client.layer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.vanity.feature.DyableElytra;

import javax.annotation.Nonnull;
import java.awt.*;

@SideOnly(Side.CLIENT)
public class LayerBetterElytra implements LayerRenderer<AbstractClientPlayer> {

	private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
	private static final ResourceLocation TEXTURE_ELYTRA_DYED = new ResourceLocation(LibMisc.MOD_ID, "textures/misc/elytra_dyed.png");
	private final RenderPlayer renderPlayer;
	private final ModelElytra modelElytra = new ModelElytra();

	public LayerBetterElytra(RenderPlayer renderPlayerIn) {
		renderPlayer = renderPlayerIn;
	}

	@Override
	public void doRenderLayer(@Nonnull AbstractClientPlayer living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		ItemStack itemstack = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

		if(!itemstack.isEmpty() && itemstack.getItem() == Items.ELYTRA) {
			int colorIndex = ItemNBTHelper.getInt(itemstack, DyableElytra.TAG_ELYTRA_DYE, -1);
			boolean dyed = colorIndex != -1 && colorIndex != 15; 

			if(!dyed)
				GlStateManager.color(1F, 1F, 1F);
			else {
				Color color = new Color(ItemDye.DYE_COLORS[colorIndex]);
				float r = color.getRed() / 255F;
				float g = color.getGreen() / 255F;
				float b = color.getBlue() / 255F;
				GlStateManager.color(r, g, b);
			}

			if(living.isPlayerInfoSet() && living.getLocationElytra() != null)
				renderPlayer.bindTexture(living.getLocationElytra());
			else if(living.hasPlayerInfo() && living.getLocationCape() != null && living.isWearing(EnumPlayerModelParts.CAPE))
				renderPlayer.bindTexture(living.getLocationCape());
			else if(dyed)
				renderPlayer.bindTexture(TEXTURE_ELYTRA_DYED);
			else renderPlayer.bindTexture(TEXTURE_ELYTRA);

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 0.125F);
			modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, living);
			modelElytra.render(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

			if(itemstack.isItemEnchanted()) {
				ColorRunes.setTargetStack(itemstack);
				LayerArmorBase.renderEnchantedGlint(renderPlayer, living, modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
			}
			
			GlStateManager.color(1F, 1F, 1F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
