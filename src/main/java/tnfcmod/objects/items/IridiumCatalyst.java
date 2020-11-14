package tnfcmod.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;

public class IridiumCatalyst extends Item
{

    protected String name;

    public IridiumCatalyst(String name)
    {
        this.name = name;
        setTranslationKey(MODID + "." + name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.MATERIALS);
        this.setMaxDamage(300);
        this.setMaxStackSize(1);
    }

    public void registerItemModel()
    {
        tnfcmod.proxy.registerItemRenderer(this, 0, name);
    }


}

