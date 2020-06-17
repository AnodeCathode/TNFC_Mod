package tnfcmod;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


import tnfcmod.capabilities.IPull;
import tnfcmod.capabilities.PullFactory;
import tnfcmod.capabilities.PullStorage;
import tnfcmod.handlers.GuiHandler;
import tnfcmod.handlers.PacketHandler;
import tnfcmod.proxy.CommonProxy;
import tnfcmod.util.VeinLoader;


@SuppressWarnings("WeakerAccess")
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = tnfcmod.DEPENDENCIES)
public class tnfcmod {

    @Mod.Instance("tnfcmod")
    public static tnfcmod instance;
    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:tfc;after:rockhounding_chemistry;after:immersiveengineering;after:betterwithmods;after:astikorcarts";

    private static Logger logger;



    public static Logger getLog()
    {
        return logger;
    }

    @SidedProxy(serverSide = "tnfcmod.proxy.CommonProxy",
        clientSide = "tnfcmod.proxy.ClientProxy")
    public static CommonProxy proxy;




    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PacketHandler.registerPackets();
        CapabilityManager.INSTANCE.register(IPull.class, new PullStorage(), PullFactory::new);
        logger = event.getModLog();
        VeinLoader.INSTANCE.preInit(event.getModConfigurationDirectory());
        proxy.preInit(event);

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }


}
