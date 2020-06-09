package tnfcmod;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import tnfcmod.proxy.CommonProxy;
import tnfcmod.util.VeinLoader;


@SuppressWarnings("WeakerAccess")
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = tnfcmod.DEPENDENCIES)
public class tnfcmod
{
    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:tfc;after:rockhounding_chemistry;after:immersiveengineering";

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
        logger = event.getModLog();
        VeinLoader.INSTANCE.preInit(event.getModConfigurationDirectory());

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }


}
