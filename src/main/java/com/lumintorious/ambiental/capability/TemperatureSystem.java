package com.lumintorious.ambiental.capability;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

import com.lumintorious.ambiental.TFCAmbientalConfig;
import com.lumintorious.ambiental.damage.ColdDamageSource;
import com.lumintorious.ambiental.damage.HeatDamageSource;
import com.lumintorious.ambiental.modifiers.*;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;

public class TemperatureSystem implements ITemperatureSystem
{
    public static Map<EntityPlayer, TemperatureSystem> entries = new HashMap<>();
    public static final BaseModifier CORE_TEMPERATURE = new BaseModifier("core", 0f, 0.0f);
    public boolean isRising;

    public static float AVERAGE = TFCAmbientalConfig.GENERAL.averageTemperature;
    public static float HOT_THRESHOLD = TFCAmbientalConfig.GENERAL.hotTemperature;
    public static float COOL_THRESHOLD = TFCAmbientalConfig.GENERAL.coldTemperature;
    public static float BURN_THRESHOLD = TFCAmbientalConfig.GENERAL.burningTemperature;
    public static float FREEZE_THRESHOLD = TFCAmbientalConfig.GENERAL.freezingTemperature;

    public ModifierStorage modifiers = new ModifierStorage();

    public float bodyTemperature = 15f;
    public EntityPlayer player;

    public TemperatureSystem(EntityPlayer player)
    {
        this.player = player;
        evaluateModifiers();
    }

    public void clearModifiers()
    {
        this.modifiers = new ModifierStorage();
    }

    public void evaluateModifiers()
    {
        this.clearModifiers();
        ItemModifier.computeModifiers(player, modifiers);
        EnvironmentalModifier.computeModifiers(player, modifiers);
        BlockModifier.computeModifiers(player, modifiers);
        EquipmentModifier.getModifiers(player, modifiers);


        savedTarget = modifiers.getTargetTemperature();
        savedPotency = modifiers.getTotalPotency();
    }

    public float savedTarget = 15f;

    public float getTargetTemperature()
    {
        return savedTarget;
    }

    public static final float BAD_MULTIPLIER = 0.009f;
    public static final float GOOD_MULTIPLIER = 0.009f;
    public static final float CHANGE_CAP = 8f;
    public static final float HIGH_CHANGE = 0.20f;

    public float savedPotency = 1f;

    public float getPotency()
    {
        return savedPotency;
    }

    public float getTemperatureChange()
    {
        float target = getTargetTemperature();
        float speed = getPotency() * TFCAmbientalConfig.GENERAL.temperatureMultiplier;
        float change = Math.min(CHANGE_CAP, target - bodyTemperature);
        float newTemp = bodyTemperature + change;
        boolean isRising = true;
        if ((bodyTemperature < AVERAGE && newTemp > bodyTemperature) || (bodyTemperature > AVERAGE && newTemp < bodyTemperature))
        {
            speed *= GOOD_MULTIPLIER * TFCAmbientalConfig.GENERAL.positiveModifier;
        }
        else
        {
            speed *= BAD_MULTIPLIER * TFCAmbientalConfig.GENERAL.negativeModifier;
        }
        return ((float) change * speed);
    }

    public int tick = 0;
    public int damageTick = 0;

    public void say(Object string)
    {
        player.sendMessage(new TextComponentString(string.toString()));
    }

    public boolean isDead = false;

