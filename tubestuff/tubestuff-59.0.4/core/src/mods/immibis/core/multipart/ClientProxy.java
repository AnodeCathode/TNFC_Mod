package mods.immibis.core.multipart;

import java.util.Map;

import mods.immibis.core.api.multipart.ICoverSystem;
import mods.immibis.core.api.multipart.ICoverableTile;
import mods.immibis.core.api.multipart.IPartContainer;
import mods.immibis.core.api.multipart.PartCoordinates;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy {
	// Returns true if anything was rendered
	public static boolean renderMultiparts(RenderBlocks render, IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		
		ICoverSystem ci = te instanceof ICoverableTile ? ((ICoverableTile)te).getCoverSystem() : null;
		IPartContainer imt = te instanceof IPartContainer ? (IPartContainer)te : null;
		
		boolean damageLayer = render.overrideBlockTexture != null;
		
		boolean returnVal = false;
		
		if(!damageLayer) {
			if(imt != null)
				returnVal |= imt.renderPartContainer(render);
		
			if(ci != null)
				returnVal |= ci.renderPartContainer(render);
		}
		else {
			for(Map.Entry<EntityPlayer, PartCoordinates> breaking : MultipartSystem.instance.getBreakingParts()) {
				if(!breaking.getKey().worldObj.isRemote)
					continue;
				
				PartCoordinates pc = breaking.getValue();
				if(pc.x == x && pc.y == y && pc.z == z) {
					if(!pc.isCoverSystemPart && imt != null)
						returnVal |= imt.renderPart(render, pc.part);
					if(pc.isCoverSystemPart && ci != null)
						returnVal |= ci.renderPart(render, pc.part);
				}
			}
		}
		
		return returnVal;
	}
	
	
	
	public ClientProxy() {
		MinecraftForge.EVENT_BUS.register(new MultipartHighlightHandler());
	}
}
