package tnfcmod.handlers;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemPowder;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import tnfcmod.objects.materials.ArmorMaterialsTNFC;
import tnfcmod.objects.materials.ToolMaterialsTNFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.types.DefaultMetals.*;
import static tnfcmod.tnfcmod.MODID;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = MODID)
public final class RegisterOres
{
    //Metals
    public static final ResourceLocation ALUMINUM = new ResourceLocation(MOD_ID, "aluminum");
    public static final ResourceLocation ALUMINUM_BRASS = new ResourceLocation(MOD_ID, "aluminum_brass");
    public static final ResourceLocation ANTIMONY = new ResourceLocation(MOD_ID, "antimony");
    public static final ResourceLocation ARDITE = new ResourceLocation(MOD_ID, "ardite");
    public static final ResourceLocation COBALT = new ResourceLocation(MOD_ID, "cobalt");
    public static final ResourceLocation CONSTANTAN = new ResourceLocation(MOD_ID, "constantan");
    public static final ResourceLocation ELECTRUM = new ResourceLocation(MOD_ID, "electrum");
    public static final ResourceLocation INVAR = new ResourceLocation(MOD_ID, "invar");
    public static final ResourceLocation MANYULLYN = new ResourceLocation(MOD_ID, "manyullyn");
    public static final ResourceLocation OSMIUM = new ResourceLocation(MOD_ID, "osmium");
    public static final ResourceLocation TITANIUM = new ResourceLocation(MOD_ID, "titanium");
    public static final ResourceLocation TUNGSTEN = new ResourceLocation(MOD_ID, "tungsten");
    public static final ResourceLocation TUNGSTEN_STEEL = new ResourceLocation(MOD_ID, "tungsten_steel");
    public static final ResourceLocation NICKEL_SILVER = new ResourceLocation(MOD_ID, "nickel_silver"); // Copper + zinc + nickel
    public static final ResourceLocation RED_ALLOY = new ResourceLocation(MOD_ID, "red_alloy"); // Copper + redstone (although not obtainable with just TFC + metallum

    public static final ResourceLocation BORON = new ResourceLocation(MOD_ID, "boron");
    public static final ResourceLocation FERROBORON = new ResourceLocation(MOD_ID, "ferroboron"); // steel + boron
    public static final ResourceLocation THORIUM = new ResourceLocation(MOD_ID, "thorium");
    public static final ResourceLocation LITHIUM = new ResourceLocation(MOD_ID, "lithium");
    public static final ResourceLocation MANGANESE = new ResourceLocation(MOD_ID, "manganese");
    public static final ResourceLocation MAGNESIUM = new ResourceLocation(MOD_ID, "magnesium");
    public static final ResourceLocation BERYLLIUM = new ResourceLocation(MOD_ID, "beryllium");
    public static final ResourceLocation BERYLLIUM_COPPER = new ResourceLocation(MOD_ID, "beryllium_copper"); // Copper + beryllium + aluminium
    public static final ResourceLocation ZIRCONIUM = new ResourceLocation(MOD_ID, "zirconium");
    public static final ResourceLocation ZIRCALOY = new ResourceLocation(MOD_ID, "zircaloy"); // zirconium + tin
    public static final ResourceLocation HSLA_STEEL = new ResourceLocation(MOD_ID, "hsla_steel"); // steel + manganese
    public static final ResourceLocation MAGNESIUM_DIBORIDE = new ResourceLocation(MOD_ID, "magnesium_diboride"); // magnesium + steel
    public static final ResourceLocation TOUGH = new ResourceLocation(MOD_ID, "tough"); // Ferroboron + lithium
    public static final ResourceLocation URANIUM = new ResourceLocation(MOD_ID, "uranium");


