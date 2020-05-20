package tnfcmod.objects.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;

public class ItemBackpackFrame extends Item
{
    protected String name;
    public ItemBackpackFrame(String name)
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
    public ItemBackpackFrame setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}
