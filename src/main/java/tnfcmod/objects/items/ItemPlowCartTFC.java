package tnfcmod.objects.items;

import net.minecraft.world.World;

import de.mennomax.astikorcarts.entity.AbstractDrawn;
import tnfcmod.objects.entities.EntityPlowCartTFC;
import tnfcmod.tnfcmod;

public class ItemPlowCartTFC extends AbstractCartItemTFC
{
    protected String name;

    public ItemPlowCartTFC(String name) {
        super(name);
        this.name = name;
    }

    public void registerItemModel() {

        tnfcmod.proxy.registerItemRenderer(this, 0, name);
    }
    public AbstractDrawn newCart(World worldIn) {
        return new EntityPlowCartTFC(worldIn);
    }

}



