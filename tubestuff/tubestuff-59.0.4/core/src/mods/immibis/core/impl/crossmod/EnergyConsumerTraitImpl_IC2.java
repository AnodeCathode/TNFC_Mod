package mods.immibis.core.impl.crossmod;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import mods.immibis.core.api.traits.IEnergyConsumerTrait;
import mods.immibis.core.api.traits.IEnergyConsumerTraitUser;
import mods.immibis.core.api.traits.TraitClass;
import mods.immibis.core.api.traits.TraitMethod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

@TraitClass(interfaces={IEnergySink.class})
public class EnergyConsumerTraitImpl_IC2 implements IEnergyConsumerTrait, IEnergySink {
	
	private IEnergyConsumerTraitUser tile;
	
	private double UNITS_PER_EU;
	
	private double storage; // in preferred units
	private double max_buffer; // in preferred units
	
	public EnergyConsumerTraitImpl_IC2(Object tile) {
		try {
			this.tile = (IEnergyConsumerTraitUser)tile;
		} catch(ClassCastException e) {
			throw new RuntimeException("Tile '"+tile+"' must implement IEnergyConsumerTraitUser.", e);
		}
		
		// U/EU = U/FT * FT/EU
		// 2.5 FT = 1 EU, so FT/EU = 0.5
		this.UNITS_PER_EU = this.tile.EnergyConsumer_getPreferredUnit().unitsPerFurnaceTick * 0.4;
		
		max_buffer = this.tile.EnergyConsumer_getPreferredBufferSize();
		if(max_buffer < 1000)
			max_buffer = 1000;
	}


	@Override
	public void readFromNBT(NBTTagCompound tag) {
		storage = tag.getDouble("ECTI_IC2_storage_eu") * UNITS_PER_EU;
		max_buffer = tag.getDouble("ECTI_IC2_max_buffer_eu") * UNITS_PER_EU;
		if(max_buffer == 0)
			max_buffer = this.tile.EnergyConsumer_getPreferredBufferSize();
		if(max_buffer < 1000)
			max_buffer = 1000;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("ECTI_IC2_storage_eu", storage / UNITS_PER_EU);
		tag.setDouble("ECTI_IC2_max_buffer_eu", max_buffer / UNITS_PER_EU);
	}


	@TraitMethod
	@Override
	public boolean acceptsEnergyFrom(TileEntity arg0, ForgeDirection arg1) {
		return true;
	}


	@TraitMethod
	@Override
	public double getDemandedEnergy() {
		return Math.max(0, max_buffer - storage) / UNITS_PER_EU;
	}


	@TraitMethod
	@Override
	public int getSinkTier() {
		return 4;
	}


	@TraitMethod
	@Override
	public double injectEnergy(ForgeDirection arg0, double arg1, double arg2) {
		if(storage >= max_buffer)
			return arg1;
		storage += arg1 * UNITS_PER_EU;
		return 0;
	}


	@Override
	public double getStoredEnergy() {
		return storage;
	}


	@Override
	public double useEnergy(double min, double max) {
		if(storage < min)
			return 0;
		
		if(storage > max) {
			storage -= max;
			return max;
		}
		
		double used = storage;
		storage = 0;
		return used;
	}


	@Override
	public void onValidate() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile)tile));
	}


	@Override
	public void onChunkUnload() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)tile));
	}


	@Override
	public void onInvalidate() {
		MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile)tile));
	}
	
	@Override
	public void setStoredEnergy(double amt) {
		storage = amt;
	}
}
