package tnfcmod.qfc.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import tnfcmod.qfc.model.ModelFrog;
import tnfcmod.qfc.entity.EntityFrog;

import javax.annotation.Nonnull;

public class RenderFrog extends RenderLiving<EntityFrog> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("tnfcmod", "textures/entity/frog.png");
	
	public static final IRenderFactory<EntityFrog> FACTORY = RenderFrog::new;
	
	public RenderFrog(RenderManager manager) {
		super(manager, new ModelFrog(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityFrog entity) {
		return TEXTURE;
	}

}
