package com.lumintorious.ambiental.capability;

import com.lumintorious.ambiental.TFCAmbientalConfig;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.minecraft.advancements.critereon.VillagerTradeTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider.WorldSleepResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.WorldEvents;
import net.minecraftforge.event.GameRuleChangeEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class TimeExtensionCapability<C> implements ICapabilitySerializable<NBTTagCompound>{

	@CapabilityInject(TimeExtensionCapability.class)
    public static final Capability<ITemperatureCapability> CAPABILITY = null;
	
	public int extensionTicksLeft = -1;
	public boolean isExtending = false;
	public long savedTime = -1;
	
	public World world;
	
	public TimeExtensionCapability(World world) {
		this.world = world;
	}
	
	public static void onSleep(PlayerWakeUpEvent event) {
		World world = event.getEntityLiving().world;
		TimeExtensionCapability cap = (TimeExtensionCapability) world.getCapability(CAPABILITY, null);
		if(cap.isExtending) {
			world.setWorldTime(((world.getWorldTime() / 24000) - 1) * 24000);
			CalendarTFC.INSTANCE.setTimeFromWorldTime(world.getWorldTime());
			cap.stopExtensionFully();	
		}
	}
	
	public static void onGameRuleChange(GameRuleChangeEvent event) {
		World world = event.getServer().getWorld(0);
		TimeExtensionCapability cap = (TimeExtensionCapability) world.getCapability(CAPABILITY, null);
		int players = world.getPlayers(EntityPlayer.class, (x) -> true).size();
		if(players == 0) {
			return;
		}
		if(event.getRuleName().equals("doDaylightCycle")) {
			if(cap.getDaylightCycle() && cap.isExtending) {
				cap.stopExtension();
			}
		}
	}
	
	public static void onWorldTick(WorldTickEvent event) {
//		int players = event.world.getPlayers(EntityPlayer.class, (x) -> true).size();
//		if(players == 0) {
//			return;
//		}
//		TimeExtensionCapability cap = (TimeExtensionCapability) event.world.getCapability(CAPABILITY, null);
//		int dayTick = (int) (event.world.getWorldTime() % 24000);
//		boolean cycle = cap.getDaylightCycle();
//		if(!cap.isExtending && cycle) {
//			int ticks1;
//			if(dayTick == 20 && (ticks1 = TFCAmbientalConfig.TIME_EXTENSION.dawnTicks) > 0) {
//				cap.startExtension(ticks1);
//			}else if(dayTick == 6020 && (ticks1 = TFCAmbientalConfig.TIME_EXTENSION.noonTicks) > 0) {
//				cap.startExtension(ticks1);
//			}else if(dayTick == 12020 && (ticks1 = TFCAmbientalConfig.TIME_EXTENSION.duskTicks) > 0) {
//				cap.startExtension(ticks1);
//			}if(dayTick == 18020 && (ticks1 = TFCAmbientalConfig.TIME_EXTENSION.midnightTicks) > 0) {
//				cap.startExtension(ticks1);
//			}
//		}else if(cap.isExtending) {
//			cap.tickDown();
//		}
	}
	
	public void tickDown() {
		this.extensionTicksLeft--;
		if(world.getWorldTime() != savedTime) {
			this.stopExtensionFully();
			world.setWorldTime(world.getWorldTime() - 2);
			CalendarTFC.INSTANCE.setTimeFromWorldTime(world.getWorldTime());
		}
		if(this.extensionTicksLeft <= 0) {
			this.stopExtensionFully();
		}
	}
	
	public void startExtension(int ticksLeft) {
		this.extensionTicksLeft = ticksLeft;
		this.isExtending = true;
		this.savedTime = world.getWorldTime();
		this.setDaylightCycle(false);
		CalendarTFC.INSTANCE.setDoDaylightCycle();
	}
	
	public void stopExtension() {
		this.isExtending = false;
		this.extensionTicksLeft = -1;
		this.world.setWorldTime(world.getWorldTime() + 1);
		CalendarTFC.INSTANCE.setTimeFromWorldTime(world.getWorldTime());
		CalendarTFC.INSTANCE.setDoDaylightCycle();
	}
	
	public void stopExtensionFully() {
		this.stopExtension();
		this.setDaylightCycle(true);
		CalendarTFC.INSTANCE.setDoDaylightCycle();
	}
	
	public void setDaylightCycle(boolean isOn) {
		world.getGameRules().setOrCreateGameRule("doDaylightCycle", isOn ? "true" : "false");
	}
	
	public boolean getDaylightCycle() {
		return world.getGameRules().getBoolean("doDaylightCycle");
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("isExtending", this.isExtending);
		tag.setInteger("ticksLeft", this.extensionTicksLeft);
		tag.setLong("savedTime", this.savedTime);
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound tag) {
		this.extensionTicksLeft = tag.getInteger("ticksLeft");
		this.savedTime = tag.getLong("savedTime");
		this.isExtending = tag.getBoolean("isExtending");
	}
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability != null && capability == CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CAPABILITY ? (T)(this) : null;
    }

}
