/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 30, 2019, 10:41 AM (EST)]
 */
package vazkii.quark.api.module;

import javax.annotation.Nonnull;

public interface IFeature extends Comparable<IFeature> {

	IModule getModule();

	boolean isEnabled();

	boolean isClient();

	String getName();

	@Override
	default int compareTo(@Nonnull IFeature o) {
		return getName().compareTo(o.getName());
	}
}
