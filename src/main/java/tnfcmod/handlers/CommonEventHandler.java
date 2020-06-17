package tnfcmod.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import tnfcmod.capabilities.PullProvider;
import tnfcmod.objects.entities.ai.EntityAIPullCart;

import static tnfcmod.tnfcmod.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CommonEventHandler
{
    @SubscribeEvent
    public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        // null check because of a compability issue with MrCrayfish's Furniture Mod and probably others
        // since this event is being fired even when an entity is initialized in the main menu
        if (event.getObject().world != null && !event.getObject().world.isRemote)
        {
            event.addCapability(new ResourceLocation(MODID), new PullProvider());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityLiving)
        {
            ((EntityLiving) event.getEntity()).tasks.addTask(2, new EntityAIPullCart((EntityLiving) event.getEntity()));
        }
    }
}