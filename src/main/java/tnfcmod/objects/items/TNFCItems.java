package tnfcmod.objects.items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.objects.CreativeTabsTFC.CT_MISC;

public class TNFCItems
{
    public static ItemPlowCartTFC PLOWCARTTNFC = new ItemPlowCartTFC("plowcarttfc");
    public static ItemBackpackPiece backpackpiece = new ItemBackpackPiece("backpackpiece").setCreativeTab(CT_MISC);
    public static ItemBackpackFrame backpackframe = new ItemBackpackFrame("backpackframe").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_axe = new IEMetalPressMold("mold_axe").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_blank = new IEMetalPressMold("mold_blank").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_block = new IEMetalPressMold("mold_block").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_bucket = new IEMetalPressMold("mold_bucket").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_chisel = new IEMetalPressMold("mold_chisel").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_doubleingot = new IEMetalPressMold("mold_doubleingot").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_hammer = new IEMetalPressMold("mold_hammer").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_hoe = new IEMetalPressMold("mold_hoe").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_hopper = new IEMetalPressMold("mold_hopper").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_javelin = new IEMetalPressMold("mold_javelin").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_knife = new IEMetalPressMold("mold_knife").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_lamp = new IEMetalPressMold("mold_lamp").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_mace = new IEMetalPressMold("mold_mace").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_pick = new IEMetalPressMold("mold_pick").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_propick = new IEMetalPressMold("mold_propick").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_saw = new IEMetalPressMold("mold_saw").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_scythe = new IEMetalPressMold("mold_scythe").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_shears = new IEMetalPressMold("mold_shears").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_sheet = new IEMetalPressMold("mold_sheet").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_shovel = new IEMetalPressMold("mold_shovel").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_sword = new IEMetalPressMold("mold_sword").setCreativeTab(CT_MISC);
    public static IEMetalPressMold mold_tuyere = new IEMetalPressMold("mold_tuyere").setCreativeTab(CT_MISC);

    public static CopperFishHook copper_fishhook = new CopperFishHook("copper_fishhook");

    // You moved the headstones but left the textures? What?!?
    public static ItemWeakSteelDust weak_steel_dust = new ItemWeakSteelDust("weak_steel_dust").setCreativeTab(CT_MISC);
    public static ItemWeakSteelDust weak_red_steel_dust = new ItemWeakSteelDust("weak_red_steel_dust").setCreativeTab(CT_MISC);
    public static ItemWeakSteelDust weak_blue_steel_dust = new ItemWeakSteelDust("weak_blue_steel_dust").setCreativeTab(CT_MISC);

    //Add the high carbon and amalgam stuff too. Not sure we're using the amalgam yet
    public static ItemHighCarbonDust hc_black_steel_dust = new ItemHighCarbonDust("hc_black_steel_dust").setCreativeTab(CT_MISC);
    public static ItemHighCarbonDust hc_red_steel_dust = new ItemHighCarbonDust("hc_red_steel_dust").setCreativeTab(CT_MISC);
    public static ItemHighCarbonDust hc_blue_steel_dust = new ItemHighCarbonDust("hc_blue_steel_dust").setCreativeTab(CT_MISC);

    public static ItemSteelAmalgam hc_black_steel_amalgam = new ItemSteelAmalgam("hc_black_steel_amalgam").setCreativeTab(CT_MISC);
    public static ItemSteelAmalgam hc_blue_steel_amalgam = new ItemSteelAmalgam("hc_blue_steel_amalgam").setCreativeTab(CT_MISC);
    public static ItemSteelAmalgam hc_red_steel_amalgam = new ItemSteelAmalgam("hc_red_steel_amalgam").setCreativeTab(CT_MISC);

     public static void register(IForgeRegistry<Item> registry) {

        registry.register(backpackpiece);
        registry.register(backpackframe);
        registry.register(mold_axe);
        registry.register(mold_blank);
        registry.register(mold_block);
        registry.register(mold_bucket);
        registry.register(mold_chisel);
        registry.register(mold_doubleingot);
        registry.register(mold_hammer);
        registry.register(mold_hoe);
        registry.register(mold_hopper);
        registry.register(mold_javelin);
        registry.register(mold_knife);
        registry.register(mold_lamp);
        registry.register(mold_mace);
        registry.register(mold_pick);
        registry.register(mold_propick);
        registry.register(mold_saw);
        registry.register(mold_scythe);
        registry.register(mold_shears);
        registry.register(mold_sheet);
        registry.register(mold_shovel);
        registry.register(mold_sword);
        registry.register(mold_tuyere);
        registry.register(weak_steel_dust);
        registry.register(weak_red_steel_dust);
        registry.register(weak_blue_steel_dust);
        registry.register(copper_fishhook);
        registry.register(PLOWCARTTNFC);
        registry.register(hc_black_steel_dust);
        registry.register(hc_red_steel_dust);
        registry.register(hc_blue_steel_dust);
        registry.register(hc_black_steel_amalgam);
        registry.register(hc_blue_steel_amalgam);
        registry.register(hc_red_steel_amalgam);
    }

    public static void registerModels(Event event)
    {
        backpackpiece.registerItemModel();
        backpackframe.registerItemModel();
        mold_axe.registerItemModel();
        mold_blank.registerItemModel();
        mold_block.registerItemModel();
        mold_bucket.registerItemModel();
        mold_chisel.registerItemModel();
        mold_doubleingot.registerItemModel();
        mold_hammer.registerItemModel();
        mold_hoe.registerItemModel();
        mold_hopper.registerItemModel();
        mold_javelin.registerItemModel();
        mold_knife.registerItemModel();
        mold_lamp.registerItemModel();
        mold_mace.registerItemModel();
        mold_pick.registerItemModel();
        mold_propick.registerItemModel();
        mold_saw.registerItemModel();
        mold_scythe.registerItemModel();
        mold_shears.registerItemModel();
        mold_sheet.registerItemModel();
        mold_shovel.registerItemModel();
        mold_sword.registerItemModel();
        mold_tuyere.registerItemModel();
        weak_steel_dust.registerItemModel();
        weak_red_steel_dust.registerItemModel();
        weak_blue_steel_dust.registerItemModel();
        copper_fishhook.registerItemModel();
        PLOWCARTTNFC.registerItemModel();
        hc_black_steel_dust.registerItemModel();
        hc_red_steel_dust.registerItemModel();
        hc_blue_steel_dust.registerItemModel();
        hc_black_steel_amalgam.registerItemModel();
        hc_blue_steel_amalgam.registerItemModel();
        hc_red_steel_amalgam.registerItemModel();


    }
}
