package mods.immibis.core.impl.texslice;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.data.IMetadataSection;

class SpriteMetadataSection implements IMetadataSection {
	public static final String SECTION_NAME = "immibis.core.sprites";
	
	public int spriteSize;
	public Map<String, SpritePosition> spritePos = new HashMap<String, SpritePosition>();
}
