package tnfcmod.objects.items;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;

public class ItemTNFC extends ItemTFC implements IBauble
{
    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack itemStack)
    {
        return null;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack itemStack)
    {
        return null;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack)
    {
        return null;
    }
}
