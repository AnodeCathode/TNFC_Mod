package mods.immibis.tubestuff;

import mods.immibis.core.RenderUtilsIC;
import mods.immibis.core.api.porting.PortableBlockRenderer;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRenderer implements PortableBlockRenderer {
	@Override
	public void renderInvBlock(RenderBlocks rb, Block block, int meta, int model) {
		if(meta == 6) {
			// block breaker
			Tessellator.instance.startDrawingQuads();
			renderBlockBreaker(rb.overrideBlockTexture, Tessellator.instance, -0.5, -0.5, -0.5, Dir.NX);
			Tessellator.instance.draw();
			
		} else {
			BlockTubestuff.model = 0;
			rb.renderBlockAsItem(block, meta, 1.0f);
			BlockTubestuff.model = model;
		}
	}
	
	private final static int[][] ACT2_ROTATION_LOOKUP = {
		// side:					outputSide:
		// NY  PY  NZ  PZ  NX  PX
		{  -1, -1,  3,  3,  3,  3 }, // NY
		{  -1, -1,  0,  0,  0,  0 }, // PY
		{   0,  0, -1, -1,  2,  1 }, // NZ
		{   3,  3, -1, -1,  1,  2 }, // PZ
		{   1,  2,  2,  1, -1, -1 }, // NX
		{   2,  1,  1,  2, -1, -1 }  // PX
	};
	
	@Override
	public boolean renderWorldBlock(RenderBlocks rb, IBlockAccess w, int x, int y, int z, Block block, int model) {
		int meta = w.getBlockMetadata(x, y, z);
		if(meta == 6) {
			// block breaker
			TileEntity te = w.getTileEntity(x, y, z);
			int facing = Dir.PX;
			if(te instanceof TileBlockBreaker)
				facing = ((TileBlockBreaker)te).facing;
			
			RenderUtilsIC.setBrightness(w, x, y, z);
			renderBlockBreaker(rb.overrideBlockTexture, Tessellator.instance, x, y, z, facing);
			
		} else if(meta != 2 || !SharedProxy.enableBHCAnim()) {
			
			if(meta == BlockTubestuff.META_ACT2 || meta == BlockTubestuff.META_DEPLOYER) {
				int outside = 
					(meta == BlockTubestuff.META_ACT2) ?
						((TileAutoCraftingMk2)w.getTileEntity(x, y, z)).outputFace
					:
						((TileDeployer)w.getTileEntity(x, y, z)).facing;
				rb.uvRotateBottom = ACT2_ROTATION_LOOKUP[outside][Dir.NY];
				rb.uvRotateEast = ACT2_ROTATION_LOOKUP[outside][Dir.PZ];
				rb.uvRotateNorth = ACT2_ROTATION_LOOKUP[outside][Dir.NX];
				rb.uvRotateSouth = ACT2_ROTATION_LOOKUP[outside][Dir.PX];
				rb.uvRotateTop = ACT2_ROTATION_LOOKUP[outside][Dir.PY];
				rb.uvRotateWest = ACT2_ROTATION_LOOKUP[outside][Dir.NZ];
			}
			
			rb.renderStandardBlock(block, x, y, z);
			
			rb.uvRotateBottom = 0;
			rb.uvRotateEast = 0;
			rb.uvRotateNorth = 0;
			rb.uvRotateSouth = 0;
			rb.uvRotateTop = 0;
			rb.uvRotateWest = 0;
		}
		return true;
	}
	
	private static float uMin, uMax, vMin, vMax;
	private static void setTexture(IIcon i) {
		uMin = i.getMinU();
		uMax = i.getMaxU();
		vMin = i.getMinV();
		vMax = i.getMaxV();
	}
	
	private void renderBlockBreaker(IIcon overrideTexture, Tessellator t, double x, double y, double z, int facing) {
		final double h = RenderTileBlockBreaker.HALF_HEIGHT ? 0.5 : 1.0;
		
		final double e = 0.01; // small offset added to inside faces to prevent z-fighting
		
		// bottom
		setTexture(overrideTexture != null ? overrideTexture : BlockTubestuff.iBreakerFrame);
		t.setColorOpaque(255, 255, 255);
		t.setNormal(0.0F, -1.0F, 0.0F);
        t.addVertexWithUV(x   , y, z   , uMin, vMin);
		t.addVertexWithUV(x+1 , y, z   , uMax, vMin);
		t.addVertexWithUV(x+1 , y, z+1 , uMax, vMax);
		t.addVertexWithUV(x   , y, z+1 , uMin, vMax);
		t.setNormal(0.0F, 1.0F, 0.0F);
		t.addVertexWithUV(x   , y+e, z   , uMin, vMin);
		t.addVertexWithUV(x   , y+e, z+1 , uMin, vMax);
		t.addVertexWithUV(x+1 , y+e, z+1 , uMax, vMax);
		t.addVertexWithUV(x+1 , y+e, z   , uMax, vMin);

		// -Z
		setTexture(overrideTexture != null ? overrideTexture : facing == Dir.PX || facing == Dir.NX ? BlockTubestuff.iBreakerFrameX : BlockTubestuff.iBreakerFrame);
		t.setNormal(0.0F, 0.0F, -1.0F);
        t.addVertexWithUV(x+1 , y+h , z, uMin, vMin);
		t.addVertexWithUV(x+1 , y   , z, uMin, vMax);
		t.addVertexWithUV(x   , y   , z, uMax, vMax);
		t.addVertexWithUV(x   , y+h , z, uMax, vMin);
		t.setNormal(0.0F, 0.0F, 1.0F);
		t.addVertexWithUV(x+1 , y+h , z+e, uMin, vMin);
		t.addVertexWithUV(x   , y+h , z+e, uMax, vMin);
		t.addVertexWithUV(x   , y   , z+e, uMax, vMax);
		t.addVertexWithUV(x+1 , y   , z+e, uMin, vMax);
		
		// +Z
		t.setNormal(0.0F, 0.0F, 1.0F);
        t.addVertexWithUV(x+1 , y+h , z+1 , uMin, vMin);
		t.addVertexWithUV(x   , y+h , z+1 , uMax, vMin);
		t.addVertexWithUV(x   , y   , z+1 , uMax, vMax);
		t.addVertexWithUV(x+1 , y   , z+1 , uMin, vMax);
		t.setNormal(0.0F, 0.0F, -1.0F);
		t.addVertexWithUV(x+1 , y+h , z+1-e , uMin, vMin);
		t.addVertexWithUV(x+1 , y   , z+1-e , uMin, vMax);
		t.addVertexWithUV(x   , y   , z+1-e , uMax, vMax);
		t.addVertexWithUV(x   , y+h , z+1-e , uMax, vMin);
		
		// -X
		setTexture(overrideTexture != null ? overrideTexture : facing == Dir.PZ || facing == Dir.NZ ? BlockTubestuff.iBreakerFrameX : BlockTubestuff.iBreakerFrame);
		t.setNormal(-1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(x, y+h , z   , uMin, vMin);
		t.addVertexWithUV(x, y   , z   , uMin, vMax);
		t.addVertexWithUV(x, y   , z+1 , uMax, vMax);
		t.addVertexWithUV(x, y+h , z+1 , uMax, vMin);
		t.setNormal(1.0F, 0.0F, 0.0F);
		t.addVertexWithUV(x+e, y+h , z   , uMin, vMin);
		t.addVertexWithUV(x+e, y+h , z+1 , uMax, vMin);
		t.addVertexWithUV(x+e, y   , z+1 , uMax, vMax);
		t.addVertexWithUV(x+e, y   , z   , uMin, vMax);
		
		// +X
		t.setNormal(1.0F, 0.0F, 0.0F);
        t.addVertexWithUV(x+1 , y+h , z   , uMin, vMin);
		t.addVertexWithUV(x+1 , y+h , z+1 , uMax, vMin);
		t.addVertexWithUV(x+1 , y   , z+1 , uMax, vMax);
		t.addVertexWithUV(x+1 , y   , z   , uMin, vMax);
		t.setNormal(-1.0F, 0.0F, 0.0F);
		t.addVertexWithUV(x+1-e , y+h , z   , uMin, vMin);
		t.addVertexWithUV(x+1-e , y   , z   , uMin, vMax);
		t.addVertexWithUV(x+1-e , y   , z+1 , uMax, vMax);
		t.addVertexWithUV(x+1-e , y+h , z+1 , uMax, vMin);
		
		// top
		setTexture(overrideTexture != null ? overrideTexture : BlockTubestuff.iBreakerFrame);
		t.setNormal(0.0F, 1.0F, 0.0F);
        t.addVertexWithUV(x   , y+h , z   , uMin, vMin);
		t.addVertexWithUV(x   , y+h , z+1 , uMin, vMax);
		t.addVertexWithUV(x+1 , y+h , z+1 , uMax, vMax);
		t.addVertexWithUV(x+1 , y+h , z   , uMax, vMin);
		t.setNormal(0.0F, -1.0F, 0.0F);
		t.addVertexWithUV(x   , y+h-e , z   , uMin, vMin);
		t.addVertexWithUV(x+1 , y+h-e , z   , uMax, vMin);
		t.addVertexWithUV(x+1 , y+h-e , z+1 , uMax, vMax);
		t.addVertexWithUV(x   , y+h-e , z+1 , uMin, vMax);
	}
}