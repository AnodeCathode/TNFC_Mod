package tnfcmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import net.dries007.tfc.objects.entity.animal.*;
import tnfcmod.handlers.GuiHandler;
import tnfcmod.proxy.CommonProxy;
import tnfcmod.recipes.LootTablesTNFC;
import tnfcmod.util.VeinLoader;

import static betterwithmods.module.hardcore.creatures.HCEnchanting.addScrollDrop;


@SuppressWarnings("WeakerAccess")
@Mod(modid = tnfcmod.MODID, name = tnfcmod.NAME, version = tnfcmod.VERSION, dependencies = tnfcmod.DEPENDENCIES)
public class tnfcmod {

    @Mod.Instance("tnfcmod")
    public static tnfcmod instance;
    public static final String MODID = "tnfcmod";
    public static final String NAME = "Technodefirmacraft";
    public static final String VERSION = "@VERSION@";
    public static final String DEPENDENCIES = "required-after:tfc;required-after:rockhounding_chemistry;after:immersiveengineering;required-after:betterwithmods;required-after:astikorcarts;required-after:jaff;required-after:firstaid";

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
        addScrollDrop(EntityPolarBearTFC.class, Enchantments.FROST_WALKER);
        addScrollDrop(EntityGrizzlyBearTFC.class, Enchantments.KNOCKBACK);
        addScrollDrop(EntityBlackBearTFC.class, Enchantments.LOOTING);
        addScrollDrop(EntityDireWolfTFC.class, Enchantments.POWER);
        addScrollDrop(EntitySaberToothTFC.class, Enchantments.FORTUNE);
        addScrollDrop(EntityPantherTFC.class, Enchantments.EFFICIENCY);
        addScrollDrop(EntityLionTFC.class, Enchantments.UNBREAKING);
        addScrollDrop(EntityCougarTFC.class, Enchantments.SHARPNESS);
        addScrollDrop(EntityRabbitTFC.class, Enchantments.LUCK_OF_THE_SEA);

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        LootTablesTNFC.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }


}
