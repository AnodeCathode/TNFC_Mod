package vazkii.quark.misc.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderParrot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderParrotKoto extends RenderParrot {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/kotobirb.png");

	public RenderParrotKoto(RenderManager manager) {
		super(manager);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityParrot entity) {
		int color = entity.getVariant();
		if(color == 4 && entity.getUniqueID().getLeastSignificantBits() % 20 == 0)
			return TEXTURE;
		
		return super.getEntityTexture(entity);
	}
	
	public static IRenderFactory<EntityParrot> factory() {
		return RenderParrotKoto::new;
	}

}
