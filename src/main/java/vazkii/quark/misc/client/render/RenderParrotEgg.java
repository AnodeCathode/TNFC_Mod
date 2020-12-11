package vazkii.quark.misc.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.misc.entity.EntityParrotEgg;
import vazkii.quark.misc.feature.ParrotEggs;

import javax.annotation.Nonnull;

public class RenderParrotEgg extends RenderSnowball<EntityParrotEgg> {

	public RenderParrotEgg(RenderManager renderManagerIn) {
		super(renderManagerIn, ParrotEggs.parrot_egg, Minecraft.getMinecraft().getRenderItem());
	}
	
	@Nonnull
	@Override
	public ItemStack getStackToRender(EntityParrotEgg entityIn) {
		return new ItemStack(ParrotEggs.parrot_egg, 1, entityIn.getColor());
	}
	
	public static IRenderFactory<EntityParrotEgg> factory() {
		return RenderParrotEgg::new;
	}

}
