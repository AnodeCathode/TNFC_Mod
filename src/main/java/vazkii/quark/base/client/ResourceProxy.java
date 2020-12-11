package vazkii.quark.base.client;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.resources.AbstractResourcePack;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibMisc;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceProxy extends AbstractResourcePack {

	private static final String MINECRAFT = "minecraft";
	private static final Set<String> RESOURCE_DOMAINS = ImmutableSet.of(MINECRAFT);

	private static final String BARE_FORMAT = "assets/" + MINECRAFT + "/%s/%s";
	private static final String OVERRIDE_FORMAT = "/assets/" + LibMisc.MOD_ID + "/%s/overrides/%s";

	private static final Map<String, String> overrides = new HashMap<>();

	public ResourceProxy(File file) {
		super(file);
		overrides.put("pack.mcmeta", "/proxypack.mcmeta");
	}

	public void addResource(String path, String file) {
		String bare = String.format(BARE_FORMAT, path, file);
		String override = String.format(OVERRIDE_FORMAT, path, file);
		overrides.put(bare, override);
	}

	@Nonnull
	@Override
	public Set<String> getResourceDomains() {
		return RESOURCE_DOMAINS;
	}

	@Nonnull
	@Override
	protected InputStream getInputStreamByName(@Nonnull String name) {
		return Quark.class.getResourceAsStream(overrides.get(name));
	}

	@Override
	protected boolean hasResourceName(@Nonnull String name) {
		return overrides.containsKey(name);
	}

	@Override
	protected void logNameNotLowercase(@Nonnull String name) {
		// NO-OP
	}

	@Nonnull
	@Override
	public String getPackName() {
		return "quark-texture-proxy";
	}

}
