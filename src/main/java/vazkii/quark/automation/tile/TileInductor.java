/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 15, 2019, 06:03 AM (EST)]
 */
package vazkii.quark.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import vazkii.arl.block.tile.TileMod;

public class TileInductor extends TileMod {
	private int outputSignal;

	public void writeSharedNBT(NBTTagCompound compound) {
		compound.setInteger("OutputSignal", this.outputSignal);
	}

	public void readSharedNBT(NBTTagCompound compound) {
		this.outputSignal = compound.getInteger("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignalIn) {
		this.outputSignal = outputSignalIn;
	}
}
