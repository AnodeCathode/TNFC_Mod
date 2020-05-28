package tnfcmod.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;

public class IEMetalPressMold extends Item
{
    protected String name;

    public IEMetalPressMold(String name)
    {
        this.name = name;
        setTranslationKey(MODID + "." + name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.MATERIALS);
    }

    public void registerItemModel() {

        tnfcmod.proxy.registerItemRenderer(this, 0, name);
    }
    @Override
    public IEMetalPressMold setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

}
