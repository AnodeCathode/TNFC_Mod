package vazkii.quark.automation.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import vazkii.quark.automation.feature.PistonsMoveTEs;
import vazkii.quark.base.module.ModuleLoader;

public class PistonTileEntityRenderer {

	public static boolean renderPistonBlock(TileEntityPiston piston, double x, double y, double z, float pTicks) {
		if (!ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class) || piston.getProgress(pTicks) > 1.0F)
			return false;


		IBlockState state = piston.getPistonState();
		Block block = state.getBlock();
		String id = Block.REGISTRY.getNameForObject(block).toString();

		try {
			TileEntity tile = PistonsMoveTEs.getMovement(piston.getWorld(), piston.getPos());
			if(tile == null || PistonsMoveTEs.renderBlacklist.contains(id))
				return false;

			GlStateManager.pushMatrix();
			tile.setWorld(piston.getWorld());
			tile.validate();

			if(tile instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest) tile;
				chest.adjacentChestXPos = null;
				chest.adjacentChestXNeg = null;
				chest.adjacentChestZPos = null;
				chest.adjacentChestZNeg = null;
			}

			EnumFacing facing = null;

			if(state.getPropertyKeys().contains(BlockHorizontal.FACING))
				facing = state.getValue(BlockHorizontal.FACING);
			else if(state.getPropertyKeys().contains(BlockDirectional.FACING))
				facing = state.getValue(BlockDirectional.FACING);

			GlStateManager.translate(x + piston.getOffsetX(pTicks), y + piston.getOffsetY(pTicks), z + piston.getOffsetZ(pTicks));

			if(facing != null) {
				float rotation = 0;
				switch(facing) {
					case NORTH:
						rotation = 180F;
						break;
					case EAST:
						rotation = 90F;
						break;
					case WEST:
						rotation = -90F;
						break;
					default: break;
				}

				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate(rotation, 0, 1, 0);
				GlStateManager.translate(-0.5, -0.5, -0.5);
			}

			RenderHelper.enableStandardItemLighting();
			TileEntityRendererDispatcher.instance.render(tile, 0, 0, 0, pTicks);
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popMatrix();

		} catch(Throwable e) {
			new RuntimeException(id + " can't be rendered for piston TE moving", e).printStackTrace();
			PistonsMoveTEs.renderBlacklist.add(id);
			return false;
		}

		return state.getRenderType() != EnumBlockRenderType.MODEL;
	}

}
