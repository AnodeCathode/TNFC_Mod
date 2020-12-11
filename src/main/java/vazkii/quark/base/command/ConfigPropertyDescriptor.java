/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 22, 2019, 13:18 AM (EST)]
 */
package vazkii.quark.base.command;

import net.minecraftforge.common.config.Property;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ConfigPropertyDescriptor {
	@Nonnull
	public final String key;

	@Nonnull
	public final Property.Type type;

	public ConfigPropertyDescriptor(@Nonnull String key, @Nonnull Property.Type type) {
		this.key = key;
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigPropertyDescriptor that = (ConfigPropertyDescriptor) o;
		return Objects.equals(key, that.key) &&
				type == that.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, type);
	}
}
