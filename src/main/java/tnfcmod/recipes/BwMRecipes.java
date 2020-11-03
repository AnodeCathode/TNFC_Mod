package tnfcmod.recipes;

import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;

import betterwithmods.common.BWRegistry;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.items.wood.ItemLumberTFC;
import tfctech.objects.items.TechItems;

public class BwMRecipes
{

    public static void registerBwMSawRecipes()
    {
        List recipes = BWRegistry.WOOD_SAW.getRecipes();
        for (Object recipe : recipes)
        {

        }
        for (BlockLogTFC block : BlocksTFC.getAllLogBlocks())
        {
            ItemStack log = new ItemStack(block, 1, 4);
            BWRegistry.WOOD_SAW.addRecipe(log, Lists.newArrayList(new ItemStack(ItemLumberTFC.get(block.getWood()), 12), new ItemStack(TechItems.WOOD_POWDER, 4)));
            ItemStack log2 = new ItemStack(block, 1, 5);
            BWRegistry.WOOD_SAW.addRecipe(log2, Lists.newArrayList(new ItemStack(ItemLumberTFC.get(block.getWood()), 12), new ItemStack(TechItems.WOOD_POWDER, 4)));
            ItemStack log3 = new ItemStack(block, 1, 6);
            BWRegistry.WOOD_SAW.addRecipe(log3, Lists.newArrayList(new ItemStack(ItemLumberTFC.get(block.getWood()), 12), new ItemStack(TechItems.WOOD_POWDER, 4)));
        }

    }


}
