package tnfcmod.Recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import tnfcmod.objects.items.TNFCItems;

import static net.dries007.tfc.api.types.Metal.ItemType.DUST;


public class IERecipes
{
    public static void registerMetalPressRecipes()
    {


        //Mold recipes
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_blank, 1), "sheetDoubleSteel", new ItemStack(IEContent.blockStorage, 1, 8), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_doubleingot, 1), "ingotDoubleSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_sheet, 1), "sheetSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_bucket, 1), "bucketRedSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_bucket, 1), "bucketBlueSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        Ingredient ingredientVanillaBucket = Ingredient.fromStacks(new ItemStack(Items.BUCKET));
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_bucket, 1), ingredientVanillaBucket, new ItemStack(TNFCItems.mold_blank, 1), 2400);


        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            // are there non-tool metals that we want sheets/doubles from?
            if (metal.isToolMetal() | metal.toString() == "lead")
            {
                ItemStack outputDoubleIngot = new ItemStack(ItemMetal.get(metal, Metal.ItemType.DOUBLE_INGOT), 1);
                ItemStack outputSheet = new ItemStack(ItemMetal.get(metal, Metal.ItemType.SHEET), 1);
                ItemStack outputDoubleSheet = new ItemStack(ItemMetal.get(metal, Metal.ItemType.DOUBLE_SHEET), 1);

                Ingredient ingredientIngot = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.INGOT)));
                Ingredient ingredientDoubleIngot = Ingredient.fromStacks(outputDoubleIngot);
                Ingredient ingredientSheet = Ingredient.fromStacks(outputSheet);
                Ingredient ingredientDoubleSheet = Ingredient.fromStacks(outputDoubleSheet);
                Ingredient ingredientKnives = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.KNIFE_BLADE)));

                //Sheets and doubles
                MetalPressRecipe.addRecipe(outputDoubleIngot, ingredientIngot, new ItemStack(TNFCItems.mold_doubleingot), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(outputSheet, ingredientIngot, new ItemStack(TNFCItems.mold_sheet), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(outputSheet, ingredientDoubleIngot, new ItemStack(TNFCItems.mold_sheet), 2400);
                MetalPressRecipe.addRecipe(outputDoubleSheet, ingredientSheet, new ItemStack(TNFCItems.mold_sheet), 2400).setInputSize(2);

                //Tools

                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_axe), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_chisel), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_hammer), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_hoe), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.JAVELIN_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_javelin), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.KNIFE_BLADE), 1), ingredientIngot, new ItemStack(TNFCItems.mold_knife), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.LAMP), 1), ingredientIngot, new ItemStack(TNFCItems.mold_lamp), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.MACE_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_mace), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.PICK_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_pick), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.PROPICK_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_propick), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW_BLADE), 1), ingredientIngot, new ItemStack(TNFCItems.mold_saw), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCYTHE_BLADE), 1), ingredientIngot, new ItemStack(TNFCItems.mold_scythe), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SHEARS), 1), ingredientKnives, new ItemStack(TNFCItems.mold_shears), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SHOVEL_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_shovel), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SWORD_BLADE), 1), ingredientDoubleIngot, new ItemStack(TNFCItems.mold_sword), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.TUYERE), 1), ingredientDoubleSheet, new ItemStack(TNFCItems.mold_tuyere), 2400).setInputSize(2);
                if (metal == Metal.RED_STEEL | metal == Metal.BLUE_STEEL)
                {
                    MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.BUCKET), 1), ingredientSheet, new ItemStack(TNFCItems.mold_bucket), 2400);
                }

            }

        }
    }
    public static void registerCrusherRecipes(){
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {

            if (DUST.hasType(metal)){
               Ingredient ingredientIngot = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.INGOT)));
               CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST),1), ingredientIngot, 4000);
            }
            if (Metal.ItemType.SCRAP.hasType(metal) && DUST.hasType(metal)){
               Ingredient ingredientScrap = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP)));
               CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST),1), ingredientScrap, 4000);
               //Do we want to be able to crush tools/armour/etc to scrap?

            }
        }
        for (Ore ore : TFCRegistries.ORES.getValuesCollection())
        {
            if (ore.canMelt())
            {
                Metal metal = ore.getMetal();
                if (Metal.ItemType.DUST.hasType(metal))
                {
                    Ingredient ingredientOre = Ingredient.fromStacks(new ItemStack(ItemOreTFC.get(ore)));
                    CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST), 1), ingredientOre, 4000);
                }
            }
        }

    }

}
