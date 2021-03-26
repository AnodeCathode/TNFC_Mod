package com.aged_drinks;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;


@Mod(modid = TFCAgedDrinks.MODID, name = TFCAgedDrinks.NAME, version = TFCAgedDrinks.VERSION)
public class TFCAgedDrinks
{
    public static final String MODID = "aged_drinks";
    public static final String NAME = "TNFC Aged Drinks";
    public static final String VERSION = "12.2.65";

    @Mod.Instance(MODID)
    public static TFCAgedDrinks INSTANCE;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }

    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {

        @SubscribeEvent
        public static void onRegisterBarrelRecipeEvent(RegistryEvent.Register<BarrelRecipe> event) {
            AgedRegistry.registerAgedDrinks(event);
        }
    }
    
}
