/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 16:20:49 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ChickensShedFeathers extends Feature {

	public static boolean chicksDropFeathers;
	public static boolean dropAtLeastOne;
	public static int dropFreq;

	@Override
	public void setupConfig() {
		chicksDropFeathers = loadPropBool("Chicks drop feathers", "", true);
		dropAtLeastOne = loadPropBool("Force at least one feather on kill", "", true);
		dropFreq = loadPropInt("Drop frequency (lower means more)", "", 28000);
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if(event.getEntity().getEntityWorld().isRemote || !(event.getEntity() instanceof EntityChicken))
			return;

		EntityChicken chicken = (EntityChicken) event.getEntity();
		if((chicksDropFeathers || !chicken.isChild()) && chicken.getEntityWorld().rand.nextInt(dropFreq) == 0)
			chicken.dropItem(Items.FEATHER, 1);
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {
		if(!dropAtLeastOne || event.getEntity().getEntityWorld().isRemote || !(event.getEntity() instanceof EntityChicken) || !((EntityChicken)event.getEntity()).isChild() && !chicksDropFeathers)
			return;

		EntityChicken chicken = (EntityChicken) event.getEntity();
		boolean hasFeather = false;

		for(EntityItem item : event.getDrops())
			if(!item.getItem().isEmpty() && item.getItem().getItem().equals(Items.FEATHER)) {
				hasFeather = true;
				break;
			}

		if(!hasFeather)
			event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), chicken.posX, chicken.posY, chicken.posZ, new ItemStack(Items.FEATHER, 1)));
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
