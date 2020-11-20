package tnfcmod.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.items.ItemGem;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import tnfcmod.objects.items.TNFCItems;

public class OreDicEntries
{
    public static void init()
    {

        //Register oredict entries for our 'fake' weak steel dusts
        OreDictionary.registerOre("dustWeakSteel", new ItemStack(TNFCItems.weak_steel_dust,1));
        OreDictionary.registerOre("dustWeakRedSteel", new ItemStack(TNFCItems.weak_red_steel_dust,1));
        OreDictionary.registerOre("dustWeakBlueSteel", new ItemStack(TNFCItems.weak_blue_steel_dust,1));

        //OreDictionary.WILDCARD_VALUE;
        //Gems
        for (Gem gem : Gem.values()) {
            for (Gem.Grade grade : Gem.Grade.values()){
                OreDictionary.registerOre("gem", ItemGem.get(gem, grade,1));
            }
            if (gem == Gem.SAPPHIRE || gem == Gem.JASPER || gem == Gem.OPAL || gem == Gem.AMETHYST){
                OreDictionary.registerOre("craftingBlueGem", new ItemStack(ItemGem.get(gem), 1, OreDictionary.WILDCARD_VALUE));
            }
            if (gem == Gem.EMERALD || gem == Gem.BERYL || gem == Gem.JADE || gem == Gem.TOURMALINE){
                OreDictionary.registerOre("craftingGreenGem", new ItemStack(ItemGem.get(gem), 1, OreDictionary.WILDCARD_VALUE));
            }
            if (gem == Gem.RUBY || gem == Gem.GARNET || gem == Gem.AGATE || gem == Gem.TOPAZ){
                OreDictionary.registerOre("craftingRedGem", new ItemStack(ItemGem.get(gem), 1, OreDictionary.WILDCARD_VALUE));
            }
        }
        // Build some ore groups, for rockhounding

        for (Ore ore : TFCRegistries.ORES.getValuesCollection()){
             if (ore.isGraded()){
                 //what to do. What do we pick
                 OreDictionary.registerOre("gradedRichOres", ItemOreTFC.get(ore, Ore.Grade.RICH, 1));
             }

        }

        //Crafting tools
        for (Metal metal : TFCRegistries.METALS.getValuesCollection()){
            if (metal.isToolMetal()){
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_III) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                }
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_IV) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));

                }
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_V) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                }
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_VI) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolEliteHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolEliteChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolEliteSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolEliteAxe",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolMediumPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolHardPick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                    OreDictionary.registerOre("craftingToolElitePick",new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE),1,OreDictionary.WILDCARD_VALUE));
                }
            }
        }
    }

}
