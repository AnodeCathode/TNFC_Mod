package vazkii.quark.tweaks.feature;

import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.ai.EntityAINuzzle;
import vazkii.quark.tweaks.ai.EntityAIWantLove;
import vazkii.quark.world.entity.EntityFoxhound;

public class PatTheDogs extends Feature {

	public static int dogsWantLove;

	@Override
	public void setupConfig() {
		dogsWantLove = loadPropInt("Dog loneliness", "How many ticks it takes for a dog to want affection after being pet/tamed; leave -1 to disable", -1);
	}

	@SubscribeEvent
	public void onWolfAppear(EntityJoinWorldEvent event) {
		if (dogsWantLove > 0 && event.getEntity() instanceof EntityWolf) {
			EntityWolf wolf = (EntityWolf) event.getEntity();
			boolean alreadySetUp = false;
			for (EntityAITasks.EntityAITaskEntry entry : wolf.tasks.taskEntries) {
				if (entry.action instanceof EntityAIWantLove) {
					alreadySetUp = true;
					break;
				}
			}

			if (!alreadySetUp) {
				wolf.tasks.addTask(4, new EntityAINuzzle(wolf, 0.5F, 16, 2, SoundEvents.ENTITY_WOLF_WHINE));
				wolf.tasks.addTask(5, new EntityAIWantLove(wolf, 0.2F));
			}
		}
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() instanceof EntityWolf) {
			EntityWolf wolf = (EntityWolf) event.getTarget();
			EntityPlayer player = event.getEntityPlayer();
			
			if(player.isSneaking() && player.getHeldItemMainhand().isEmpty() && wolf.isTamed()) {
				if(event.getHand() == EnumHand.MAIN_HAND) {
					if(player.world instanceof WorldServer) {
						((WorldServer) player.world).spawnParticle(EnumParticleTypes.HEART, wolf.posX, wolf.posY + 0.5, wolf.posZ, 1, 0, 0, 0, 0.1);
						wolf.playSound(SoundEvents.ENTITY_WOLF_WHINE, 1F, 0.5F + (float) Math.random() * 0.5F);
					} else player.swingArm(EnumHand.MAIN_HAND);

					EntityAIWantLove.setPetTime(wolf);

					if (wolf instanceof EntityFoxhound && !player.isInWater() && !player.isImmuneToFire() && !player.isCreative())
						player.setFire(5);
				}
				
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onTame(AnimalTameEvent event) {
		if(event.getAnimal() instanceof EntityWolf) {
			EntityWolf wolf = (EntityWolf) event.getAnimal();
			EntityAIWantLove.setPetTime(wolf);
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
