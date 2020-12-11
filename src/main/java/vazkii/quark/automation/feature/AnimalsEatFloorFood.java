package vazkii.quark.automation.feature;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

import java.util.Comparator;
import java.util.List;

public class AnimalsEatFloorFood extends Feature {

	public static double maxDensity;

	@Override
	public void setupConfig() {
		maxDensity = loadPropDouble("Maximum entity density", "Prevents entities from proliferating infinitely. Set to 0 or less to disable checking.", 2);
	}

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityAnimal && event.getEntityLiving().world instanceof WorldServer) {
			EntityAnimal animal = (EntityAnimal) event.getEntityLiving();
			if(animal.getGrowingAge() == 0 && animal.ticksExisted % 20 == 0 && !animal.isInLove() && !animal.isDead) {
				double range = 2;


				double density = 0;

				if (maxDensity > 0) {
					List<Entity> animals = animal.world.getEntitiesWithinAABBExcludingEntity(animal, animal.getEntityBoundingBox().grow(10));

					for (Entity other : animals) {
						double dSq = other.getDistanceSq(animal);

						if (dSq <= 100) {
							density += 1 / dSq;

							if (density > maxDensity)
								return;
						}
					}
				}

				List<EntityItem> nearbyFood = animal.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, animal.getEntityBoundingBox().expand(range, 0, range),
						(EntityItem i) -> i != null && !i.getItem().isEmpty() && !i.isDead && animal.isBreedingItem(i.getItem()) && i.getItem().getItem() != Items.ROTTEN_FLESH);
				
				if(!nearbyFood.isEmpty()) {
					nearbyFood.sort(Comparator.comparingDouble(ent -> ent.getDistanceSq(animal)));
					EntityItem e = nearbyFood.get(0);
					
					ItemStack stack = e.getItem();
					ItemStack original = stack.copy();
					stack.shrink(1);
					e.setItem(stack);
					if(stack.isEmpty())
						e.setDead();

					if (animal instanceof EntityWolf &&
							original.getItem() instanceof ItemFood &&
							animal.getHealth() < animal.getMaxHealth())
						animal.heal(((ItemFood) original.getItem()).getHealAmount(original));
					else
						animal.setInLove(null);
				}
			}
		}
	}
	
	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "betterwithmods", "easybreeding", "animania" };
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
