/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:11:25 (GMT)]
 */
package vazkii.quark.world.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.client.layer.LayerPirateClothes;

public class RenderPirate extends RenderSkeleton {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/pirate.png");

	public static final IRenderFactory<EntitySkeleton> FACTORY = RenderPirate::new;

	public RenderPirate(RenderManager renderManagerIn) {
		super(renderManagerIn);

		addLayer(new LayerPirateClothes(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
		return TEXTURE;
	}

}
