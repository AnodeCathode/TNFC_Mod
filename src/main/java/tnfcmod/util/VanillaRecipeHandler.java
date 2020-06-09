package tnfcmod.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.GameData;

import net.dries007.tfc.objects.recipes.ShapelessDamageRecipe;

public class VanillaRecipeHandler
{
    /*
    * Taken from https://github.com/TechReborn/RebornCore as the data-centric view can bite my shiny metal ass.
    * Copyright (c) 2018 modmuss50 and Gigabit101
     *
    /**
     * Adds a shaped recipe that supports string input paramers corresponding to an oredict entry, can also be used for recipes without ore dict ingredients
     *
     * @param output The stack that should be produced
     * @param params Input ingredients
     */
    public static void addShapedOreRecipe(ItemStack output, Object... params) {
        ResourceLocation location = getNameForRecipe(output);
        ShapedOreRecipe recipe = new ShapedOreRecipe(location, output, params);
        recipe.setRegistryName(location);
        GameData.register_impl(recipe);
    }

    /**
     * Adds a basic shaped recipe
     *
     * @param output The stack that should be produced
     * @param params Input ingredients
     */
    public static void addShapedRecipe(ItemStack output, Object... params) {
        ResourceLocation location = getNameForRecipe(output);
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(params);
        ShapedRecipes recipe = new ShapedRecipes(output.getItem().getRegistryName().toString(), primer.width, primer.height, primer.input, output);
        recipe.setRegistryName(location);
        GameData.register_impl(recipe);
    }

    /**
     * Adds a shapeless ore recipe with dmg
     *
     * @param output The stack that should be produced
     * @param input Input ingredients
     */
    public static void addShapelessDmgOreRecipe(ItemStack output, int dmg, Object... input) {
        ResourceLocation location = getNameForRecipe(output);
        NonNullList<Ingredient> ingredients = buildInput(input);
        ShapelessDamageRecipe recipe = new ShapelessDamageRecipe(location, ingredients, output,dmg);
        recipe.setRegistryName(location);
        GameData.register_impl(recipe);
    }

    /**
     * Adds a shapeless ore recipe
     *
     * @param output The stack that should be produced
     * @param input Input ingredients
     */
    public static void addShapelessOreRecipe(ItemStack output, Object... input) {
        ResourceLocation location = getNameForRecipe(output);
        ShapelessOreRecipe recipe = new ShapelessOreRecipe(location, output, input);
        recipe.setRegistryName(location);
        GameData.register_impl(recipe);
    }

    /**
     * Adds a basic shapeless recipe
     *
     * @param output The stack that should be produced
     * @param input Input ingredients
     */
    public static void addShapelessRecipe(ItemStack output, Object... input) {
        ResourceLocation location = getNameForRecipe(output);
        ShapelessRecipes recipe = new ShapelessRecipes(location.getNamespace(), output, buildInput(input));
        recipe.setRegistryName(location);
        GameData.register_impl(recipe);
    }

    /**
     * Generates a unique name based of the active mod, and the ItemStack that the recipe outputs
     *
     * @param output an ItemStack, usually the one the the recipe produces
     * @return a unique ResourceLocation based off the item item
     */
    public static ResourceLocation getNameForRecipe(ItemStack output) {
        ModContainer activeContainer = Loader.instance().activeModContainer();
        ResourceLocation baseLoc = new ResourceLocation(activeContainer.getModId(), output.getItem().getRegistryName().getPath());
        ResourceLocation recipeLoc = baseLoc;
        int index = 0;
        while (CraftingManager.REGISTRY.containsKey(recipeLoc)) {
            index++;
            recipeLoc = new ResourceLocation(activeContainer.getModId(), baseLoc.getPath() + "_" + index);
        }
        return recipeLoc;
    }

    /**
     * Converts an object array into a NonNullList of Ingredients
     */
    private static NonNullList<Ingredient> buildInput(Object[] input) {
        NonNullList<Ingredient> list = NonNullList.create();
        for (Object obj : input) {
            if (obj instanceof Ingredient) {
                list.add((Ingredient) obj);
            } else {
                Ingredient ingredient = CraftingHelper.getIngredient(obj);
                if (ingredient == null) {
                    ingredient = Ingredient.EMPTY;
                }
                list.add(ingredient);
            }
        }
        return list;
    }
}
