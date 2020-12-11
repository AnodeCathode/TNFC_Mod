/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [27/03/2016, 21:55:50 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class ArmedArmorStands extends Feature {

	@SubscribeEvent
	public void entityConstruct(EntityEvent.EntityConstructing event) {
		if(event.getEntity() instanceof EntityArmorStand) {
			EntityArmorStand stand = (EntityArmorStand) event.getEntity();
			if(!stand.getShowArms())
				setShowArms(stand, true);
		}
	}

	private void setShowArms(EntityArmorStand e, boolean showArms) {
		e.getDataManager().set(EntityArmorStand.STATUS, setBit(e.getDataManager().get(EntityArmorStand.STATUS), 4, showArms));
	}

	private byte setBit(byte status, int bitFlag, boolean value) {
		if (value)
			status = (byte)(status | bitFlag);
		else
			status = (byte)(status & ~bitFlag);

		return status;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
