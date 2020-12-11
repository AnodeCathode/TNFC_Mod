/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:17 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibObfuscation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class EmoteHandler {

	public static final String CUSTOM_EMOTE_NAMESPACE = "quark_custom";
	public static final String CUSTOM_PREFIX = "custom:";
	
	public static final Map<String, EmoteDescriptor> emoteMap = new LinkedHashMap<>();
	private static final Map<String, EmoteBase> playerEmotes = new HashMap<>();

	private static int count;
	
	public static void addEmote(String name, Class<? extends EmoteBase> clazz) {
		EmoteDescriptor desc = new EmoteDescriptor(clazz, name, name, count++);
		emoteMap.put(name, desc);
	}
	
	public static void addEmote(String name) {
		addEmote(name, TemplateSourcedEmote.class);
	}

	public static void addCustomEmote(String name) {
		String reg = CUSTOM_PREFIX + name;
		EmoteDescriptor desc = new CustomEmoteDescriptor(name, reg, count++);
		emoteMap.put(reg, desc);
	}
	
	public static void putEmote(AbstractClientPlayer player, String emoteName, int tier) {
		if(emoteMap.containsKey(emoteName)) {
			putEmote(player, emoteMap.get(emoteName), tier);
		}
	}
	
	public static void putEmote(AbstractClientPlayer player, EmoteDescriptor desc, int tier) {
		String name = player.getName();
		if(desc == null)
			return;

		if (desc.getTier() > tier)
			return;

		ModelBiped model = getPlayerModel(player);
		ModelBiped armorModel = getPlayerArmorModel(player);
		ModelBiped armorLegModel = getPlayerArmorLegModel(player);

		if(model != null && armorModel != null && armorLegModel != null) {
			resetPlayer(player);
			EmoteBase emote = desc.instantiate(player, model, armorModel, armorLegModel);
			emote.startAllTimelines();
			playerEmotes.put(name, emote);
		}
	}

	public static void updateEmotes(Entity e) {
		if(e instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer) e;
			String name = player.getName();
			
			resetPlayer(player);
			
			if(playerEmotes.containsKey(name)) {
				EmoteBase emote = playerEmotes.get(name);
				boolean done = emote.isDone();

				if(!done)
					emote.update();
			}
		}
	}

	public static void preRender(EntityPlayer player) {
		EmoteBase emote = getPlayerEmote(player);
		if (emote != null) {
			GlStateManager.pushMatrix();
			emote.rotateAndOffset();
		}
	}

	public static void postRender(EntityPlayer player) {
		EmoteBase emote = getPlayerEmote(player);
		if (emote != null) {
			GlStateManager.popMatrix();
		}
	}
	
	public static void onRenderTick(Minecraft mc) {
		World world = mc.world;
		if(world == null)
			return;
		
		for(EntityPlayer player : world.playerEntities)
			updatePlayerStatus(player);
	}
	
	private static void updatePlayerStatus(EntityPlayer e) {
		if(e instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer) e;
			String name = player.getName();

			if(playerEmotes.containsKey(name)) {
				EmoteBase emote = playerEmotes.get(name);
				boolean done = emote.isDone();
				if(done) {
					playerEmotes.remove(name);
					resetPlayer(player);
				} else
					emote.update();
			} else resetPlayer(player);
		}
	}
	
	public static EmoteBase getPlayerEmote(EntityPlayer player) {
		return playerEmotes.get(player.getName());
	}

	private static RenderPlayer getRenderPlayer(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		RenderManager manager = mc.getRenderManager();
		return manager.getSkinMap().get(player.getSkinType());
	}

	private static ModelBiped getPlayerModel(AbstractClientPlayer player) {
		RenderPlayer render = getRenderPlayer(player);
		if(render != null)
			return render.getMainModel();
		
		return null;
	}

	private static ModelBiped getPlayerArmorModel(AbstractClientPlayer player) {
		RenderPlayer render = getRenderPlayer(player);
		if(render == null)
			return null;
		
		List list = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, render, LibObfuscation.LAYER_RENDERERS);
		for (Object aList : list)
			if (aList instanceof LayerBipedArmor)
				return ObfuscationReflectionHelper.getPrivateValue(LayerArmorBase.class, (LayerArmorBase) aList, LibObfuscation.MODEL_ARMOR);

		return null;
	}

	private static ModelBiped getPlayerArmorLegModel(AbstractClientPlayer player) {
		RenderPlayer render = getRenderPlayer(player);
		if(render == null)
			return null;
		
		List list = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, render, LibObfuscation.LAYER_RENDERERS);
		for (Object aList : list)
			if (aList instanceof LayerBipedArmor)
				return ObfuscationReflectionHelper.getPrivateValue(LayerArmorBase.class, (LayerArmorBase) aList, LibObfuscation.MODEL_LEGGINGS);
		
		return null;
	}
	
	private static void resetPlayer(AbstractClientPlayer player) {
		resetModel(getPlayerModel(player));
		resetModel(getPlayerArmorModel(player));
		resetModel(getPlayerArmorLegModel(player));
	}

	private static void resetModel(ModelBiped model) {
		if (model != null) {
			resetPart(model.bipedHead);
			resetPart(model.bipedHeadwear);
			resetPart(model.bipedBody);
			resetPart(model.bipedLeftArm);
			resetPart(model.bipedRightArm);
			resetPart(model.bipedLeftLeg);
			resetPart(model.bipedRightLeg);
			if (model instanceof ModelPlayer) {
				ModelPlayer playerModel = (ModelPlayer) model;
				resetPart(playerModel.bipedBodyWear);
				resetPart(playerModel.bipedLeftArmwear);
				resetPart(playerModel.bipedRightArmwear);
				resetPart(playerModel.bipedLeftLegwear);
				resetPart(playerModel.bipedRightLegwear);
				resetPart(ModelAccessor.getEarsModel(playerModel));
			}

			ModelAccessor.INSTANCE.resetModel(model);
		}
	}
	
	private static void resetPart(ModelRenderer part) {
		if(part != null)
			part.rotateAngleZ = part.offsetX = part.offsetY = part.offsetZ = 0F;
	}

}
