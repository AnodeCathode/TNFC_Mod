package tnfcmod.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.util.calendar.ICalendar;
import tnfcmod.objects.items.TNFCItems;
import tnfcmod.qfc.features.Crabs;
import tnfcmod.qfc.features.Frogs;

import static net.dries007.tfc.api.types.Metal.ItemType.SHEET;
import static net.dries007.tfc.objects.fluids.FluidsTFC.LIMEWATER;
import static net.dries007.tfc.util.forge.ForgeRule.*;
import static net.dries007.tfc.util.skills.SmithingSkill.Type.GENERAL;
import static tnfcmod.tnfcmod.MODID;
import static tnfcmod.util.VanillaRecipeMaker.addShapelessDmgOreRecipe;

public class TFCRecipes
{

    public static void registerKnapping(RegistryEvent.Register<KnappingRecipe> event) {
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(TNFCItems.backpackpiece),
                "XX XX", " XXX ", "XXXXX", " XXX ", "XX XX").setRegistryName("backpackpiece")
            //new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(TNFCItems.leather_tunic), "X X X", "XXXXX", "XXXXX", "XXXXX", "XXXXX").setRegistryName("leather_tunic")
       );
   }
    public static void registerBarrel(RegistryEvent.Register<BarrelRecipe> event) {
        event.getRegistry().registerAll(
            new BarrelRecipe(IIngredient.of(LIMEWATER.get(), 5000), IIngredient.of(new ItemStack(Items.LEATHER_CHESTPLATE,1)), null, new ItemStack(TNFCItems.leather_tunic, 1), 8 * ICalendar.TICKS_IN_HOUR).setRegistryName("leathertunic")
            );
    }


    public static void registerHeatRecipes(RegistryEvent.Register<HeatRecipe> event)
    {
        IForgeRegistry<HeatRecipe> r = event.getRegistry();
        r.register(new HeatRecipeSimple(IIngredient.of(Crabs.crabLeg), new ItemStack(Crabs.cookedCrabLeg), 200, 480).setRegistryName("cooked_crab_leg"));
        r.register(new HeatRecipeSimple(IIngredient.of(Frogs.frogLeg), new ItemStack(Frogs.cookedFrogLeg), 200, 480).setRegistryName("cooked_frog_leg"));
    }

    public static void registerAnvil(RegistryEvent.Register<AnvilRecipe> event) {
        IForgeRegistry<AnvilRecipe> r = event.getRegistry();

        r.register(new AnvilRecipe(new ResourceLocation(MODID, "backpackframe"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, SHEET))),
            new ItemStack(TNFCItems.backpackframe), Metal.WROUGHT_IRON.getTier(), GENERAL, BEND_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_axe"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_axe), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, DRAW_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_chisel"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_chisel), Metal.STEEL.getTier(), GENERAL, BEND_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_hammer"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_hammer), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_hoe"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_hoe), Metal.STEEL.getTier(), GENERAL, PUNCH_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_javelin"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_javelin), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_knife"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_knife), Metal.STEEL.getTier(), GENERAL, BEND_LAST, DRAW_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_lamp"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_lamp), Metal.STEEL.getTier(), GENERAL, PUNCH_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_mace"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_mace), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_pick"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_pick), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_propick"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_propick), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, DRAW_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_saw), Metal.STEEL.getTier(), GENERAL, BEND_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_scythe"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_scythe), Metal.STEEL.getTier(), GENERAL, PUNCH_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_shears"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_shears), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, DRAW_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_shovel"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_shovel), Metal.STEEL.getTier(), GENERAL, BEND_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_sword"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_sword), Metal.STEEL.getTier(), GENERAL, PUNCH_LAST, SHRINK_SECOND_LAST, BEND_THIRD_LAST));
        r.register(new AnvilRecipe(new ResourceLocation(MODID, "mold_tuyere"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, SHEET))),
            new ItemStack(TNFCItems.mold_tuyere), Metal.STEEL.getTier(), GENERAL, UPSET_ANY, DRAW_SECOND_LAST, BEND_THIRD_LAST));

    }

    public static void registerOrePileRecipes(){


        //We're going to steal the 'scrap' items for OrePiles.
        for (Ore ore : TFCRegistries.ORES.getValuesCollection()){

            if (ore.isGraded())
            {
                Metal metal = ore.getMetal();
                if (Metal.ItemType.SCRAP.hasType(metal))
                {

                    //Rich to poor
                    addShapelessDmgOreRecipe(ItemOreTFC.get(ore, Ore.Grade.POOR, 3), 1, ItemOreTFC.get(ore, Ore.Grade.RICH, 1), "craftingToolMediumHammer");
                    //Poor to Pile
                    addShapelessDmgOreRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP),1),1,ItemOreTFC.get(ore, Ore.Grade.POOR, 1),ItemOreTFC.get(ore, Ore.Grade.POOR, 1),ItemOreTFC.get(ore, Ore.Grade.POOR, 1),ItemOreTFC.get(ore, Ore.Grade.POOR, 1), "craftingToolMediumHammer");
                    //Normal to Pile
                    addShapelessDmgOreRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP),1), 1, ItemOreTFC.get(ore, Ore.Grade.NORMAL, 1), ItemOreTFC.get(ore, Ore.Grade.NORMAL, 1), "craftingToolMediumHammer");
                    //Rich to Pile
                    addShapelessDmgOreRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP),3), 1, ItemOreTFC.get(ore, Ore.Grade.RICH, 1), ItemOreTFC.get(ore, Ore.Grade.RICH, 1), ItemOreTFC.get(ore, Ore.Grade.RICH, 1),ItemOreTFC.get(ore, Ore.Grade.RICH, 1), "craftingToolMediumHammer");
                    //Poor + Normal to Pile
                    addShapelessDmgOreRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP),1), 1,ItemOreTFC.get(ore, Ore.Grade.NORMAL, 1),ItemOreTFC.get(ore, Ore.Grade.POOR, 1), ItemOreTFC.get(ore, Ore.Grade.POOR, 1), "craftingToolMediumHammer");
                    //Poor + Rich to Pile
                    addShapelessDmgOreRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP),1),1,  ItemOreTFC.get(ore, Ore.Grade.RICH, 1),ItemOreTFC.get(ore, Ore.Grade.POOR, 1), "craftingToolMediumHammer");

                }
            }
            if (ItemSmallOre.get(ore) != null){
                //Small to normal
                addShapelessDmgOreRecipe(ItemOreTFC.get(ore, Ore.Grade.NORMAL, 1),1, ItemSmallOre.get(ore),  ItemSmallOre.get(ore),  ItemSmallOre.get(ore),  ItemSmallOre.get(ore),  ItemSmallOre.get(ore),  "craftingToolMediumHammer");
            }
        }
    }


}
