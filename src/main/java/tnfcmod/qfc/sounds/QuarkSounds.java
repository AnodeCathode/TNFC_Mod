/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 15, 2019, 00:50 AM (EST)]
 */
package tnfcmod.qfc.sounds;

import net.minecraft.util.SoundEvent;

import vazkii.arl.util.ProxyRegistry;

public class QuarkSounds {
	public static final SoundEvent ENTITY_FROG_WEDNESDAY = new ModSoundEvent("entity.frog.wednesday");
	public static final SoundEvent ENTITY_FROG_HURT = new ModSoundEvent("entity.frog.hurt");
	public static final SoundEvent ENTITY_FROG_DIE = new ModSoundEvent("entity.frog.die");
	public static final SoundEvent ENTITY_FROG_IDLE = new ModSoundEvent("entity.frog.idle");
	public static final SoundEvent ENTITY_FROG_JUMP = new ModSoundEvent("entity.frog.jump");


	public static void init() {
		for (ModSoundEvent event : ModSoundEvent.allEvents)
			ProxyRegistry.register(event);
	}
}
