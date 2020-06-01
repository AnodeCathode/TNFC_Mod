package tnfcmod.util;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.api.recipes.BloomeryRecipe;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;

import tnfcmod.Recipes.OreDicEntries;
import tnfcmod.Recipes.TFCRecipes;
import tnfcmod.objects.items.TNFCItems;
import tnfcmod.tnfcmod;

import static tnfcmod.Recipes.IERecipes.*;
import static tnfcmod.Recipes.VanillaRecipes.removeVanillaRecipes;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = tnfcmod.MODID)
public final class RegistryHandler
{


    @SubscribeEvent
    public static void onPreRegisterMetal(TFCRegistryEvent.RegisterPreBlock<Metal> event)
    {
        IForgeRegistry<Metal> r = event.getRegistry();
    }


    @SubscribeEvent
    public static void onPreRegisterOre(TFCRegistryEvent.RegisterPreBlock<Ore> event)
    {
        IForgeRegistry<Ore> r = event.getRegistry();
    }

    @SubscribeEvent
    public static void onRegisterAlloyRecipe(RegistryEvent.Register<AlloyRecipe> event)
    {
        IForgeRegistry<AlloyRecipe> r = event.getRegistry();
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onRegisterBloomeryRecipeEvent(RegistryEvent.Register<BloomeryRecipe> event)
    {
        IForgeRegistry<BloomeryRecipe> registry = event.getRegistry();
    }

    @SubscribeEvent
    public static void onRegisterBlastFurnaceRecipeEvent(RegistryEvent.Register<BlastFurnaceRecipe> event)
    {
        IForgeRegistry<BlastFurnaceRecipe> registry = event.getRegistry();
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event)
    {
        TFCRecipes.registerAnvil(event);
    }
    /**
     * Listen for the register event for creating custom items
     */
    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event)
    {
        TNFCItems.register(event.getRegistry());
        OreDicEntries.init();
    }

    /**
     * Listen for the register event for models
     */
    @SubscribeEvent
    public static void registerItems(ModelRegistryEvent event)
    {
        TNFCItems.registerModels();

    }

    /**
     * Register Knapping Recipe
     */
    @SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        TFCRecipes.registerKnapping(event);
    }


    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        removeVanillaRecipes(event);
        registerMetalPressRecipes();
        registerCrusherRecipes();
        registerGardenClocheRecipes();
        registerArcFurnaceRecipes();

    }
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
//        ResourceLocation soundID = new ResourceLocation(MOD_ID, "item.flaskbreak");
//        event.getRegistry().register((new SoundEvent(soundID)).setRegistryName(soundID));
    }

}
