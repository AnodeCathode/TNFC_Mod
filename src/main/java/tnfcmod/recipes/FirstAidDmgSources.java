package tnfcmod.recipes;

import java.util.Objects;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import lyeoj.tfcthings.init.TFCThingsDamageSources;
import com.lumintorious.ambiental.AmbientalDamage;
import ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import net.dries007.tfc.util.DamageSourcesTFC;

public class FirstAidDmgSources
{

    public static void registerDefaults() {
        DamageDistributionBuilderFactory distributionBuilderFactory = Objects.requireNonNull(DamageDistributionBuilderFactory.getInstance());

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY)
            .registerStatic(AmbientalDamage.HEAT);

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY)
            .registerStatic(AmbientalDamage.COLD);


        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY)
            .registerStatic(DamageSourcesTFC.DEHYDRATION, DamageSourcesTFC.FOOD_POISON);

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.FEET, EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT)
            .addDistributionLayer(EntityEquipmentSlot.LEGS, EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG)
            .registerStatic(DamageSourcesTFC.GRILL, DamageSourcesTFC.SOUP);

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.FEET, EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT)
            .addDistributionLayer(EntityEquipmentSlot.LEGS, EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG)
            .registerStatic(DamageSource.IN_FIRE, DamageSource.LAVA);

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.HEAD, EnumPlayerPart.HEAD)
            .addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.LEFT_ARM, EnumPlayerPart.RIGHT_ARM)
            .addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY)
            .registerStatic(DamageSource.FALLING_BLOCK);

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.FEET, EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT)
            .addDistributionLayer(EntityEquipmentSlot.LEGS, EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG)
            .registerStatic(DamageSourcesTFC.BERRYBUSH);

        distributionBuilderFactory.newStandardBuilder()
            .addDistributionLayer(EntityEquipmentSlot.FEET, EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT)
            .registerStatic(TFCThingsDamageSources.BEAR_TRAP);



    }

}
