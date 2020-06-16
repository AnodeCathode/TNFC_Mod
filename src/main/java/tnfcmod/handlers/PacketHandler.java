package tnfcmod.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;


import de.mennomax.astikorcarts.packets.CPacketOpenCartGui;
import de.mennomax.astikorcarts.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.packets.SPacketDrawnUpdate;

public class PacketHandler {
    private static int id = 0;
    public static final SimpleNetworkWrapper INSTANCE;

    public PacketHandler() {
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(CPacketOpenCartGui.OpenCartGuiPacketHandler.class, CPacketOpenCartGui.class, id++, Side.SERVER);
        INSTANCE.registerMessage(SPacketDrawnUpdate.DrawnUpdatePacketHandler.class, SPacketDrawnUpdate.class, id++, Side.CLIENT);
    }

    static {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("tnfcmod");
    }
}
