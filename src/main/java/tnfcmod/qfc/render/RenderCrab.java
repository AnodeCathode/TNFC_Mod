package tnfcmod.qfc.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import tnfcmod.qfc.model.ModelCrab;
import tnfcmod.qfc.entity.EntityCrab;

import javax.annotation.Nonnull;

public class RenderCrab extends RenderLiving<EntityCrab> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/crab.png");
	
	public static final IRenderFactory<EntityCrab> FACTORY = RenderCrab::new;

	public RenderCrab(RenderManager render) {
		super(render, new ModelCrab(), 0.5F);
	}
	
	@Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityCrab entity) {
        return TEXTURE;
    }
	
}
