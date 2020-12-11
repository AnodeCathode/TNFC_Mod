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
import java.util.List;
import java.util.Map;

public interface IModule extends Comparable<IModule> {

	String getName();

	boolean isEnabled();

	Map<String, ? extends IFeature> getFeatures();

	List<? extends IFeature> getEnabledFeatures();

	@Override
	default int compareTo(@Nonnull IModule o) {
		return getName().compareTo(o.getName());
	}
}
