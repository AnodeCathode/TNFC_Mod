package tnfcmod.Recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

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
import static net.dries007.tfc.util.skills.SmithingSkill.Type.GENERAL;
import static tnfcmod.tnfcmod.MODID;

public class TFCRecipes
{

    public static void registerKnapping(RegistryEvent.Register<KnappingRecipe> event) {
        event.getRegistry().registerAll(
            new KnappingRecipeSimple(KnappingType.LEATHER, true, new ItemStack(TNFCItems.backpackpiece),
                "XX XX", " XXX ", "XXXXX", " XXX ", "XX XX").setRegistryName("backpackpiece")
       );
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
}
