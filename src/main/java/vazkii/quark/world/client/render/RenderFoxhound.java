/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 13:30 AM (EST)]
 */
package vazkii.quark.world.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.world.client.layer.LayerFoxhoundCollar;
import vazkii.quark.world.client.model.ModelFoxhound;
import vazkii.quark.world.entity.EntityFoxhound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderFoxhound extends RenderLiving<EntityFoxhound> {
	private static final ResourceLocation FOXHOUND_IDLE = new ResourceLocation(LibMisc.MOD_ID,
			"textures/entity/foxhound_idle.png");
	private static final ResourceLocation FOXHOUND_ANGRY = new ResourceLocation(LibMisc.MOD_ID,
			"textures/entity/foxhound_angry.png");
	private static final ResourceLocation FOXHOUND_SLEEPING = new ResourceLocation(LibMisc.MOD_ID,
			"textures/entity/foxhound_sleeping.png");

	public RenderFoxhound(RenderManager render) {
		super(render, new ModelFoxhound(), 0.5F);
		addLayer(new LayerFoxhoundCollar(this));
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityFoxhound entity) {
		return entity.isSleeping() ? FOXHOUND_SLEEPING : (entity.isAngry() ? FOXHOUND_ANGRY : FOXHOUND_IDLE);
	}
}
