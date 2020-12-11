/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 30, 2019, 10:46 AM (EST)]
 */
package vazkii.quark.api.module;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Canceling this event will result in the module being ignored completely.
 */
@Cancelable
public class ModuleLoadedEvent extends Event {

	private final IModule module;

	public ModuleLoadedEvent(IModule module) {
		this.module = module;
	}

	public IModule getModule() {
		return module;
	}

}
