package tnfcmod.handlers;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import tnfcmod.client.gui.inventory.GuiPlowCartTFC;
import tnfcmod.inventory.ContainerPlowCartTFC;
import tnfcmod.objects.entities.EntityPlowCartTFC;

public class GuiHandler implements IGuiHandler {
    public GuiHandler() {
    }

    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch(id) {
            case 0:
                EntityPlowCartTFC plow = (EntityPlowCartTFC)world.getEntityByID(x);
                return new ContainerPlowCartTFC(player.inventory, plow.inventory, plow, player);
            default:
                return null;
        }
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch(id) {
            case 0:
                EntityPlowCartTFC plow = (EntityPlowCartTFC) world.getEntityByID(x);
                return new GuiPlowCartTFC(player.inventory, plow.inventory, plow, player);
            default:
                return null;
        }
    }
}
