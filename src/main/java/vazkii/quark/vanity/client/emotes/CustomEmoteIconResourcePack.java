package vazkii.quark.vanity.client.emotes;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.vanity.feature.EmoteSystem;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class CustomEmoteIconResourcePack extends AbstractResourcePack {

	private final List<String> verifiedNames = new ArrayList<>();
	private final List<String> existingNames = new ArrayList<>();

	public CustomEmoteIconResourcePack() {
		super(EmoteSystem.emotesDir);
	}
	
	@Nonnull
	@Override
	public Set<String> getResourceDomains() {
		return ImmutableSet.of(EmoteHandler.CUSTOM_EMOTE_NAMESPACE);
	}
	
	@Nonnull
	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException {
		return getInputStreamByName(location.toString());
	}

	@Nonnull
	@Override
	protected InputStream getInputStreamByName(@Nonnull String name) throws IOException {
		if(name.equals("pack.mcmeta"))
			return Quark.class.getResourceAsStream("/proxypack.mcmeta");
		
		File file = getFile(name);
		if(!file.exists())
			throw new FileNotFoundException(name);
		
		return new FileInputStream(file);
	}

	@Override
	public boolean resourceExists(ResourceLocation location) {
		return hasResourceName(location.toString());
	}
	
	@Override
	protected boolean hasResourceName(@Nonnull String name) {
		if(!verifiedNames.contains(name)) {
			File file = getFile(name);
			if(file.exists())
				existingNames.add(name);
			verifiedNames.add(name);
		}
		
		return existingNames.contains(name);
	}
	
	private File getFile(String name) {
		String filename = name.substring(name.indexOf(":") + 1) + ".png";
		return new File(EmoteSystem.emotesDir, filename);
	}

	@Nonnull
	@Override
	public String getPackName() {
		return "quark-emote-pack";
	}
}