    public void update()
    {
        if (player.isDead)
        {
            setTemperature(15);
            entries.remove(this.player);
            isDead = true;
            updateAndSync();
            return;
        }
        else
        {
            isDead = false;
        }
        this.setTemperature(this.getTemperature() + this.getTemperatureChange() / TFCAmbientalConfig.GENERAL.tickInterval);
        if (tick != TFCAmbientalConfig.GENERAL.tickInterval)
        {
            tick++;
            return;
        }
        else
        {
            tick = 0;
            if (damageTick == 8)
            {
                damageTick = 0;
                if (TFCAmbientalConfig.GENERAL.takeDamage)
                {
                    if (this.getTemperature() > BURN_THRESHOLD)
                    {
                        if (this.getTemperature() > BURN_THRESHOLD + 3)
                        {
                            player.attackEntityFrom(HeatDamageSource.INSTANCE, 4f);
                        }
                        else
                        {
                            player.attackEntityFrom(HeatDamageSource.INSTANCE, 2f);
                        }
                    }
                    else if (this.getTemperature() < FREEZE_THRESHOLD)
                    {
                        if (this.getTemperature() < FREEZE_THRESHOLD - 3)
                        {
                            player.attackEntityFrom(ColdDamageSource.INSTANCE, 4f);
                        }
                        else
                        {
                            player.attackEntityFrom(ColdDamageSource.INSTANCE, 2f);
                        }
                    }
                }
                if (TFCAmbientalConfig.GENERAL.loseHungerThirst)
                {
                    if (player.getFoodStats() instanceof FoodStatsTFC)
                    {
                        FoodStatsTFC stats = (FoodStatsTFC) player.getFoodStats();
                        if (this.getTemperature() > (HOT_THRESHOLD + BURN_THRESHOLD) / 2)
                        {
                            stats.setThirst(stats.getThirst() - 10);
                        }
                        else if (this.getTargetTemperature() < (COOL_THRESHOLD + FREEZE_THRESHOLD) / 2)
                        {
                            stats.setFoodLevel(stats.getFoodLevel() - 1);
                        }
                    }

                }

            }
            else
            {
                damageTick++;
            }
            this.evaluateModifiers();
            updateAndSync();
        }

    }

    public String toString()
    {
        String str = "";
        for (BaseModifier modifier : modifiers)
        {
            str += modifier.getUnlocalizedName() + " -> " + modifier.getChange() + " @ " + modifier.getPotency() + "\n";
        }
        return String.format(
            "Body: %.1f ( %.4f )\n"
                + "Target: %.1f \n"
                + "Potency: %.4f",
            bodyTemperature,
            this.getTemperatureChange(),
            this.getTargetTemperature(),
            modifiers.getTotalPotency()
        ) + "\n" + str;
    }

    public static TemperatureSystem getTemperatureFor(EntityPlayer player)
    {
        TemperatureSystem entry = entries.get(player);
        if (entry == null)
        {
            entry = new TemperatureSystem(player);
            entries.put(player, entry);
        }
        return entry;
    }

    @Override
    public float getTemperature()
    {
        return bodyTemperature;
    }

    @Override
    public void setTemperature(float newTemp)
    {
        if (newTemp < this.getTemperature())
        {
            isRising = false;
        }
        else
        {
            isRising = true;
        }
        this.bodyTemperature = newTemp;
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    @Override
    public float getChange()
    {
        return getTemperatureChange();
    }

    public float getChangeSpeed()
    {
        return getPotency();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setFloat("temperature", this.getTemperature());
        tag.setFloat("target", this.getTargetTemperature());
        tag.setFloat("potency", this.getPotency());
        tag.setBoolean("isDead", this.isDead);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        if (!(nbt instanceof NBTTagCompound)) throw new IllegalArgumentException("Temperature must be read from an NBTTagCompound!");

        TemperatureSystem temp = this;
        NBTTagCompound tag = (NBTTagCompound) nbt;
        if (tag.hasKey("temperature"))
        {
            temp.setTemperature(tag.getFloat("temperature"));
            temp.savedTarget = (tag.getFloat("target"));
            temp.savedPotency = (tag.getFloat("potency"));
            temp.isDead = (tag.getBoolean("isDead"));
            if (isDead)
            {
                setTemperature(15);
            }
        }
        else
        {
            temp.setTemperature(15f);
        }
    }

    public void updateAndSync()
    {
        EntityPlayer player = getPlayer();
        if (player instanceof EntityPlayerMP)
        {
            TerraFirmaCraft.getNetwork().sendTo(new TemperaturePacket(serializeNBT()), (EntityPlayerMP) player);
        }
    }
}
