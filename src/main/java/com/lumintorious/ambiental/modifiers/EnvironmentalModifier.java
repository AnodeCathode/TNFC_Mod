package com.lumintorious.ambiental.modifiers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumSkyBlock;

import com.lumintorious.ambiental.TFCAmbientalConfig;
import com.lumintorious.ambiental.api.IEnvironmentalTemperatureProvider;
import com.lumintorious.ambiental.api.TemperatureRegistry;
import com.lumintorious.ambiental.capability.TemperatureCapability;
import com.lumintorious.ambiental.effects.TempEffect;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.food.Nutrient;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateData;
import net.dries007.tfc.util.climate.ClimateTFC;

public class EnvironmentalModifier extends BaseModifier
{

    public EnvironmentalModifier(String name)
    {
        super(name);
    }

    public EnvironmentalModifier(String name, float change, float potency) {
        super(name, change, potency);
    }



    public static float getEnvironmentTemperature(EntityPlayer player)
    {
        float actual = 0f;
        if (player.world.provider.getDimensionType() == DimensionType.OVERWORLD)
        {
            float avg = ClimateData.DEFAULT.getRegionalTemp() + 2.5f;
            float incr = 0f;
            actual = ClimateTFC.getActualTemp(player.world, player.getPosition());
            if (TFCAmbientalConfig.GENERAL.harsherTemperateAreas)
            {
                float diff = actual - TemperatureCapability.AVERAGE;
                float sign = Math.signum(diff);
                float generalDiff = Math.abs(avg - TemperatureCapability.AVERAGE);
                float mult0 = Math.max(0f, TFCAmbientalConfig.GENERAL.harsherMultiplier - 1f);
                float multiplier = 1 + Math.max(0, 1 - generalDiff / 40) * mult0;
                actual = TemperatureCapability.AVERAGE + (diff + 3f * sign) * multiplier;
            }

        }
        else
        {
            actual = 15;
        }
        return actual;
    }

    public static float getEnvironmentHumidity(EntityPlayer player)
    {
        if (player.world.provider.getDimensionType() == DimensionType.OVERWORLD)
        {
            return ClimateTFC.getRainfall(player.world, player.getPosition()) / 3000;
        }
        else
        {
            return 0;
        }

    }

    public static EnvironmentalModifier handleFire(EntityPlayer player)
    {
        return player.isBurning() ? new EnvironmentalModifier("on_fire", 5f, 5f) : null;
    }

    public static EnvironmentalModifier handleWater(EntityPlayer player)
    {
        if (player.isInWater())
        {
            BlockPos pos = player.getPosition();
            IBlockState state = player.world.getBlockState(pos);
            if (state.getBlock() == FluidsTFC.HOT_WATER.get().getBlock())
            {
                return new EnvironmentalModifier("in_hot_water", 6f, 8f);
            }
            else if (state.getBlock() == Blocks.LAVA)
            {
                return new EnvironmentalModifier("in_lava", 16f, 10f);
            }
            else
            {
                return new EnvironmentalModifier("in_water", -8f, 8f);
            }
        }
        else
        {
            return null;
        }
    }

    public static EnvironmentalModifier handleDimensions(EntityPlayer player)
    {

        int dimension = player.world.provider.getDimension();
        if (dimension == 0)
        {
            return null;
        }
        else if (dimension == -1)
        {
            // It's Hell. It outta be hawt!
            return new EnvironmentalModifier("in_hell", 12f, 6f);
        }
        else if (dimension == 1)
        {
            // It's the End. It outta be cold!
            return new EnvironmentalModifier("in_end", -12f, 6f);
        }
        return null;
    }

