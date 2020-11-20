package com.lumintorious.ambiental;

import com.lumintorious.ambiental.capability.TemperatureCapability;
import com.lumintorious.ambiental.effects.TempEffect;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.lang.reflect.Field;

public class AmbientalHandler {
	
	// Ignore this code!
	@SubscribeEvent
	public void onInteract(EntityInteractSpecific event) {
		
		Entity entity = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();
		if(entity instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager)entity;
			long time = villager.world.getWorldTime();
			try {
				MerchantRecipeList list = new MerchantRecipeList();
				for(Field f : EntityVillager.class.getDeclaredFields()) {
					f.setAccessible(true);
					if(f.get(villager) instanceof MerchantRecipeList) {
						list = (MerchantRecipeList)f.get(villager);
					
						MerchantRecipeList list2 = new MerchantRecipeList();
						
						for(MerchantRecipe recipe: list) {
							ItemStack itemToBuy = recipe.getItemToBuy().copy();
							ItemStack itemToBuy2 = recipe.getSecondItemToBuy().copy();
							ItemStack itemToSell = recipe.getItemToSell().copy();
							if(itemToSell.hasCapability(CapabilityFood.CAPABILITY, null)) {
								IFood cap = (IFood)itemToSell.getCapability(CapabilityFood.CAPABILITY, null);
								cap.setCreationDate(time);
							}
							list2.add(new MerchantRecipe(itemToBuy, itemToBuy2, itemToSell));
						}
						f.set(villager, list2);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void onPlayerDeath(LivingDeathEvent event) {
		if(event.getEntityLiving().world.isRemote) {
			return;
		}
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if(player.hasCapability(TemperatureCapability.CAPABILITY, null)) {
			TemperatureCapability cap = (TemperatureCapability)player.getCapability(TemperatureCapability.CAPABILITY, null);
			cap.bodyTemperature = TemperatureCapability.AVERAGE;
		}
	}
	
	@SubscribeEvent
	public void onPlayerSpawn(LivingSpawnEvent event) {
		if(event.getEntityLiving().world.isRemote) {
			return;
		}
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		player.sendMessage(new TextComponentString("respawned"));
	}
	
//	@SubscribeEvent
//    public void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event)
//    {
//		if (event.getObject() instanceof World)
//        {
//            ResourceLocation loc = new ResourceLocation(TFCAmbiental.MODID, "time_extension");
//            World world = (World)event.getObject();
//            if (!event.getCapabilities().containsKey(loc))
//                event.addCapability(loc, new TimeExtensionCapability(world));
//        }
//    }
	
	@SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getObject();

                ResourceLocation loc = new ResourceLocation(TFCAmbiental.MODID, "temperature");

                //Each player should have their own instance for each stat, as associated values may vary
                if (!event.getCapabilities().containsKey(loc))
                    event.addCapability(loc, new TemperatureCapability(player));
        }
    }

	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent  event) {
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if(player.isCreative()) {
			return;
		}
		TemperatureCapability temp = (TemperatureCapability)player.getCapability(TemperatureCapability.CAPABILITY, null);
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if(stack != null && !player.world.isRemote) {
			if(stack.getItem().getRegistryName().toString().equals("tfc:wand")) {
				temp.say(temp);
			}
		}
		temp.update();
	}

	@SubscribeEvent
	public void registerPotions(RegistryEvent.Register<Potion> event)
	{
		event.getRegistry().register(TempEffect.WARM);
		event.getRegistry().register(TempEffect.COOL);
	}
}
