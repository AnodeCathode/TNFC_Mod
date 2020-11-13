package com.lumintorious.ambiental;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.lumintorious.ambiental.capability.ITemperatureSystem;
import com.lumintorious.ambiental.capability.TemperaturePacket;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.DumbStorage;
import net.dries007.tfc.proxy.IProxy;

@Mod(modid = TFCAmbiental.MODID, name = TFCAmbiental.NAME, version = TFCAmbiental.VERSION, dependencies = "required-after:tfc")
public class TFCAmbiental
{
    public static final String MODID = "tfcambiental";
    public static final String NAME = "TFC Ambiental";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static TFCAmbiental INSTANCE;

    @SidedProxy(modId = MODID, clientSide = "net.dries007.tfc.proxy.ClientProxy", serverSide = "net.dries007.tfc.proxy.ServerProxy")
    private static IProxy PROXY = null;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(new GuiRenderer());
        }
        CapabilityManager.INSTANCE.register(ITemperatureSystem.class, new DumbStorage(), () -> null);


        TerraFirmaCraft.getNetwork().registerMessage(new TemperaturePacket.Handler(), TemperaturePacket.class, 0, Side.CLIENT);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new PlayerTemperatureHandler());

    }

}
