/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 22, 2019, 13:01 AM (EST)]
 */
package vazkii.quark.base.command;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigCategoryDescriptor {
	@Nullable
	public final ConfigCategory parentCategory;

	@Nonnull
	public final Configuration config;

	@Nonnull
	public final String key;

	@Nullable
	public final String parentKey;

	public ConfigCategoryDescriptor(@Nullable ConfigCategory parentCategory, @Nonnull Configuration config, @Nonnull String key, @Nullable String parentKey) {
		this.parentCategory = parentCategory;
		this.config = config;
		this.key = key;
		this.parentKey = parentKey;
	}

	public ConfigCategoryDescriptor(@Nullable ConfigCategory parentCategory, @Nonnull Configuration config, @Nonnull String key) {
		this(parentCategory, config, key, null);
	}

	@Nullable
	public ConfigCategory category() {
		return parentCategory == null ? config.getCategory(key) :
				parentCategory.getChildren().stream()
						.filter((category) -> category.getName().equals(key))
						.findFirst()
						.orElse(null);
	}

	public Set<String> subCategories() {
		return parentCategory == null ? config.getCategoryNames() :
				parentCategory.getChildren().stream()
						.map(ConfigCategory::getName)
						.collect(Collectors.toSet());
	}

	public String getFullKey() {
		return parentKey == null ? key : parentKey + "." + key;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ConfigCategoryDescriptor that = (ConfigCategoryDescriptor) o;
		return Objects.equals(parentCategory, that.parentCategory) &&
				Objects.equals(config, that.config) &&
				Objects.equals(key, that.key) &&
				Objects.equals(parentKey, that.parentKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parentCategory, config, key, parentKey);
	}
}
