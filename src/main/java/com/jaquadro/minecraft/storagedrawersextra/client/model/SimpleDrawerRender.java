package com.jaquadro.minecraft.storagedrawersextra.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.render.ChamRenderState;
import com.jaquadro.minecraft.chameleon.render.helpers.ModularBoxRenderer;
import com.jaquadro.minecraft.chameleon.resources.IconRegistry;
import com.jaquadro.minecraft.storagedrawers.api.storage.EnumBasicDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockStandardDrawers;
import com.jaquadro.minecraft.storagedrawers.block.dynamic.StatusModelData;
import com.jaquadro.minecraft.storagedrawersextra.block.BlockExtraDrawers;
import com.jaquadro.minecraft.storagedrawersextra.block.EnumVariant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class SimpleDrawerRender
{
    private final ChamRender renderHelper;
    private final ModularBoxRenderer boxRenderer;

    private double depth;
    private double trimWidth;
    private double trimDepth;
    private EnumBasicDrawer blockInfo;

    SimpleDrawerRender (ChamRender renderer) {
        this.renderHelper = renderer;
        this.boxRenderer = new ModularBoxRenderer(renderer);
    }

    private void start (IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction) {
        BlockExtraDrawers block = (BlockExtraDrawers) state.getBlock();
        StatusModelData status = block.getStatusInfo(state);
        blockInfo = state.getValue(BlockStandardDrawers.BLOCK);

        depth = blockInfo.isHalfDepth() ? .5 : 0;
        trimWidth = .0625;
        trimDepth = status.getFrontDepth() / 16f;

        boxRenderer.setUnit(trimWidth);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);

        renderHelper.state.setRotateTransform(ChamRender.ZNEG, direction.getIndex());
        renderHelper.state.setUVRotation(ChamRender.YPOS, ChamRenderState.ROTATION_BY_FACE_FACE[ChamRender.ZNEG][direction.getIndex()]);
    }

    private void end () {
        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(ChamRender.YPOS);
    }

    public void render (IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing direction, EnumVariant variant) {
        start(world, state, pos, direction);

        IconRegistry iconRegistry = Chameleon.instance.iconRegistry;
        BlockExtraDrawers block = (BlockExtraDrawers) state.getBlock();
        boolean halfBlock = block.isHalfDepth(state);

        TextureAtlasSprite textureTrim = iconRegistry.getIcon(TextureFace.TRIM.getLocation(variant));
        TextureAtlasSprite textureBack = iconRegistry.getIcon(TextureFace.SIDE.getLocation(variant));
        TextureAtlasSprite textureSide = textureBack;
        TextureAtlasSprite textureTop = textureBack;
        if (halfBlock) {
            textureTop = iconRegistry.getIcon(TextureFace.SIDE_H.getLocation(variant));
            textureSide = iconRegistry.getIcon(TextureFace.SIDE_V.getLocation(variant));
        }

        renderHelper.targetFaceGroup(true);

        boxRenderer.setUnit(trimWidth);
        boxRenderer.setCutIcon(textureTrim);
        boxRenderer.setExteriorIcon(textureBack, ChamRender.FACE_ZPOS.ordinal());
        boxRenderer.setExteriorIcon(textureSide, ChamRender.FACE_XNEG.ordinal());
        boxRenderer.setExteriorIcon(textureSide, ChamRender.FACE_XPOS.ordinal());
        boxRenderer.setExteriorIcon(textureTop, ChamRender.FACE_YNEG.ordinal());
        boxRenderer.setExteriorIcon(textureTop, ChamRender.FACE_YPOS.ordinal());
        boxRenderer.renderExterior(world, state, pos, 0, 0, depth, 1, 1, 1, 0, ModularBoxRenderer.CUT_ZNEG);

        renderHelper.targetFaceGroup(false);

        TextureAtlasSprite textureFront = textureBack;
        switch (blockInfo) {
            case FULL1:
                textureFront = iconRegistry.getIcon(TextureFace.FRONT_1.getLocation(variant));
                break;
            case FULL2:
            case HALF2:
                textureFront = iconRegistry.getIcon(TextureFace.FRONT_2.getLocation(variant));
                break;
            case FULL4:
            case HALF4:
                textureFront = iconRegistry.getIcon(TextureFace.FRONT_4.getLocation(variant));
                break;
        }

        float depth = halfBlock ? .5f : 0;

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(textureTrim);
        boxRenderer.setInteriorIcon(textureFront, ChamRender.FACE_ZPOS.ordinal());
        boxRenderer.renderInterior(world, state, pos, trimWidth, trimWidth, depth, 1 - trimWidth, 1 - trimWidth, depth + trimDepth, 0, ModularBoxRenderer.CUT_ZNEG);

        end();
    }
}
