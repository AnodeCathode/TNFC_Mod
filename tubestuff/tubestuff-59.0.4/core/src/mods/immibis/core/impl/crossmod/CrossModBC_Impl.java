package mods.immibis.core.impl.crossmod;

import buildcraft.api.tools.IToolWrench;
import buildcraft.transport.TileGenericPipe;
import mods.immibis.core.api.crossmod.ICrossModBC;
import mods.immibis.core.api.util.Dir;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class CrossModBC_Impl implements ICrossModBC {
	@Override
	public Class<?> getWrenchInterface() {
		return IToolWrench.class;
	}
	
	@Override
	public boolean emitItem(ItemStack stack, TileEntity pipe, TileEntity from) {
		if(!(pipe instanceof TileGenericPipe))
			return false;
		
		ForgeDirection dir;
		if(pipe.xCoord < from.xCoord)
			dir = ForgeDirection.VALID_DIRECTIONS[Dir.NX];
		else if(pipe.xCoord > from.xCoord)
			dir = ForgeDirection.VALID_DIRECTIONS[Dir.PX];
		else if(pipe.yCoord < from.yCoord)
			dir = ForgeDirection.VALID_DIRECTIONS[Dir.NY];
		else if(pipe.yCoord > from.yCoord)
			dir = ForgeDirection.VALID_DIRECTIONS[Dir.PY];
		else if(pipe.zCoord < from.zCoord)
			dir = ForgeDirection.VALID_DIRECTIONS[Dir.NZ];
		else if(pipe.zCoord > from.zCoord)
			dir = ForgeDirection.VALID_DIRECTIONS[Dir.PZ];
		else
			return false;
		
		return ((TileGenericPipe)pipe).injectItem(stack, true, dir == null ? ForgeDirection.UNKNOWN : dir.getOpposite()) > 0;
	}
	
	@Override
	public String getPipeClass(TileEntity te) {
		if(te instanceof TileGenericPipe)
			return ((TileGenericPipe)te).pipe.getClass().getName();
		return null;
	}
}
