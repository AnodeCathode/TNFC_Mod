package tnfcmod.objects.items;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.objects.fluids.properties.FluidWrapper;

import static net.dries007.tfc.objects.CreativeTabsTFC.CT_MISC;

public class TNFCItems
{
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





     public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
            backpackpiece,
            backpackframe,
            mold_axe,
            mold_blank,
            mold_block,
            mold_bucket,
            mold_chisel,
            mold_doubleingot,
            mold_hammer,
            mold_hoe,
            mold_hopper,
            mold_javelin,
            mold_knife,
            mold_lamp,
            mold_mace,
            mold_pick,
            mold_propick,
            mold_saw,
            mold_scythe,
            mold_shears,
            mold_sheet,
            mold_shovel,
            mold_sword,
            mold_tuyere


        );


    }

    public static void registerModels()
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
    }
}
