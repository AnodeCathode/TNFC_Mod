package tnfcmod;

import org.apache.logging.log4j.Logger;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import tnfcmod.Recipes.TNFCRecipes;
import tnfcmod.objects.items.TNFCItems;
import tnfcmod.proxy.CommonProxy;


@SuppressWarnings("WeakerAccess")
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = "required-after:tfc", certificateFingerprint = tnfcmod.SIGNING_KEY)
public class tnfcmod
{
    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";
    public static final String SIGNING_KEY = "@FINGERPRINT@";

    private static Logger logger;
    private static final boolean signedBuild = true;

    public static Logger getLog()
    {
        return logger;
    }

    @SidedProxy(serverSide = "tnfcmod.proxy.CommonProxy",
        clientSide = "tnfcmod.proxy.ClientProxy")
    public static CommonProxy proxy;


    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        /*if (!event.isDirectory())
        {
            signedBuild = false; // todo disabled for the time being
        }*/
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if (!signedBuild)
        {
            logger.error("INVALID FINGERPRINT DETECTED! This means this jar file has been compromised and are not supported.");
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    }


}
