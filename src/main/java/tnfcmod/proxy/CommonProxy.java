package tnfcmod.proxy;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import net.dries007.tfc.objects.entity.animal.*;
import tnfcmod.handlers.GuiHandler;
import tnfcmod.qfc.entity.EntityCrab;
import tnfcmod.qfc.entity.EntityFrog;
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
        addScrollDrop(EntitySheepTFC.class, Enchantments.MENDING);
        addScrollDrop(EntityCowTFC.class, Enchantments.SWEEPING);
        addScrollDrop(EntityDonkeyTFC.class, Enchantments.BINDING_CURSE);
        addScrollDrop(EntityHorseTFC.class, Enchantments.FIRE_PROTECTION);
        addScrollDrop(EntityChickenTFC.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityCrab.class, Enchantments.AQUA_AFFINITY);
        addScrollDrop(EntityFrog.class, Enchantments.AQUA_AFFINITY);
        addScrollDrop(EntityDuckTFC.class, Enchantments.AQUA_AFFINITY);
        addScrollDrop(EntityCreeper.class, Enchantments.BLAST_PROTECTION);
        addScrollDrop(EntityZombie.class, Enchantments.LOOTING);
        addScrollDrop(EntityEnderman.class, Enchantments.SILK_TOUCH);
        addScrollDrop(EntitySkeleton.class, Enchantments.EFFICIENCY);
        addScrollDrop(EntityWitherSkeleton.class, Enchantments.SILK_TOUCH);
        addScrollDrop(EntityBlaze.class, Enchantments.FIRE_PROTECTION);
        addScrollDrop(EntityHorseTFC.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityCamelTFC.class, Enchantments.EFFICIENCY);
        addScrollDrop(EntityDeerTFC.class, Enchantments.FORTUNE);
        addScrollDrop(EntityGazelleTFC.class, Enchantments.EFFICIENCY);
        addScrollDrop(EntityHyenaTFC.class, Enchantments.SMITE);
        addScrollDrop(EntityGhast.class, Enchantments.BLAST_PROTECTION);
        addScrollDrop(EntityBoarTFC.class, Enchantments.PROJECTILE_PROTECTION);
        addScrollDrop(EntityGoatTFC.class, Enchantments.THORNS);
        addScrollDrop(EntityPigTFC.class, Enchantments.SILK_TOUCH);
        addScrollDrop(EntityMagmaCube.class, Enchantments.FIRE_PROTECTION);
        addScrollDrop(EntityOcelotTFC.class, Enchantments.LOOTING);
        addScrollDrop(EntityParrotTFC.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityMuskOxTFC.class, Enchantments.SMITE);
        addScrollDrop(EntityHareTFC.class, Enchantments.INFINITY);
        addScrollDrop(EntityGrouseTFC.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityTurkeyTFC.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityQuailTFC.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityAlpacaTFC.class, Enchantments.PROJECTILE_PROTECTION);
        addScrollDrop(EntityJackalTFC.class, Enchantments.SWEEPING);
        addScrollDrop(EntityAnimalMammal.class, Enchantments.SILK_TOUCH);
        addScrollDrop(EntityPigZombie.class, Enchantments.FLAME);


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
