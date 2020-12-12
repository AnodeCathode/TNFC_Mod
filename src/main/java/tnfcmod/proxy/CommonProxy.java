package tnfcmod.proxy;

import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import net.dries007.tfc.objects.entity.animal.*;
import tnfcmod.handlers.GuiHandler;
import tnfcmod.qfc.module.ModuleLoader;
import tnfcmod.qfc.sounds.QFCSounds;
import tnfcmod.recipes.FirstAidDmgSources;
import tnfcmod.recipes.LootTablesTNFC;

import static betterwithmods.module.hardcore.creatures.HCEnchanting.addScrollDrop;
import static tnfcmod.tnfcmod.instance;

public class CommonProxy
{
    public void registerItemRenderer(Item item, int meta, String id) {
    }

    public void registerNormalItemRenderer(Item item, int meta, String id) {
    }

    public void preInit(FMLPreInitializationEvent event){
        addScrollDrop(EntityPolarBearTFC.class, Enchantments.FROST_WALKER);
        addScrollDrop(EntityGrizzlyBearTFC.class, Enchantments.KNOCKBACK);
        addScrollDrop(EntityBlackBearTFC.class, Enchantments.LOOTING);
        addScrollDrop(EntityDireWolfTFC.class, Enchantments.POWER);
        addScrollDrop(EntitySaberToothTFC.class, Enchantments.FORTUNE);
        addScrollDrop(EntityPantherTFC.class, Enchantments.EFFICIENCY);
        addScrollDrop(EntityLionTFC.class, Enchantments.UNBREAKING);
        addScrollDrop(EntityCougarTFC.class, Enchantments.SHARPNESS);
        addScrollDrop(EntityRabbitTFC.class, Enchantments.LUCK_OF_THE_SEA);
        FirstAidDmgSources.registerDefaults();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        LootTablesTNFC.init();
        QFCSounds.init();
        ModuleLoader.preInit(event);
    }

    public void init(FMLInitializationEvent event)
    {
        ModuleLoader.init(event);
    }

    public void postInit(FMLPostInitializationEvent event){
        ModuleLoader.postInit(event);
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
        ModuleLoader.serverStarting(event);
    }

}
