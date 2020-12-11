/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 23:50:45 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;

public class LookDownLadders extends Feature {

	public boolean sneakWhileInGui;

	@Override
	public void setupConfig() {
		sneakWhileInGui = loadPropBool("Sneak While In Gui", "Should your character automatically sneak on ladders in GUIs?", true);
	}

	@SubscribeEvent
	public void onPlayerTick(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			if(player.isOnLadder() && !player.isSneaking() && player.moveForward == 0 && player.rotationPitch > 70)
				player.move(MoverType.SELF, 0, -0.2, 0);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onInput(InputUpdateEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (sneakWhileInGui && player.isOnLadder() && Minecraft.getMinecraft().currentScreen != null
				&& !(player.moveForward == 0 && player.rotationPitch > 70)) {
			MovementInput input = event.getMovementInput();
			if (input != null)
				input.sneak = true;
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Look Down Ladders";
	}

}
