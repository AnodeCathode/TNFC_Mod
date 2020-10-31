package mods.immibis.core.impl.crossmod;

import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitClass;
import net.minecraft.nbt.NBTTagCompound;

@TraitClass(interfaces={})
public class EnergyConsumerTraitImpl_Infinite implements IEnergyConsumerTrait  {
	
	private IEnergyConsumerTraitUser tile;
	
	private double max_buffer;
	
	public EnergyConsumerTraitImpl_Infinite(Object tile) {
		try {
			this.tile = (IEnergyConsumerTraitUser)tile;
		} catch(ClassCastException e) {
			throw new RuntimeException("Tile '"+tile+"' must implement IEnergyConsumerTraitUser.", e);
		}
		
		max_buffer = this.tile.EnergyConsumer_getPreferredBufferSize();
	}

	@Override public void readFromNBT(NBTTagCompound tag) {}
	@Override public void writeToNBT(NBTTagCompound tag) {}
	@Override public double getStoredEnergy() {return Math.max(max_buffer, 1000000);}
	@Override public double useEnergy(double min, double max) {return max;}
	@Override public void onValidate() {}
	@Override public void onChunkUnload() {}
	@Override public void onInvalidate() {}
	@Override public void setStoredEnergy(double amt) {}
}
