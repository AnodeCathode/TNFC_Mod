package tnfcmod.Recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.Gem;
import net.dries007.tfc.objects.items.ItemGem;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;

public class OreDicEntries
{
    public static void init()
    {
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

        //Crafting tools
        for (Metal metal : TFCRegistries.METALS.getValuesCollection()){
            if (metal.isToolMetal()){
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_III) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER)));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL)));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW)));
                }
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_IV) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER)));
                    OreDictionary.registerOre("craftingToolMediumHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER)));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL)));
                    OreDictionary.registerOre("craftingToolMediumChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL)));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW)));
                    OreDictionary.registerOre("craftingToolMediumSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW)));

                }
                if (metal.getTier().isAtLeast(Metal.Tier.TIER_VI) ){
                    OreDictionary.registerOre("craftingToolHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER)));
                    OreDictionary.registerOre("craftingToolMediumHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER)));
                    OreDictionary.registerOre("craftingToolHardHammer",new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER)));
                    OreDictionary.registerOre("craftingToolChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL)));
                    OreDictionary.registerOre("craftingToolMediumChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL)));
                    OreDictionary.registerOre("craftingToolHardChisel",new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL)));
                    OreDictionary.registerOre("craftingToolSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW)));
                    OreDictionary.registerOre("craftingToolMediumSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW)));
                    OreDictionary.registerOre("craftingToolHardSaw",new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW)));
                }
            }
        }
    }

}
