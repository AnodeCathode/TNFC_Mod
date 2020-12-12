package tnfcmod.qfc.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.dries007.tfc.api.recipes.heat.HeatRecipeSimple;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.agriculture.Food;
import tnfcmod.qfc.features.Crabs;
import tnfcmod.qfc.features.Frogs;

public class qfcRecipes
{
    @SubscribeEvent
    public static void onRegisterHeatRecipeEvent(RegistryEvent.Register<HeatRecipe> event)
    {
        IForgeRegistry<HeatRecipe> r = event.getRegistry();
        r.register(new HeatRecipeSimple(IIngredient.of(Crabs.crabLeg), new ItemStack(Crabs.cookedCrabLeg), 200, 480).setRegistryName("cooked_crab_leg"));
        r.register(new HeatRecipeSimple(IIngredient.of(Frogs.frogLeg), new ItemStack(Frogs.cookedFrogLeg), 200, 480).setRegistryName("cooked_frog_leg"));
    }

}
