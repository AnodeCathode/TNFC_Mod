package vazkii.quark.misc.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class PoisonPotatoUsage extends Feature {

	private static final String TAG_POISONED = "quark:poison_potato_applied";

	public static double chance;
	public static boolean poisonEffect;
	
	@Override
	public void setupConfig() {
		chance = loadPropChance("Chance to Poison", "", 0.1);
		poisonEffect = loadPropBool("Apply Poison to the poisoned entity", "", true);
	}
	
	@SubscribeEvent
	public void onInteract(EntityInteract event) {
		if(event.getTarget() instanceof EntityAnimal && event.getItemStack().getItem() == Items.POISONOUS_POTATO) {
			EntityAnimal animal = (EntityAnimal) event.getTarget();
			if(animal.isChild() && !isEntityPoisoned(animal)) {
				if(!event.getWorld().isRemote) {
					if(animal.world.rand.nextDouble() < chance) {
						animal.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.25f);
						animal.world.spawnParticle(EnumParticleTypes.SPELL_MOB, animal.posX, animal.posY, animal.posZ, 0.2, 0.8, 0);
						poisonEntity(animal);
						if (poisonEffect)
							animal.addPotionEffect(new PotionEffect(MobEffects.POISON, 200));
					} else {
						animal.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f, 0.5f + animal.world.rand.nextFloat() / 2);
						animal.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, animal.posX, animal.posY, animal.posZ, 0, 0.1, 0);
					}

					if (!event.getEntityPlayer().isCreative())
						event.getItemStack().shrink(1);

				} else event.getEntityPlayer().swingArm(event.getHand());

			}
		}
	}
	
	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityAnimal) {
			EntityAnimal animal = (EntityAnimal) event.getEntity();
			if(animal.isChild() && isEntityPoisoned(animal))
				animal.setGrowingAge(-24000);
		}
	}
	
	private boolean isEntityPoisoned(Entity e) {
		return e.getEntityData().getBoolean(TAG_POISONED);
	}
	
	private void poisonEntity(Entity e) {
		e.getEntityData().setBoolean(TAG_POISONED, true);
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
