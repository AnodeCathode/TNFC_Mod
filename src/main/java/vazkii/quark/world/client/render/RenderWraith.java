/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 04:42:25 (GMT)]
 */
package vazkii.quark.world.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.client.model.ModelWraith;
import vazkii.quark.world.entity.EntityWraith;

import javax.annotation.Nonnull;

public class RenderWraith extends RenderLiving<EntityWraith> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/wraith.png");

	public static final IRenderFactory<EntityWraith> FACTORY = RenderWraith::new;

	public RenderWraith(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelWraith(), 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityWraith entity) {
		return TEXTURE;
	}

	@Override
	public void doRenderShadowAndFire(@Nonnull Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		// NO-OP
	}

}
