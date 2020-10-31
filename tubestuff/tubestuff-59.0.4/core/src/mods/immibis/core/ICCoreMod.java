package mods.immibis.core;

import java.util.Map;

import mods.immibis.core.impl.MultiInterfaceClassTransformer;
import mods.immibis.core.impl.TraitTransformer;
import mods.immibis.core.multipart.MultipartCoreHookTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(1500)
public class ICCoreMod implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
			MultiInterfaceClassTransformer.class.getName(),
			TraitTransformer.class.getName(),
			MultipartCoreHookTransformer.class.getName(),
		};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
