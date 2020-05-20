package tnfcmod.Recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipeSimple;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import tnfcmod.objects.items.TNFCItems;

import static net.dries007.tfc.api.types.Metal.ItemType.SHEET;
import static net.dries007.tfc.util.forge.ForgeRule.*;
import static net.dries007.tfc.util.forge.ForgeRule.BEND_SECOND_LAST;
import static net.dries007.tfc.util.forge.ForgeRule.BEND_THIRD_LAST;
import static net.dries007.tfc.util.forge.ForgeRule.PUNCH_LAST;
import static net.dries007.tfc.util.skills.SmithingSkill.Type.GENERAL;
import static tnfcmod.tnfcmod.MODID;

public class TNFCRecipes
{

    public static void registerKnapping(RegistryEvent.Register<KnappingRecipe> event) {
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(TNFCItems.backpackpiece),
                "XX XX", " XXX ", "XXXXX", " XXX ", "XX XX").setRegistryName("backpackpiece")
       );
   }


    public static void registerAnvil(RegistryEvent.Register<AnvilRecipe> event) {
        event.getRegistry().registerAll(
            new AnvilRecipe(new ResourceLocation(MODID,"backpackframe"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON,SHEET))),
                new ItemStack(TNFCItems.backpackframe), Metal.WROUGHT_IRON.getTier(), GENERAL, UPSET_ANY, SHRINK_SECOND_LAST, BEND_THIRD_LAST)
       );
    }
}
