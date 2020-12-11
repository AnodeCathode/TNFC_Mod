package vazkii.quark.management.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.management.entity.EntityChestPassenger;

import javax.annotation.Nonnull;

public class RenderChestPassenger extends Render<EntityChestPassenger> {

	protected RenderChestPassenger(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(@Nonnull EntityChestPassenger entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if(!entity.isRiding())
			return;

		Entity riding = entity.getRidingEntity();
		if (riding == null)
			return;

		EntityBoat boat = (EntityBoat) riding;

		float rot = 180F - entityYaw;
		
		ItemStack stack = entity.getChestType();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.375F, z);
		GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
		float timeSinceHit = boat.getTimeSinceHit() - partialTicks;
		float damageTaken = boat.getDamageTaken() - partialTicks;

		if (damageTaken < 0.0F)
			damageTaken = 0.0F;

		if (timeSinceHit > 0.0F)
			GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10.0F * boat.getForwardDirection(), 1.0F, 0.0F, 0.0F);

		GlStateManager.translate(0F, 0.7F - 0.375F, -0.15F);
		if(boat.getPassengers().size() == 1)
			GlStateManager.translate(0F, 0F, 0.6F);	
		
		GlStateManager.scale(1.75F, 1.75F, 1.75F);
		
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);		
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityChestPassenger entity) {
		return null;
	}
	
	public static IRenderFactory<EntityChestPassenger> factory() {
		return RenderChestPassenger::new;
	}


}
