package com.jaquadro.minecraft.storagedrawerstfc.core;

import com.jaquadro.minecraft.storagedrawerstfc.block.EnumMod;
import com.jaquadro.minecraft.storagedrawerstfc.block.EnumVariant;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ModCreativeTabs
{
    private ModCreativeTabs () { }

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storagedrawerstfc") {
        @Override
        @Nonnull
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon () {
            EnumVariant material = EnumVariant.DEFAULT;
            for (EnumMod mod : EnumMod.values()) {
                if (mod.isLoaded()) {
                    material = mod.getDefaultMaterial();
                    break;
                }
            }

            if (material == EnumVariant.DEFAULT)
                return com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs.tabStorageDrawers.createIcon();

            @Nonnull ItemStack stack = new ItemStack(Item.getItemFromBlock(ModBlocks.extraDrawers), 1, 1);

            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("material", material.getResource().toString());
            stack.setTagCompound(tag);

            return stack;
        }
    };
}
