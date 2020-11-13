package com.lumintorious.ambiental;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.lumintorious.ambiental.capability.TemperatureCapability;
import com.lumintorious.ambiental.capability.TemperatureSystem;

@Mod.EventBusSubscriber(modid = TFCAmbiental.MODID)
public class PlayerTemperatureHandler
{

    @SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getObject();

            ResourceLocation loc = new ResourceLocation(TFCAmbiental.MODID, "temperature");

            //Each player should have their own instance for each stat, as associated values may vary
            if (!event.getCapabilities().containsKey(loc))
                event.addCapability(loc, new TemperatureCapability(player));
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event)
    {
        if (event.getEntity().world.isRemote)
        {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.isCreative())
        {
            return;
        }
        TemperatureSystem temp = TemperatureSystem.getTemperatureFor(player);
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
        if (stack != null)
        {
            if (stack.getItem().getRegistryName().toString().equals("tfc:wand"))
            {
                temp.say(temp);
            }
        }
        temp.update();
    }
}
