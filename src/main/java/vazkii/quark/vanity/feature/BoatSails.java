/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 17:22:47 (GMT)]
 */
package vazkii.quark.vanity.feature;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.api.capability.IBoatBanner;
import vazkii.quark.base.capability.BackedUpBoatBanner;
import vazkii.quark.base.capability.SimpleProvider;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageSyncBoatBanner;
import vazkii.quark.base.sounds.QuarkSounds;

import java.util.WeakHashMap;

public class BoatSails extends Feature {

	private static final String TAG_BANNER = "quark:banner";
	private static final ResourceLocation BANNER_CAPABILITY = new ResourceLocation(TAG_BANNER);

	private static final WeakHashMap<EntityBoat, ItemStack> cachedStacks = new WeakHashMap<>();
	private static final TIntObjectHashMap<ItemStack> uninitializedStacks = new TIntObjectHashMap<>();

	public static void setBanner(Entity entity, ItemStack banner, boolean sync) {
		if (canHaveBanner(entity)) {
			EntityBoat boat = (EntityBoat) entity;
			ItemStack cached = cachedStacks.get(boat);
			if (cached == null || !sync || !ItemStack.areItemStacksEqual(banner, cached)) {
				IBoatBanner.getBoatBanner(boat).setStack(banner);
				if (sync) {
					cachedStacks.put(boat, banner);
					if (boat.world instanceof WorldServer) {
						WorldServer world = (WorldServer) boat.world;
						for (EntityPlayer target : world.getEntityTracker().getTrackingPlayers(boat))
							syncDataFor(boat, target);
					}
				}
			}
		}
	}

	private static void syncDataFor(Entity target, EntityPlayer player) {
		if (player instanceof EntityPlayerMP && canHaveBanner(target)) {
			ItemStack banner = getBanner(target);
			NetworkHandler.INSTANCE.sendTo(new MessageSyncBoatBanner(target.getEntityId(), banner),
					(EntityPlayerMP) player);
		}
	}

	public static void queueBannerUpdate(int boatID, ItemStack banner) {
		uninitializedStacks.put(boatID, banner);
	}

	@SubscribeEvent
	public void onBoatGenesis(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityBoat)
			event.addCapability(BANNER_CAPABILITY, new SimpleProvider<>(IBoatBanner.CAPABILITY,
					new BackedUpBoatBanner((EntityBoat) event.getObject())));
	}

	@SubscribeEvent
	public void onBoatSeen(PlayerEvent.StartTracking event) {
		syncDataFor(event.getTarget(), event.getEntityPlayer());
	}

	@SubscribeEvent
	public void onBoatArrive(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote) {
			Entity target = event.getEntity();
			int id = target.getEntityId();
			if (uninitializedStacks.containsKey(id))
				setBanner(target, uninitializedStacks.get(id), false);
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteract event) {
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();

		if(canHaveBanner(target) && !target.getPassengers().contains(player)) {
			ItemStack banner = getBanner(target);
			if(!banner.isEmpty())
				return;

			EnumHand hand = EnumHand.MAIN_HAND;
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.isEmpty() || !(stack.getItem() instanceof ItemBanner)) {
				stack = player.getHeldItemOffhand();
				hand = EnumHand.OFF_HAND;
			}

			if(!stack.isEmpty() && stack.getItem() instanceof ItemBanner) {
				ItemStack copyStack = stack.copy();
				player.swingArm(hand);

				setBanner(target, copyStack, !event.getWorld().isRemote);

				if(!event.getWorld().isRemote) {
					event.getWorld().playSound(null, target.posX, target.posY, target.posZ, QuarkSounds.ENTITY_BOAT_ADD_ITEM, SoundCategory.PLAYERS, 1f, 1f);

					event.setCanceled(true);
					event.setCancellationResult(EnumActionResult.SUCCESS);

					if(!player.capabilities.isCreativeMode) {
						stack.shrink(1);

						if(stack.getCount() <= 0)
							player.setHeldItem(hand, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	public static void onBoatUpdate(Entity boat) {
		if (!ModuleLoader.isFeatureEnabled(BoatSails.class) || !canHaveBanner(boat))
			return;

		if (!boat.world.isRemote)
			setBanner(boat, getBanner(boat), true);
	}

	public static boolean canHaveBanner(Entity target) {
		return target instanceof EntityBoat && IBoatBanner.hasBoatBanner(target);
	}

	public static ItemStack getBanner(Entity target) {
		return IBoatBanner.getBoatBanner(target).getStack();
	}

	public static void dropBoatBanner(EntityBoat boat) {
		if(!ModuleLoader.isFeatureEnabled(BoatSails.class))
			return;

		ItemStack banner = getBanner(boat);
		if(!banner.isEmpty()) {
			banner.setCount(1);
			boat.entityDropItem(banner, 0F);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
}
