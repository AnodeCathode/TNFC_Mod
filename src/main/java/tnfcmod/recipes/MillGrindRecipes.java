package tnfcmod.recipes;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemMaterial;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.blocks.wood.BlockPlanksTFC;

public class MillGrindRecipes
{

    public static void registerBwMMillRecipes()
    {

        for (BlockLogTFC block : BlocksTFC.getAllLogBlocks())
        {
            ItemStack log = new ItemStack(block, 1, 4);
            BWRegistry.WOOD_SAW.addRecipe(log, Lists.newArrayList(new ItemStack(BlockPlanksTFC.get(block.getWood()), 16), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SAWDUST, 4)));
            ItemStack log2 = new ItemStack(block, 1, 5);
            BWRegistry.WOOD_SAW.addRecipe(log2, Lists.newArrayList(new ItemStack(BlockPlanksTFC.get(block.getWood()), 16), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SAWDUST, 4)));
            ItemStack log3 = new ItemStack(block, 1, 6);
            BWRegistry.WOOD_SAW.addRecipe(log3, Lists.newArrayList(new ItemStack(BlockPlanksTFC.get(block.getWood()), 16), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SAWDUST, 4)));
        }

    }


}
