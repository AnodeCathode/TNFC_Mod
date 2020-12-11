package vazkii.quark.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * Implement or provide from a TileEntity to add a callback to when it's moved by a piston.
 *
 * You should not check for TileEntities implementing this.
 * Instead, check if they provide this as a capability.
 */
public interface IPistonCallback {

	@CapabilityInject(IPistonCallback.class)
	Capability<IPistonCallback> CAPABILITY = null;

	static boolean hasCallback(TileEntity tile) {
		return tile.hasCapability(CAPABILITY, null);
	}

	static IPistonCallback getCallback(TileEntity tile) {
		return tile.getCapability(CAPABILITY, null);
	}

	void onPistonMovementStarted();
	default void onPistonMovementFinished() {
		// NO-OP
	}
	
}
