package tnfcmod.util;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
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

import tnfcmod.Recipes.TNFCRecipes;
import tnfcmod.objects.items.TNFCItems;
import tnfcmod.tnfcmod;

import static tnfcmod.Recipes.IERecipes.registerCrusherRecipes;
import static tnfcmod.Recipes.IERecipes.registerMetalPressRecipes;

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
//        //  Ores which *could* be melted directly if it's temperature is met
//        r.register(new Ore(STIBNITE, ANTIMONY, true));
//
//        // Ores which we add tools, armor and textures inside TFC realm, but can't be melted directly (processing by other mods required)
//        r.register(new Ore(NATIVE_ARDITE, ARDITE, false));
//        r.register(new Ore(NATIVE_OSMIUM, OSMIUM, false));
//        r.register(new Ore(BAUXITE, ALUMINIUM, false));
//        r.register(new Ore(WOLFRAMITE, TUNGSTEN, false));
//        r.register(new Ore(COBALTITE, COBALT, false));
//        r.register(new Ore(RUTILE, TITANIUM, false));
//
//        // Ores without metals registered inside TFC
//        r.register(new Ore(THORIANITE));
//        r.register(new Ore(CHROMITE));
//        r.register(new Ore(PYROLUSITE));
//        r.register(new Ore(MAGNESITE));
//        r.register(new Ore(BORON));
//        r.register(new Ore(SPODUMENE));
    }

    @SubscribeEvent
    public static void onRegisterAlloyRecipe(RegistryEvent.Register<AlloyRecipe> event)
    {
        IForgeRegistry<AlloyRecipe> r = event.getRegistry();
//        r.register(new AlloyRecipe.Builder(CONSTANTAN).add(COPPER, 0.4, 0.6).add(NICKEL, 0.4, 0.6).build());
//        r.register(new AlloyRecipe.Builder(ELECTRUM).add(GOLD, 0.4, 0.6).add(SILVER, 0.4, 0.6).build());
//        r.register(new AlloyRecipe.Builder(MITHRIL).add(COPPER, 0.88, 0.92).add(ANTIMONY, 0.08, 0.12).build());
//        r.register(new AlloyRecipe.Builder(INVAR).add(WROUGHT_IRON, 0.6, 0.7).add(NICKEL, 0.3, 0.4).build());
//        r.register(new AlloyRecipe.Builder(ALUMINIUM_BRASS).add(ALUMINIUM, 0.65, 0.85).add(COPPER, 0.15, 0.35).build());
//        r.register(new AlloyRecipe.Builder(MANYULLYN).add(COBALT, 0.4, 0.6).add(ARDITE, 0.4, 0.6).build());
//        r.register(new AlloyRecipe.Builder(TUNGSTEN_STEEL).add(TUNGSTEN, 0.02, 0.18).add(STEEL, 0.72, 0.98).build());
//        r.register(new AlloyRecipe.Builder(NICKEL_SILVER).add(COPPER, 0.50, 0.65).add(ZINC, 0.1, 0.3).add(NICKEL, 0.1, 0.3).build());
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void onRegisterBloomeryRecipeEvent(RegistryEvent.Register<BloomeryRecipe> event)
    {
        IForgeRegistry<BloomeryRecipe> registry = event.getRegistry();
//        if (ConfigTFCM.RECIPES.aluminium)
//        {
//            registry.register(new BloomeryRecipe(TFCRegistries.METALS.getValue(ALUMINIUM), FuelManager::isItemBloomeryFuel));
//        }
//        if (ConfigTFCM.RECIPES.ardite)
//        {
//            registry.register(new BloomeryRecipe(TFCRegistries.METALS.getValue(ARDITE), FuelManager::isItemBloomeryFuel));
//        }
//        if (ConfigTFCM.RECIPES.cobalt)
//        {
//            registry.register(new BloomeryRecipe(TFCRegistries.METALS.getValue(COBALT), FuelManager::isItemBloomeryFuel));
//        }
    }

    @SubscribeEvent
    public static void onRegisterBlastFurnaceRecipeEvent(RegistryEvent.Register<BlastFurnaceRecipe> event)
    {
        IForgeRegistry<BlastFurnaceRecipe> registry = event.getRegistry();
//        if (ConfigTFCM.RECIPES.osmium)
//        {
//            Metal osmium = TFCRegistries.METALS.getValue(OSMIUM);
//            if (osmium != null)
//            {
//                registry.register(new BlastFurnaceRecipe(osmium, osmium, IIngredient.of("dustFlux")));
//            }
//        }
//        if (ConfigTFCM.RECIPES.titanium)
//        {
//            Metal titanium = TFCRegistries.METALS.getValue(TITANIUM);
//            if (titanium != null)
//            {
//                registry.register(new BlastFurnaceRecipe(titanium, titanium, IIngredient.of("dustFlux")));
//            }
//        }
//        if (ConfigTFCM.RECIPES.tungsten)
//        {
//            Metal tungsten = TFCRegistries.METALS.getValue(TUNGSTEN);
//            if (tungsten != null)
//            {
//                registry.register(new BlastFurnaceRecipe(tungsten, tungsten, IIngredient.of("dustFlux")));
//            }
//        }
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event)
    {
        TNFCRecipes.registerAnvil(event);
    }
    /**
     * Listen for the register event for creating custom items
     */
    @SubscribeEvent
    public static void addItems(RegistryEvent.Register<Item> event)
    {
        TNFCItems.register(event.getRegistry());
        //oredic registries?
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
        TNFCRecipes.registerKnapping(event);
    }


    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        registerMetalPressRecipes();
        registerCrusherRecipes();
    }
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
//        ResourceLocation soundID = new ResourceLocation(MOD_ID, "item.flaskbreak");
//        event.getRegistry().register((new SoundEvent(soundID)).setRegistryName(soundID));
    }


}
