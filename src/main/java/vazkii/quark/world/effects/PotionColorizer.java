/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 08, 2019, 17:52 AM (EST)]
 */
package vazkii.quark.world.effects;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.network.message.MessageSyncColors;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.base.util.MutableVectorHolder;

import java.util.Collection;
import java.util.WeakHashMap;

public class PotionColorizer extends PotionMod {

	public final int color;

	public final float r;
	public final float g;
	public final float b;

	public PotionColorizer(String name, int color, int iconIndex) {
		super(name, true, color, iconIndex);
		this.color = color;
		this.r = ((color & 0xff0000) >> 16) / 255f;
		this.g = ((color & 0xff00) >> 8) / 255f;
		this.b = (color & 0xff) / 255f;
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getEntityPlayer() instanceof EntityPlayerMP && event.getTarget() instanceof EntityLivingBase)
			NetworkHandler.INSTANCE.sendTo(prepareMessage((EntityLivingBase) event.getTarget()),
					(EntityPlayerMP) event.getEntityPlayer());
	}

	@SubscribeEvent
	public static void onCalculateColor(PotionColorCalculationEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;
		if (world instanceof WorldServer) {
			for (EntityPlayer tracker : ((WorldServer) world).getEntityTracker()
					.getTrackingPlayers(entity)) {
				if (tracker instanceof EntityPlayerMP)
					NetworkHandler.INSTANCE.sendTo(prepareMessage(entity.getEntityId(), event.getEffects()),
							(EntityPlayerMP) tracker);
			}
		}

		if (entity instanceof EntityPlayerMP)
			NetworkHandler.INSTANCE.sendTo(prepareMessage(entity.getEntityId(), event.getEffects()),
					(EntityPlayerMP) entity);
	}

	private static MessageSyncColors prepareMessage(EntityLivingBase entity) {
		return prepareMessage(entity.getEntityId(), entity.getActivePotionEffects());
	}

	private static MessageSyncColors prepareMessage(int id, Collection<PotionEffect> effects) {
		float rMix = 0, gMix = 0, bMix = 0;
		int total = 0;
		for (PotionEffect effect : effects) {
			if (effect.getPotion() instanceof PotionColorizer) {
				rMix += ((PotionColorizer) effect.getPotion()).r;
				gMix += ((PotionColorizer) effect.getPotion()).g;
				bMix += ((PotionColorizer) effect.getPotion()).b;
				total++;
			}
		}

		if (total > 0) {
			rMix /= total;
			gMix /= total;
			bMix /= total;
			return new MessageSyncColors(id, rMix, gMix, bMix);
		}

		return new MessageSyncColors(id, -1, -1, -1);
	}

	private static final WeakHashMap<EntityLivingBase, MutableVectorHolder> colors = new WeakHashMap<>();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void colorize(RenderLivingEvent.Pre event) {
		EntityLivingBase entity = event.getEntity();
		if (entity.getDataManager().get(ClientReflectiveAccessor.potionEffectColor()) == 0)
			return;
		MutableVectorHolder holder = colors.get(event.getEntity());
		if (holder != null)
			GlStateManager.color((float) holder.x, (float) holder.y, (float) holder.z);
	}


	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void colorize(RenderLivingEvent.Post event) {
		if (colors.containsKey(event.getEntity()))
			GlStateManager.color(1f, 1f, 1f);
	}

	public static void receiveColors(EntityLivingBase entity, double r, double g, double b) {
		if (r < 0 || g < 0 || b < 0) {
			colors.remove(entity);
		} else {
			MutableVectorHolder holder = colors.computeIfAbsent(entity, (e) -> new MutableVectorHolder());
			holder.x = r;
			holder.y = g;
			holder.z = b;
		}
	}
}
