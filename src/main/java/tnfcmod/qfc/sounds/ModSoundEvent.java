/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 15, 2019, 00:52 AM (EST)]
 */
package tnfcmod.qfc.sounds;

import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.List;

public class ModSoundEvent extends SoundEvent {
	public static final List<ModSoundEvent> allEvents = Lists.newArrayList();

	public ModSoundEvent(String name) {
		this(new ResourceLocation("tnfcmod", name));
	}

	public ModSoundEvent(ResourceLocation soundName) {
		super(soundName);
		setRegistryName(soundName);
		allEvents.add(this);
	}
}
