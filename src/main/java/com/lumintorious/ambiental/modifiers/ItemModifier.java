package com.lumintorious.ambiental.modifiers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.lumintorious.ambiental.api.IItemTemperatureOwner;
import com.lumintorious.ambiental.api.IItemTemperatureProvider;
import com.lumintorious.ambiental.api.TemperatureRegistry;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;

public class ItemModifier extends BaseModifier
{

    public ItemModifier(String name)
    {
        super(name);
    }

    public ItemModifier(String name, float change, float potency)
    {
        super(name, change, potency);
    }

    public static void computeModifiers(EntityPlayer player, ModifierStorage modifiers)
    {
        int strawCount = 0;
        int iceCount = 0;
        int snowCount = 0;
        for (ItemStack stack : player.inventoryContainer.inventoryItemStacks)
        {
            Item item = stack.getItem();

            if (stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null))
            {
                IItemHeat heat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                float temp = heat.getTemperature() / 500;
                float change = temp;
                float potency = 0f;
                modifiers.add(new ItemModifier("heat_item", change, potency * stack.getCount()));
            }
            if (item instanceof IItemTemperatureOwner)
            {
                IItemTemperatureOwner owner = (IItemTemperatureOwner) stack.getItem();
                modifiers.add(owner.getModifier(stack, player));
            }
            for (IItemTemperatureProvider provider : TemperatureRegistry.ITEMS)
            {
                modifiers.add(provider.getModifier(stack, player));
            }
            //individual stacks of straw provide a small amount of cold protection. Stack size doesn't matter.
            if (item == ItemsTFC.STRAW)
            {
                strawCount += 1;
            }
            if (item == Item.getItemFromBlock(Blocks.PACKED_ICE) || item == Item.getItemFromBlock(Blocks.ICE) )
            {
                iceCount += 1;
            }
            if (stack.getItem() == Item.getItemFromBlock(Blocks.SNOW))
            {
                snowCount += 1;
            }
        }
        if (strawCount > 0)
        {
            modifiers.add(new ItemModifier("straw_insulation", strawCount * 0.4f, 0.2f));
        }
        if (iceCount > 0)
        {
            modifiers.add(new ItemModifier("iceblocks", iceCount * -0.4f, 0.2f));
        }
        if (snowCount > 0)
        {
            modifiers.add(new ItemModifier("snowblocks", snowCount * -0.2f, 0.2f));
        }

    }


}
