package mods.immibis.core.porting;

import mods.immibis.core.api.porting.SidedProxy;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public abstract class CommonProxy142 extends SidedProxy {
	@Override
	public boolean isWorldCurrent(World w) {
		return DimensionManager.getWorld(w.provider.dimensionId) == w;
	}
}
