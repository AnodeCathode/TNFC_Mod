package tnfcmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.oredict.RecipeSorter;

import tnfcmod.proxy.CommonProxy;
import tnfcmod.util.FireworksRecipes;
import tnfcmod.util.VeinLoader;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;


@SuppressWarnings("WeakerAccess")
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = tnfcmod.DEPENDENCIES)
public class tnfcmod {

    @Mod.Instance("tnfcmod")
    public static tnfcmod instance;

    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-before:autoreglib;required-after:tfc;required-after:rockhounding_chemistry;after:immersiveengineering;required-after:betterwithmods;required-after:astikorcarts;required-after:jaff;required:firstaid;after:mineraltracker;after:mcwbridges";

    private final Logger log = LogManager.getLogger(MODID);

    public static final ItemArmor.ArmorMaterial strawArmorMaterial = EnumHelper.addArmorMaterial(
        "STRAW"
        , MODID + ":straw_hat"
        , 5
        , new int[]{0, 0, 0, 0}
        , 0
        , SoundEvents.ITEM_ARMOR_EQUIP_LEATHER
        , 0.0F
    );


    @SidedProxy(serverSide = "tnfcmod.proxy.CommonProxy",
        clientSide = "tnfcmod.proxy.ClientProxy")
    public static CommonProxy proxy;


    public static Logger getLog()
    {
        return instance.log;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log.info("preInit TNFCMod Stuff");
        VeinLoader.INSTANCE.preInit(event.getModConfigurationDirectory());
        proxy.preInit(event);


    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        log.info("Init TNFCMod Stuff");
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        log.info("postInit TNFCMod Stuff");
        proxy.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        log.info("serverStarting TNFCMod Stuff");
        RecipeSorter.register("tnfcmod:fireworks",  FireworksRecipes.class, SHAPELESS, "after:minecraft:shapeless");
        proxy.serverStarting(event);
    }

}
