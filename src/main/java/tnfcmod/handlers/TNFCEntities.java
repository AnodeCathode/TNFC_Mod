package tnfcmod.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import tnfcmod.client.render.RenderPlowCartTFC;
import tnfcmod.objects.entities.EntityPlowCartTFC;

import static tnfcmod.tnfcmod.MODID;

public class TNFCEntities
{


    public TNFCEntities()
    {
    }

    public static void registerRenders()
    {

        RenderingRegistry.registerEntityRenderingHandler(EntityPlowCartTFC.class, RenderPlowCartTFC::new);

    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    @Mod.EventBusSubscriber(modid = MODID)
    public static class EntityRegistrationHandler
    {
        private static int id = 0;

        public EntityRegistrationHandler()
        {
        }

        @SubscribeEvent
        public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
        {
            event.getRegistry().registerAll(new EntityEntry[] {createEntry(EntityPlowCartTFC.class, "plowcarttfc", 80, 3, false)});
        }

        private static EntityEntry createEntry(Class<? extends Entity> entityClass, String name, int trackingRange, int updateFrequency, boolean sendVelocityUpdates)
        {
            ResourceLocation resourceLocation = new ResourceLocation(MODID + "." + name);
            return EntityEntryBuilder.create().entity(entityClass).id(resourceLocation, id++).name(resourceLocation.toString()).tracker(trackingRange, updateFrequency, sendVelocityUpdates).build();
        }

    }
}