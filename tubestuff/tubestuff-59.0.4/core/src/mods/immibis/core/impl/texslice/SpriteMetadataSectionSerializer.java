package mods.immibis.core.impl.texslice;

import java.lang.reflect.Type;

import net.minecraft.client.resources.data.IMetadataSectionSerializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

class SpriteMetadataSectionSerializer implements IMetadataSectionSerializer {
	@Override
	public String getSectionName() {
		return SpriteMetadataSection.SECTION_NAME;
	}
	
	@Override
	public SpriteMetadataSection deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		SpriteMetadataSection rv = new SpriteMetadataSection();
		
		JsonObject o = json.getAsJsonObject();
		rv.spriteSize = o.get("spriteSize").getAsInt();
		
		for(JsonElement spriteElement : o.get("sprites").getAsJsonArray()) {
			JsonArray spriteData = spriteElement.getAsJsonArray();
			
			SpritePosition pos;
			
			String name = spriteData.get(0).getAsString();
			
			JsonElement posEl = spriteData.get(1);
			if(posEl.isJsonPrimitive())
				pos = new SpritePosition1D(posEl.getAsInt());
			else
				pos = new SpritePosition2D(posEl.getAsJsonArray().get(0).getAsInt(), posEl.getAsJsonArray().get(1).getAsInt());
			
			if(name.contains("*")) {
				int n = Integer.parseInt(name.substring(name.indexOf('*') + 1));
				name = name.substring(0, name.indexOf('*'));
				
				for(int k = 0; k < n; k++) {
					rv.spritePos.put(name+k, pos);
					pos = pos.increment();
				}
			} else
				rv.spritePos.put(name, pos);
		}
		
		return rv;
	}
}