    //Ores
    public static final ResourceLocation NATIVE_ARDITE = new ResourceLocation(MOD_ID, "native_ardite");
    public static final ResourceLocation NATIVE_OSMIUM = new ResourceLocation(MOD_ID, "native_osmium");
    public static final ResourceLocation BAUXITE = new ResourceLocation(MOD_ID, "bauxite"); // aluminum / titanium
    public static final ResourceLocation WOLFRAMITE = new ResourceLocation(MOD_ID, "wolframite"); // tungsten
    public static final ResourceLocation COBALTITE = new ResourceLocation(MOD_ID, "cobaltite"); // cobalt
    public static final ResourceLocation STIBNITE = new ResourceLocation(MOD_ID, "stibnite"); // antimony
    public static final ResourceLocation RUTILE = new ResourceLocation(MOD_ID, "rutile"); // titanium
    public static final ResourceLocation TETRABORATE = new ResourceLocation(MOD_ID, "tetraborate"); // boron
    public static final ResourceLocation SPODUMENE = new ResourceLocation(MOD_ID, "spodumene"); // lithium
    public static final ResourceLocation THORIANITE = new ResourceLocation(MOD_ID, "thorianite"); // thorium
    public static final ResourceLocation PYROLUSITE = new ResourceLocation(MOD_ID, "pyrolusite"); // manganese
    public static final ResourceLocation MAGNESITE = new ResourceLocation(MOD_ID, "magnesite"); //magnesium
    public static final ResourceLocation BERYL = new ResourceLocation(MOD_ID, "beryl"); //beryllium
    public static final ResourceLocation ZIRCON = new ResourceLocation(MOD_ID, "zircon"); // zirconium

    //Ore without TFC-M metals
    public static final ResourceLocation CHROMITE = new ResourceLocation(MOD_ID, "chromite"); // chrome
    public static final ResourceLocation VILLIAUMITE = new ResourceLocation(MOD_ID, "villiaumite");
    public static final ResourceLocation RHODOCHROSITE = new ResourceLocation(MOD_ID, "rhodochrosite");
    public static final ResourceLocation FLUORITE = new ResourceLocation(MOD_ID, "fluorite");
    public static final ResourceLocation CAROBBIITE = new ResourceLocation(MOD_ID, "carobbiite");
    public static final ResourceLocation ARSENIC = new ResourceLocation(MOD_ID, "arsenic");
  


