package vazkii.quark.experimental.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import vazkii.quark.experimental.block.FramedBlockCommons;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// referenced from BlockCraftery BakedModelEditable
// https://github.com/EpicSquid/Blockcraftery/blob/master/src/main/java/epicsquid/blockcraftery/model/BakedModelEditable.java
public class FramedBlockModel extends BakedModelWrapper<IBakedModel> {

	public static final Map<String, RetextureData> cache = new HashMap<>();

	private final IModel model;

	public FramedBlockModel(IBakedModel originalModel, IModel model) {
		super(originalModel);
		this.model = model;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel bakedModel = this.originalModel;
		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;
			IBlockState texState = extendedState.getValue(FramedBlockCommons.STATE);
			String cacheId = (texState == null ? "null" : texState.toString()) + "_" + state.toString() + "_" + (side == null ? "null" : side.toString()) + 
					(MinecraftForgeClient.getRenderLayer() == null ? "null" : MinecraftForgeClient.getRenderLayer().toString());

			RetextureData data;

			if(!cache.containsKey(cacheId)) {
				TextureAtlasSprite[] sprites = new TextureAtlasSprite[] { getParticleTexture() };
				int[] tintIndices = new int[] { 0 };
				if(texState != null && texState.getBlock() != Blocks.AIR) {
					IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(texState);
					sprites[0] = model.getParticleTexture();

					List<BakedQuad> texQuads = model.getQuads(texState, side, rand);
					if(texQuads.size() > 0) {
						sprites = new TextureAtlasSprite[texQuads.size()];
						tintIndices = new int[texQuads.size()];
						for(int i = 0; i < texQuads.size(); i++) {
							if(texQuads.get(i).hasTintIndex())
								tintIndices[i] = texQuads.get(i).getTintIndex();
							else
								tintIndices[i] = -1;

							sprites[i] = texQuads.get(i).getSprite();
						}
					}
				}

				data = new RetextureData(sprites, tintIndices);
				cache.put(cacheId, data);
			} else data = cache.get(cacheId);

			if(data != null) {
				IModel retextured = data.retextureModel(model);
				bakedModel = retextured.bake(retextured.getDefaultState(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
			}
		}
		
		return bakedModel.getQuads(state, side, rand);
	}
	
	private static class FramedBlockColor implements IBlockColor {

		@Override
		public int colorMultiplier(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
			if(state instanceof IExtendedBlockState) {
				IExtendedBlockState extendedState = (IExtendedBlockState) state;
				IBlockState texState = extendedState.getValue(FramedBlockCommons.STATE);

			}
				
//			Minecraft.getMinecraft().getBlockColors().colorMultiplier(camoState, worldIn, pos, tintIndex);
			return 0;
		}
		
	}

	private static class RetextureData {

		private static final String[] SIDES = new String[] {
				"down", "up", "north", "south", "west", "east"
		};

		private final TextureAtlasSprite[] sprites;
		private final int[] tintIndices;

		public RetextureData(TextureAtlasSprite[] sprites, int[] tintIndices) {
			this.sprites = sprites;
			this.tintIndices = tintIndices;
		}

		private TextureAtlasSprite getSprite(int side) {
			return sprites[Math.min(sprites.length - 1, side)];
		}

		private int getTint(int side) {
			return tintIndices[Math.min(tintIndices.length - 1, side)];
		}

		private IModel retextureModel(IModel model) {
			Map<String, String> retextureMap = new HashMap<>();
			for(int i = 0; i < SIDES.length; i++)
				retextureMap.put(SIDES[i], getSprite(i).getIconName());

			return model.retexture(ImmutableMap.copyOf(retextureMap));
		}

	}



}
