package tnfcmod.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import gigaherz.guidebook.client.BookRegistryEvent;
import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import tnfcmod.objects.blocks.TNFCBlocks;
import tnfcmod.objects.items.TNFCItems;
import tnfcmod.recipes.OreDicEntries;
import tnfcmod.recipes.TFCRecipes;
import tnfcmod.tnfcmod;
import tnfcmod.util.FireworksRecipes;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;
import static tnfcmod.recipes.BwMRecipes.registerBwMSawRecipes;
import static tnfcmod.recipes.IERecipes.*;
import static tnfcmod.recipes.LootTablesTNFC.modifyLootTableLoad;
import static tnfcmod.recipes.TFCRecipes.registerOrePileRecipes;
import static tnfcmod.recipes.VanillaRecipes.registerVanillaRecipes;
import static tnfcmod.recipes.VanillaRecipes.removeVanillaRecipes;
import static tnfcmod.tnfcmod.MODID;


@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = MODID)
public final class RegistryHandler
{

    @Optional.Method(modid = "gbook")
    @SubscribeEvent
    public static void registerBook(BookRegistryEvent event)
    {
        event.register(new ResourceLocation(MODID + ":xml/tnfcchanges.xml"));
    }

    /**
     * Listen for the register event for creating custom blocks
     */
    @SubscribeEvent
    public static void addBlocks(RegistryEvent.Register<Block> event)
    {
        TNFCBlocks.registerBlocks(event);

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
        TNFCItems.registerModels(event);

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
    public static void onRegisterHeatRecipeEvent(RegistryEvent.Register<HeatRecipe> event)
    {
        TFCRecipes.registerHeatRecipes(event);
    }

    /**
     * Register Loot Tables
     */
    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
           modifyLootTableLoad(event);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        tnfcmod.getLog().info("Registering TechnodeFirmaCraft recipes");
        removeVanillaRecipes(event);
        registerVanillaRecipes(event);
        registerMetalPressRecipes();
        registerCrusherRecipes();
        registerGardenClocheRecipes();
        registerArcFurnaceRecipes();
        registerOrePileRecipes();
        registerBwMSawRecipes();
        event.getRegistry().register(new FireworksRecipes().setRegistryName(MODID, "fireworks"));


    }
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
//        ResourceLocation soundID = new ResourceLocation(MOD_ID, "item.flaskbreak");
//        event.getRegistry().register((new SoundEvent(soundID)).setRegistryName(soundID));
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event)
    {
        TFCRecipes.registerAnvil(event);
    }

    @SubscribeEvent
    public static void onRegisterBarrelRecipeEvent(RegistryEvent.Register<BarrelRecipe> event)
    {
        TFCRecipes.registerBarrel(event);
    }



}