    @SubscribeEvent
    public static void onPreRegisterMetal(TFCRegistryEvent.RegisterPreBlock<Metal> event)
    {
        IForgeRegistry<Metal> r = event.getRegistry();
        
        r.register(new Metal(ANTIMONY, Metal.Tier.TIER_II, true, 0.25f, 630, 0xFFE7E7F5, null, null));
        r.register(new Metal(LITHIUM, Metal.Tier.TIER_II, true, 0.25f, 630, 0xC9CBC3, null, null));
        r.register(new Metal(CONSTANTAN, Metal.Tier.TIER_III, true, 0.5f, 1200, 0xFFD28874, null, null));
        r.register(new Metal(ELECTRUM, Metal.Tier.TIER_III, true, 0.5f, 1200, 0xFFDFB950, null, null));
        r.register(new Metal(NICKEL_SILVER, Metal.Tier.TIER_III, true, 0.35f, 1450, 0xFFA4A4A5, ToolMaterialsTNFC.NICKEL_SILVER, ArmorMaterialsTNFC.NICKEL_SILVER));
        r.register(new Metal(RED_ALLOY, Metal.Tier.TIER_III, true, 0.35f, 1080, 0xFFDA6E6E, null, null));
        r.register(new Metal(MAGNESIUM, Metal.Tier.TIER_III, true, 0.30f, 650, 0xFF978195, null, null));
        r.register(new Metal(MANGANESE, Metal.Tier.TIER_III, true, 0.29f, 1250, 0xFF9397A8, null, null));
        r.register(new Metal(BORON, Metal.Tier.TIER_III, true, 0.3f, 630, 0xFF252525, ToolMaterialsTNFC.BORON, ArmorMaterialsTNFC.BORON));
        r.register(new Metal(MAGNESIUM_DIBORIDE, Metal.Tier.TIER_III, true, 0.35f, 2000, 0xFF46391E, null, null));
        r.register(new Metal(URANIUM, Metal.Tier.TIER_III, true, 0.3f, 3000, 0xFF3A6724, null, null));
        r.register(new Metal(ALUMINUM, Metal.Tier.TIER_IV, true, 0.3f, 3025, 0xFFD9FBFC, ToolMaterialsTNFC.ALUMINUM, ArmorMaterialsTNFC.ALUMINUM));
        r.register(new Metal(ALUMINUM_BRASS, Metal.Tier.TIER_IV, true, 0.3f, 3025, 0xFFDCDABE, null, null));
        r.register(new Metal(ARDITE, Metal.Tier.TIER_IV, true, 0.3f, 3025, 0xFF40444A, null, null));
        r.register(new Metal(COBALT, Metal.Tier.TIER_IV, true, 0.3f, 3025, 0xFF6CA6E5, ToolMaterialsTNFC.COBALT, ArmorMaterialsTNFC.COBALT));
        r.register(new Metal(INVAR, Metal.Tier.TIER_IV, true, 0.35f, 1450, 0xFF40444A, ToolMaterialsTNFC.INVAR, ArmorMaterialsTNFC.INVAR));
        r.register(new Metal(MANYULLYN, Metal.Tier.TIER_IV, true, 0.3f, 3025, 0xFF40444A, ToolMaterialsTNFC.MANYULLYN, ArmorMaterialsTNFC.MANYULLYN));
        r.register(new Metal(THORIUM, Metal.Tier.TIER_IV, true, 0.3f, 630, 0xFF3D4548, null, null));
        r.register(new Metal(BERYLLIUM, Metal.Tier.TIER_V, true, 0.35f, 1300, 0xFFE4EADA, null, null));
        r.register(new Metal(BERYLLIUM_COPPER, Metal.Tier.TIER_V, true, 0.35f, 1500, 0xFFEAAE90, ToolMaterialsTNFC.BERYLLIUM_COPPER, ArmorMaterialsTNFC.BERYLLIUM_COPPER));
        r.register(new Metal(FERROBORON, Metal.Tier.TIER_V, true, 0.3f, 3000, 0xFF4B4B4B, null, null));
        r.register(new Metal(HSLA_STEEL, Metal.Tier.TIER_V, true, 0.35f, 2000, 0xFF3F4180, null, null));
        r.register(new Metal(OSMIUM, Metal.Tier.TIER_V, true, 0.35f, 3025, 0xFFB8D8DE, ToolMaterialsTNFC.OSMIUM, ArmorMaterialsTNFC.OSMIUM));
        r.register(new Metal(TITANIUM, Metal.Tier.TIER_V, true, 0.3f, 3025, 0xFFC2C4CC, ToolMaterialsTNFC.TITANIUM, ArmorMaterialsTNFC.TITANIUM));
        r.register(new Metal(TUNGSTEN, Metal.Tier.TIER_V, true, 0.2f, 3400, 0xFF40444A, ToolMaterialsTNFC.TUNGSTEN, ArmorMaterialsTNFC.TUNGSTEN));
        r.register(new Metal(TUNGSTEN_STEEL, Metal.Tier.TIER_V, true, 0.2f, 3695, 0xFF565F6E, ToolMaterialsTNFC.TUNGSTEN_STEEL, ArmorMaterialsTNFC.TUNGSTEN_STEEL));
        r.register(new Metal(ZIRCONIUM, Metal.Tier.TIER_V, true, 0.35f, 1500, 0xFF747527, null, null));
        r.register(new Metal(ZIRCALOY, Metal.Tier.TIER_V, true, 0.35f, 1500, 0xFF43423A, ToolMaterialsTNFC.ZIRCALOY, ArmorMaterialsTNFC.ZIRCALOY));
        r.register(new Metal(TOUGH, Metal.Tier.TIER_V, true, 0.3f, 3000, 0xFF3F2B61, null, null));


    }


    @SubscribeEvent
    public static void onPreRegisterOre(TFCRegistryEvent.RegisterPreBlock<Ore> event)
    {
        IForgeRegistry<Ore> r = event.getRegistry();
        //  Ores which *could* be melted directly if it's temperature is met
        r.register(new Ore(STIBNITE, ANTIMONY, true));
        r.register(new Ore(SPODUMENE, LITHIUM, true));

        // Ores which we add tools, armor and textures inside TFC realm, but can't be melted directly (processing by other mods required)
        r.register(new Ore(NATIVE_ARDITE, ARDITE, false));
        r.register(new Ore(NATIVE_OSMIUM, OSMIUM, false));
        r.register(new Ore(BAUXITE, ALUMINUM, false));
        r.register(new Ore(WOLFRAMITE, TUNGSTEN, false));
        r.register(new Ore(COBALTITE, COBALT, false));
        r.register(new Ore(RUTILE, TITANIUM, false));
        r.register(new Ore(THORIANITE, THORIUM, false));
        r.register(new Ore(PYROLUSITE, MANGANESE, false));
        r.register(new Ore(MAGNESITE, MAGNESIUM, false));
        r.register(new Ore(ZIRCON, ZIRCONIUM, false));

        // Ores without metals registered inside TFC
        r.register(new Ore(CHROMITE));
        r.register(new Ore(VILLIAUMITE));
        r.register(new Ore(RHODOCHROSITE));
        r.register(new Ore(FLUORITE));
        r.register(new Ore(CAROBBIITE));
        r.register(new Ore(ARSENIC));
    }

