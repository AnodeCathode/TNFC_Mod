package tnfcmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


import tnfcmod.handlers.GuiHandler;

import tnfcmod.proxy.CommonProxy;
import tnfcmod.recipes.LootTablesTNFC;
import tnfcmod.util.VeinLoader;


@SuppressWarnings("WeakerAccess")
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = tnfcmod.DEPENDENCIES)
public class tnfcmod {

    @Mod.Instance("tnfcmod")
    public static tnfcmod instance;
    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:tfc;after:rockhounding_chemistry;after:immersiveengineering;after:betterwithmods;after:astikorcarts;after:jaff";

    private final Logger log = LogManager.getLogger(MODID);



    @Mod.Instance
    private static tnfcmod INSTANCE = null;

    @SidedProxy(serverSide = "tnfcmod.proxy.CommonProxy",
        clientSide = "tnfcmod.proxy.ClientProxy")
    public static CommonProxy proxy;


    public static Logger getLog()
    {
        return INSTANCE.log;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log.info("Loading TechnodeFirmaCraft Stuff");
        VeinLoader.INSTANCE.preInit(event.getModConfigurationDirectory());
        proxy.preInit(event);

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        LootTablesTNFC.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }


}
