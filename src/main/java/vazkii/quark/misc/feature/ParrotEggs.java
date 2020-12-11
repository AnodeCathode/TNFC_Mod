package vazkii.quark.misc.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.client.render.RenderParrotEgg;
import vazkii.quark.misc.client.render.RenderParrotKoto;
import vazkii.quark.misc.entity.EntityParrotEgg;
import vazkii.quark.misc.item.ItemParrotEgg;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ParrotEggs extends Feature {

	private static final ResourceLocation KOTO = new ResourceLocation("quark", "textures/entity/kotobirb.png");

	private static final String TAG_EGG_TIMER = "quark:parrot_egg_timer";

	public static Item parrot_egg;

	public static Item item;
	public static double chance;
	public static int eggTime;
	public static boolean enableKotobirb;

	@SideOnly(Side.CLIENT)
	public static ResourceLocation getTextureForParrot(ResourceLocation previous, NBTTagCompound parrot) {
		if (!ModuleLoader.isFeatureEnabled(ParrotEggs.class) || !enableKotobirb)
			return previous;


		UUID uuid = parrot.getUniqueId("UUID");
		Class<?> entityClass = EntityList.getClassFromName(parrot.getString("id"));

		if (entityClass == EntityParrot.class &&
				parrot.getInteger("Variant") == 4 &&
				(uuid == null || uuid.getLeastSignificantBits() % 20 == 0))
				return KOTO;

		return previous;
	}

	@Override
	public void setupConfig() {
		item = Items.BEETROOT_SEEDS;
		String itemName = loadPropString("Feed Item", "", Objects.toString(item.getRegistryName()));
		Item targetItem = Item.REGISTRY.getObject(new ResourceLocation(itemName));
		if(targetItem != null)
			item = targetItem;

		chance = loadLegacyPropChance("Success Percentage Chance", "Success Chance",
				"If this is X, the parrot will, on average, start making an egg in every 1/X seeds fed", 0.05);
		eggTime = loadPropInt("Egg Creation Time", "", 12000);
		enableKotobirb = loadPropBool("Enable Special Awesome Parrot", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		parrot_egg = new ItemParrotEgg();

		String eggName = "quark:parrot_egg";
		EntityRegistry.registerModEntity(new ResourceLocation(eggName), EntityParrotEgg.class, eggName, LibEntityIDs.PARROT_EGG, Quark.instance, 64, 10, true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityParrotEgg.class, RenderParrotEgg.factory());
		
		if(enableKotobirb)
			RenderingRegistry.registerEntityRenderingHandler(EntityParrot.class, RenderParrotKoto.factory());
	}

	@SubscribeEvent
	public void entityInteract(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() != null) {
			Entity e = event.getTarget();
			if(e instanceof EntityParrot && e.getEntityData().getInteger(TAG_EGG_TIMER) == 0) {
				EntityParrot parrot = (EntityParrot) e;
				if(!parrot.isTamed())
					return;
				
				EntityPlayer player = event.getEntityPlayer();
				ItemStack stack = player.getHeldItemMainhand();
				if(stack.isEmpty() || stack.getItem() != item) {
					stack = player.getHeldItemOffhand();
				}

				if(!stack.isEmpty() && stack.getItem() == item) {
					event.setCanceled(true);
					if(e.world.isRemote || event.getHand() == EnumHand.OFF_HAND)
						return;
					
					if(!player.isCreative())
						stack.shrink(1);
					
					WorldServer ws = (WorldServer) e.world;
					ws.playSound(null, e.posX, e.posY, e.posZ, SoundEvents.ENTITY_PARROT_EAT, SoundCategory.NEUTRAL, 1.0F, 1.0F + (ws.rand.nextFloat() - ws.rand.nextFloat()) * 0.2F);

					if(e.world.rand.nextDouble() < chance) {
						e.getEntityData().setInteger(TAG_EGG_TIMER, eggTime);
						ws.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, e.posX, e.posY, e.posZ, 10, e.width, e.height, e.width, 0);
					} else ws.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, e.posX, e.posY, e.posZ, 10, e.width, e.height, e.width, 0);
				}
			}
		}
	}

	@SubscribeEvent
	public void entityUpdate(LivingUpdateEvent event) {
		Entity e = event.getEntity();
		if(e instanceof EntityParrot) {
			int time = e.getEntityData().getInteger(TAG_EGG_TIMER);
			if(time > 0) {
				if(time == 1) {
					e.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (e.world.rand.nextFloat() - e.world.rand.nextFloat()) * 0.2F + 1.0F);
					e.entityDropItem(new ItemStack(parrot_egg, 1, getResultingEggColor((EntityParrot) e)), 0);
				}
				e.getEntityData().setInteger(TAG_EGG_TIMER, time - 1);
			}
		}
	}

	private int getResultingEggColor(EntityParrot parrot) {
		int color = parrot.getVariant();
		Random rand = parrot.world.rand;
		if(rand.nextBoolean())
			return color;
		return rand.nextInt(5);
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
