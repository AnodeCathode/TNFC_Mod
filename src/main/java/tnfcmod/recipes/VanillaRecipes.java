package tnfcmod.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import tnfcmod.objects.items.TNFCItems;
import tnfcmod.util.RecipeUtils;

import static net.dries007.tfc.api.types.Metal.ItemType.NUGGET;
import static tnfcmod.util.VanillaRecipeMaker.addShapelessDmgOreRecipe;
import static tnfcmod.util.VanillaRecipeMaker.addShapelessOreRecipe;


public class VanillaRecipes
{
    public static void registerVanillaRecipes(RegistryEvent.Register<IRecipe> event){

        addShapelessDmgOreRecipe(new ItemStack(TNFCItems.copper_fishhook,1), 1,"oreCopperSmall", "hammer");
        addShapelessOreRecipe(new ItemStack(Items.CLAY_BALL, 4), Blocks.CLAY);


      //ADD Shapeless Hammer and Ingot recipes to make delicious metal nuggets.
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            //Basic ingot to nugget
            if (NUGGET.hasType(metal))
            {
                addShapelessDmgOreRecipe(new ItemStack(ItemMetal.get(metal, NUGGET), 9), 1,new ItemStack(ItemMetal.get(metal, Metal.ItemType.INGOT),1), "hammer");
            }
        }

    }

    public static void removeVanillaRecipes(RegistryEvent.Register<IRecipe> event)
   {
       IForgeRegistryModifiable<IRecipe> modRegistry = (IForgeRegistryModifiable<IRecipe>) event.getRegistry();
        //things we remove so we can add our own:
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "furnace");

       //Other recipes I need to remove here rather than in CT:
       RecipeUtils.removeRecipeByName(modRegistry,"betterwithmods", "raw_kebab");
      
       //Things added by TFC-NG that we're going to remove.

       //Ladders from planks. Ladders are made from sticks damnit
       RecipeUtils.removeRecipeByName(modRegistry,"tfc", "vanilla/ladder");
       //Things remove by TFC-NG but not added. TBD
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "coal_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_boat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_door");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_fence_gate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_planks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "acacia_wooden_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "andesite");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "anvil");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "beacon");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "beetroot_soup");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_boat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_door");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_fence_gate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_planks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "birch_wooden_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "black_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "black_bed_from_white_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "black_stained_glass");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "black_stained_hardened_clay");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "black_wool");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "blue_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "blue_bed_from_white_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "blue_stained_glass");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "blue_stained_hardened_clay");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "boat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "bone_meal_from_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "bone_meal_from_bone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "bookshelf");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "bread");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "brewing_stand");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "brick_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "brown_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cake");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "carrot_on_a_stick");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chainmail_boots");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chainmail_chestplate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chainmail_helmet");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chainmail_leggings");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chest");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chest_minecart");
       //Reenable as we can create quartz now
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chiseled_quartz_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chiseled_red_sandstone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chiseled_sandstone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "chiseled_stonebrick");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "coal");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "coarse_dirt");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cobblestone_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cobblestone_wall");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "comparator");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cookie");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "crafting_table");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cyan_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_boat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_door");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_fence_gate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_planks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_oak_wooden_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "dark_prismarine");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_axe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_boots");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_chestplate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_helmet");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_hoe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_leggings");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_pickaxe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_shovel");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diamond_sword");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "diorite");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "emerald");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "emerald_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "end_bricks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "end_crystal");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "end_rod");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "ender_eye");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "fence_gate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "fermented_spider_eye");
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "fireworks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "fishing_rod");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "flower_pot");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "furnace_minecart");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "glass_bottle");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "glowstone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "gold_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "gold_ingot_from_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "gold_ingot_from_nuggets");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "gold_nugget");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_apple");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_axe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_boots");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_chestplate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_helmet");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_hoe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_leggings");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_pickaxe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_shovel");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "golden_sword");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "granite");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "gray_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "green_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "hay_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_axe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_bars");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_boots");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_chestplate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_helmet");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_hoe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_ingot_from_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_ingot_from_nuggets");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_leggings");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_nugget");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_pickaxe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_shovel");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_sword");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "iron_trapdoor");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_boat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_door");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_fence_gate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_planks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "jungle_wooden_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "lapis_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "lapis_lazuli");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "lead");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "leather");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "leather_boots");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "leather_chestplate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "leather_helmet");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "leather_leggings");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_blue_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_blue_dye_from_blue_orchid");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_gray_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_gray_dye_from_azure_bluet");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_gray_dye_from_ink_bonemeal");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_gray_dye_from_oxeye_daisy");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_gray_dye_from_white_tulip");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "lime_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magenta_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magenta_dye_from_allium");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magenta_dye_from_lilac");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magma");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magma_cream");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "melon_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "melon_seeds");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "mossy_cobblestone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "mossy_cobblestone_wall");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "mossy_stonebrick");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "mushroom_stew");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "nether_brick");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "nether_brick_fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "nether_brick_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "nether_brick_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "nether_wart_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "oak_planks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "oak_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "oak_wooden_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "orange_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "orange_dye_from_orange_tulip");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "paper");
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pillar_quartz_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pink_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pink_dye_from_peony");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pink_dye_from_pink_tulip");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "polished_andesite");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "polished_diorite");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "polished_granite");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "prismarine");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "prismarine_bricks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pumpkin_pie");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pumpkin_seeds");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "purple_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "purpur_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "purpur_pillar");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "purpur_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "purpur_stairs");
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "quartz_block");
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "quartz_slab");
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "quartz_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "rabbit_stew_from_brown_mushroom");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "rabbit_stew_from_red_mushroom");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_dye_from_beetroot");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_dye_from_poppy");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_dye_from_rose_bush");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_dye_from_tulip");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_nether_brick");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_sandstone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_sandstone_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_sandstone_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "repeater");
       // added IE Metal Press Recipe. So slabs and stairs can now be made.
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "sandstone");
//       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "sandstone_slab");
//       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "sandstone_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "sea_lantern");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "shield");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "slime");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "slime_ball");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "smooth_red_sandstone");
      // RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "smooth_sandstone");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "speckled_melon");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spectral_arrow");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_boat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_door");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_fence");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_fence_gate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_planks");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "spruce_wooden_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_axe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_brick_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_brick_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_hoe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_pickaxe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_shovel");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_slab");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_stairs");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stone_sword");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "stonebrick");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "string_to_wool");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "sugar");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "tippedarrow");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "torch");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "trapdoor");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "trapped_chest");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wheat");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "white_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_axe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_button");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_door");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_hoe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_pickaxe");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_shovel");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "wooden_sword");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "yellow_bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "yellow_dye_from_dandelion");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "yellow_dye_from_sunflower");
        
       //Things actually added by TFC-NG, so leave them alone :)

       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "armor_stand");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "bed");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "bucket");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cauldron");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "compass");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "fire_charge");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "flint_and_steel");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "hay");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "hay_bale");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "item_frame");
       //Get us some ladders made from sticks
       //RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "ladder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "lapis_lazuli_block");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magenta_dye_lapis_red_pink");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "name_tag");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "painting");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "tnt");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "black_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "blue_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "brown_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "cyan_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "gray_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "green_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "Light_blue_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "light_gray_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "lime_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "magenta_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "orange_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "pink_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "purple_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "red_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "white_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "yellow_concrete_powder");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "heavy_weighted_pressure_plate");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "activator_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "detector_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "minecart");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "powered_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "steel_activator_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "steel_detector_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "steel_minecart");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "steel_rail");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "daylight_detector");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "exquisite_daylight_detector");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "flawless_daylight_detector");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "hopper");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "observer");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "piston");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "redstone_comparator");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "redstone_lamp");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "redstone_repeater");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "steel_hopper");
       RecipeUtils.removeRecipeByName(modRegistry,"minecraft", "shears");
 
   }
}
