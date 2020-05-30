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
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = "required-after:tfc")
public class tnfcmod
{
    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";


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
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

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