    public static EnvironmentalModifier handleRain(EntityPlayer player)
    {
        if (player.world.provider.getDimensionType() == DimensionType.OVERWORLD)
        {
            if (player.world.isRaining())
            {
                if (getSkylight(player) < 15)
                {
                    return new EnvironmentalModifier("rain", -2f, 0.1f);
                }
                else
                {
                    return new EnvironmentalModifier("rain", -4f, 0.3f);
                }
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    public static EnvironmentalModifier handleSprinting(EntityPlayer player)
    {
        if (player.isSprinting())
        {
            return new EnvironmentalModifier("sprint", 2f, 0.3f);
        }
        else
        {
            return null;
        }
    }

    public static EnvironmentalModifier handleUnderground(EntityPlayer player)
    {
        if (player.world.provider.getDimensionType() == DimensionType.OVERWORLD){
            if (player.world.getLight(player.getPosition()) < 1 && player.getPosition().getY() < 135)
            {
                return new EnvironmentalModifier("underground", -6f, 0.2f);
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    public static EnvironmentalModifier handleShade(EntityPlayer player)
    {

        int light = getSkylight(player);
        light = Math.max(12, light);
        float temp = getEnvironmentTemperature(player);
        float avg = TemperatureCapability.AVERAGE;
        float coverage = (1f - (float) light / 15f) + 0.5f;
        if (light < 15 && temp > avg)
        {
            return new EnvironmentalModifier("shade", -Math.abs(avg - temp) * coverage, 0f);
        }
        else
        {
            return null;
        }
    }

    public static EnvironmentalModifier handleCozy(EntityPlayer player)
    {
        int skyLight = getSkylight(player);
        skyLight = Math.max(11, skyLight);
        int blockLight = getBlockLight(player);
        float temp = getEnvironmentTemperature(player);
        float avg = TemperatureCapability.AVERAGE;
        float coverage = (1f - (float) skyLight / 15f) + 0.4f;
        if (skyLight < 14 && blockLight > 1 && temp < avg - 2)
        {
            return new EnvironmentalModifier("cozy", Math.abs(avg - 2 - temp) * coverage, 0f);
        }
        else
        {
            return null;
        }
    }

    public static EnvironmentalModifier handleThirst(EntityPlayer player)
    {
        if (player.getFoodStats() instanceof IFoodStatsTFC)
        {
            IFoodStatsTFC stats = (IFoodStatsTFC) player.getFoodStats();
            if (getEnvironmentTemperature(player) > TemperatureCapability.AVERAGE + 3 && stats.getThirst() > 80f)
            {
                return new EnvironmentalModifier("well_hidrated", -2f, 0f);
            }
        }
        return null;
    }

    public static EnvironmentalModifier handleFood(EntityPlayer player)
    {
        if (getEnvironmentTemperature(player) < TemperatureCapability.AVERAGE - 3 && player.getFoodStats().getFoodLevel() > 16)
        {
            return new EnvironmentalModifier("well_fed", 2f, 0f);
        }
        return null;
    }

    public static EnvironmentalModifier handleDiet(EntityPlayer player)
    {
        if (player.getFoodStats() instanceof IFoodStatsTFC)
        {
            IFoodStatsTFC stats = (IFoodStatsTFC) player.getFoodStats();
            if (getEnvironmentTemperature(player) < TemperatureCapability.AVERAGE - 7)
            {
                float grainLevel = stats.getNutrition().getNutrient(Nutrient.GRAIN);
                float meatLevel = stats.getNutrition().getNutrient(Nutrient.PROTEIN);
                return new EnvironmentalModifier("nutrients", -4f * grainLevel * meatLevel, 0f);
            }
            if (getEnvironmentTemperature(player) > TemperatureCapability.AVERAGE + 7)
            {
                float fruitLevel = stats.getNutrition().getNutrient(Nutrient.FRUIT);
                float veggieLevel = stats.getNutrition().getNutrient(Nutrient.VEGETABLES);
                return new EnvironmentalModifier("nutrients", 4f * fruitLevel * veggieLevel, 0f);
            }
        }
        return null;
    }
    public static EnvironmentalModifier handlePotionEffects(EntityPlayer player) {
        if(player.isPotionActive(TempEffect.COOL)){
            return new EnvironmentalModifier("cooling_effect", -10F, 0);
        }
        if(player.isPotionActive(TempEffect.WARM)){
            return new EnvironmentalModifier("heating_effect", 10F, 0);
        }
        return null;
    }

    public static int getSkylight(EntityPlayer player)
    {
        BlockPos pos = new BlockPos(player.getPosition());
        BlockPos pos2 = pos.add(0, 1.8, 0);
        return player.world.getLightFor(EnumSkyBlock.SKY, pos2);
    }

    public static int getBlockLight(EntityPlayer player)
    {
        BlockPos pos = new BlockPos(player.getPosition());
        pos.add(0, 1, 0);
        return player.world.getLightFor(EnumSkyBlock.BLOCK, pos);
    }

    public static EnvironmentalModifier handleGeneralTemperature(EntityPlayer player)
    {
        int hour = CalendarTFC.CALENDAR_TIME.getHourOfDay();
        float dayPart = 0f;
        if (hour < 6) dayPart = -4f;
        else if (hour < 12) dayPart = 2f;
        else if (hour < 18) dayPart = 4f;
        else dayPart = -2f;
        return new EnvironmentalModifier("environment", getEnvironmentTemperature(player) + dayPart, getEnvironmentHumidity(player));
    }

    public static void computeModifiers(EntityPlayer player, ModifierStorage modifiers)
    {
        for (IEnvironmentalTemperatureProvider provider : TemperatureRegistry.ENVIRONMENT)
        {
            modifiers.add(provider.getModifier(player));
        }
    }

}
