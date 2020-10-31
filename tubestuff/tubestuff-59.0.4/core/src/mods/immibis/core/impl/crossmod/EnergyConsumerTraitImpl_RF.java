package mods.immibis.core.impl.crossmod;

import cofh.api.energy.IEnergyHandler;
import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitClass;
import mods.immibis.core.api.traits.TraitMethod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

@TraitClass(interfaces={IEnergyHandler.class})
public class EnergyConsumerTraitImpl_RF implements IEnergyConsumerTrait, cofh.api.energy.IEnergyHandler {
	
	private IEnergyConsumerTraitUser tile;
	
	private double UNITS_PER_RF;
	private double storage; // in preferred units
	private double max_buffer; // in preferred units
	private int max_buffer_rf; // in RF
	
	public EnergyConsumerTraitImpl_RF(Object tile) {
		try {
			this.tile = (IEnergyConsumerTraitUser)tile;
		} catch(ClassCastException e) {
			throw new RuntimeException("Tile '"+tile+"' must implement IEnergyConsumerTraitUser.", e);
		}
		
		this.UNITS_PER_RF = EnergyUnit.RF.getConversionRate(this.tile.EnergyConsumer_getPreferredUnit());
		
		max_buffer = Math.max(1000 * UNITS_PER_RF, this.tile.EnergyConsumer_getPreferredBufferSize());
		max_buffer_rf = (int)(max_buffer / UNITS_PER_RF);
	}
	
	


	@Override
	public void readFromNBT(NBTTagCompound tag) {
		storage = tag.getDouble("ECTI_RF_storage");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("ECTI_RF_storage", storage);
	}



	@Override
	public double getStoredEnergy() {
		return storage;
	}


	@Override
	public double useEnergy(double min, double max) {
		if(storage < min)
			return 0;
		max = Math.min(max, storage);
		storage -= max;
		return max;
	}


	@Override
	public void onValidate() {
		
	}


	@Override
	public void onChunkUnload() {
		
	}


	@Override
	public void onInvalidate() {
		
	}
	
	@Override
	public void setStoredEnergy(double amt) {
		storage = amt;
	}




	@Override
	@TraitMethod
	public boolean canConnectEnergy(ForgeDirection arg0) {
		return true;
	}

	@Override
	@TraitMethod
	public int extractEnergy(ForgeDirection arg0, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	@TraitMethod
	public int getEnergyStored(ForgeDirection arg0) {
		return (int)(storage / UNITS_PER_RF);
	}

	@Override
	@TraitMethod
	public int getMaxEnergyStored(ForgeDirection arg0) {
		return max_buffer_rf;
	}

	@Override
	@TraitMethod
	public int receiveEnergy(ForgeDirection arg0, int amount, boolean simulate) {
		if(storage >= max_buffer)
			return 0;
		
		if(!simulate)
			storage += amount * UNITS_PER_RF;
		return amount;
	}
}
