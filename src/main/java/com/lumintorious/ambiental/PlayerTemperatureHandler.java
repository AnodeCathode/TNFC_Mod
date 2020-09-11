package com.lumintorious.ambiental;

import com.ibm.icu.impl.CalendarData;
import com.lumintorious.ambiental.capability.TemperatureCapability;
import com.lumintorious.ambiental.capability.TemperatureSystem;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.ConfigTFC.General.MiscCFG;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.CalendarWorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

@Mod.EventBusSubscriber(modid = TFCAmbiental.MODID)
public class PlayerTemperatureHandler {
	
	@SubscribeEvent
    public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.getObject();

                ResourceLocation loc = new ResourceLocation(TFCAmbiental.MODID, "temperature");

                //Each player should have their own instance for each stat, as associated values may vary
                if (!event.getCapabilities().containsKey(loc))
                    event.addCapability(loc, new TemperatureCapability(player));
        }
    }

	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent  event) {
		if(event.getEntity().world.isRemote) {
			return;
		}
		if(!(event.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}

		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		if(player.isCreative()) {
			return;
		}
		TemperatureSystem temp = TemperatureSystem.getTemperatureFor(player);
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if(stack != null) {
			if(stack.getItem().getRegistryName().toString().equals("tfc:wand")) {
				temp.say(temp);
			}
		}
		temp.update();
	}
}