    @SubscribeEvent
    public static void onRegisterAlloyRecipe(RegistryEvent.Register<AlloyRecipe> event)
    {
        IForgeRegistry<AlloyRecipe> r = event.getRegistry();
        r.registerAll(
            new AlloyRecipe.Builder(CONSTANTAN).add(COPPER, 0.4, 0.6).add(NICKEL, 0.4, 0.6).build(),
            new AlloyRecipe.Builder(ELECTRUM).add(GOLD, 0.4, 0.6).add(SILVER, 0.4, 0.6).build(),
            new AlloyRecipe.Builder(INVAR).add(WROUGHT_IRON, 0.6, 0.7).add(NICKEL, 0.3, 0.4).build(),
            new AlloyRecipe.Builder(ALUMINUM_BRASS).add(ALUMINUM, 0.65, 0.85).add(COPPER, 0.15, 0.35).build(),
            new AlloyRecipe.Builder(MANYULLYN).add(COBALT, 0.4, 0.6).add(ARDITE, 0.4, 0.6).build(),
            new AlloyRecipe.Builder(TUNGSTEN_STEEL).add(TUNGSTEN, 0.02, 0.18).add(STEEL, 0.72, 0.98).build(),
            new AlloyRecipe.Builder(NICKEL_SILVER).add(COPPER, 0.50, 0.65).add(ZINC, 0.1, 0.3).add(NICKEL, 0.1, 0.3).build(),
            new AlloyRecipe.Builder(FERROBORON).add(STEEL, 0.4, 0.6).add(BORON, 0.4, 0.6).build(),
            new AlloyRecipe.Builder(HSLA_STEEL).add(STEEL, 0.2, 0.4).add(MANGANESE, 0.4, 0.6).build(),
            new AlloyRecipe.Builder(MAGNESIUM_DIBORIDE).add(BORON, 0.4, 0.6).add(MAGNESIUM, 0.2, 0.4).build(),
            new AlloyRecipe.Builder(BERYLLIUM_COPPER).add(BERYLLIUM, 0.3, 0.6).add(COPPER, 0.3, 0.6).add(ALUMINUM, 0.1, 0.3).build(),
            new AlloyRecipe.Builder(ZIRCALOY).add(ZIRCONIUM, 0.72, 0.98).add(TIN, 0.2, 0.4).build(),
            new AlloyRecipe.Builder(TOUGH).add(FERROBORON, 0.4, 0.6).add(LITHIUM, 0.4, 0.6).build()
        );
    }


    @SubscribeEvent
    public static void onRegisterQuernRecipeEvent(RegistryEvent.Register<QuernRecipe> event)
    {
        IForgeRegistry<QuernRecipe> r = event.getRegistry();

        Metal uranium = TFCRegistries.METALS.getValue(URANIUM);
        Metal boron = TFCRegistries.METALS.getValue(BORON);
        if (uranium != null)
        {
            r.register(new QuernRecipe(IIngredient.of("gemPitchblende"), new ItemStack(ItemMetal.get(uranium, Metal.ItemType.DUST), 4)).setRegistryName("uranium_dust"));
        }
        if (boron != null)
        {
            IForgeRegistryModifiable<QuernRecipe> modRegistry = (IForgeRegistryModifiable<QuernRecipe>) TFCRegistries.QUERN;
            modRegistry.remove(new ResourceLocation(MOD_ID, "borax"));
            r.register(new QuernRecipe(IIngredient.of("gemBorax"), new ItemStack(ItemMetal.get(boron, Metal.ItemType.DUST), 4)).setRegistryName("boron_dust"));
        }

        r.register(new QuernRecipe(IIngredient.of("gemFluorite"), new ItemStack(ItemPowder.get(Powder.FLUX), 6)).setRegistryName("fluorite_flux"));
    }

}

