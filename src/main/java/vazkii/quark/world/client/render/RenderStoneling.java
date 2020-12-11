package vazkii.quark.world.client.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.world.client.layer.LayerStonelingItem;
import vazkii.quark.world.client.model.ModelStoneling;
import vazkii.quark.world.entity.EntityStoneling;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderStoneling extends RenderLiving<EntityStoneling> {

	public static final IRenderFactory<EntityStoneling> FACTORY = RenderStoneling::new;
	
	protected RenderStoneling(RenderManager renderManager) {
		super(renderManager, new ModelStoneling(), 0.3F);
		addLayer(new LayerStonelingItem());
	}
	
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityStoneling entity) {
		return entity.getVariant().getTexture();
	}

}
