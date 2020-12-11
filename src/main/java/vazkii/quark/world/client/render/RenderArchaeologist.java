package vazkii.quark.world.client.render;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.client.layer.LayerArchaeologistHat;
import vazkii.quark.world.entity.EntityArchaeologist;

import javax.annotation.Nonnull;

public class RenderArchaeologist extends RenderLiving<EntityArchaeologist> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/archaeologist.png");
	
	public static final IRenderFactory<EntityArchaeologist> FACTORY = RenderArchaeologist::new;
	
	public RenderArchaeologist(RenderManager manager) {
		super(manager, new ModelVillager(0.0F), 0.5F);
		addLayer(new LayerArchaeologistHat(this));
	}

	@Nonnull
	@Override
	public ModelVillager getMainModel() {
		return (ModelVillager) super.getMainModel();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityArchaeologist entity) {
		return TEXTURE;
	}
	
}
