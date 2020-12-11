package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.EntityStoneling;

public class ModelStoneling extends ModelBase {

	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer arm_right;
	private final ModelRenderer arm_left;
	private final ModelRenderer leg_right;
	private final ModelRenderer leg_left;

	public ModelStoneling() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 14.0F, 0.0F);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		body.addChild(head);
		head.cubeList.add(new ModelBox(head, 0, 0, -3.0F, -2.0F, -3.0F, 6, 8, 6, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 8, 24, -1.0F, -4.0F, -5.0F, 2, 4, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 16, 20, -1.0F, 6.0F, -3.0F, 2, 2, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 0, 24, -1.0F, -4.0F, 3.0F, 2, 4, 2, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 16, 24, -1.0F, -4.0F, -3.0F, 2, 2, 6, 0.0F, false));
		head.cubeList.add(new ModelBox(head, 24, 20, -1.0F, -4.0F, -1.0F, 2, 2, 2, 0.0F, false));

		arm_right = new ModelRenderer(this);
		arm_right.setRotationPoint(-3.0F, 2.0F, 0.0F);
		setRotationAngle(arm_right, 3.1416F, 0.0F, 0.0F);
		body.addChild(arm_right);
		arm_right.cubeList.add(new ModelBox(arm_right, 0, 14, -2.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));

		arm_left = new ModelRenderer(this);
		arm_left.setRotationPoint(3.0F, 2.0F, 0.0F);
		setRotationAngle(arm_left, 3.1416F, 0.0F, 0.0F);
		body.addChild(arm_left);
		arm_left.cubeList.add(new ModelBox(arm_left, 8, 14, 0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F, false));

		leg_right = new ModelRenderer(this);
		leg_right.setRotationPoint(-2.0F, 4.0F, 0.0F);
		body.addChild(leg_right);
		leg_right.cubeList.add(new ModelBox(leg_right, 16, 14, -1.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, false));

		leg_left = new ModelRenderer(this);
		leg_left.setRotationPoint(1.0F, 4.0F, 0.0F);
		body.addChild(leg_left);
		leg_left.cubeList.add(new ModelBox(leg_left, 24, 14, 0.0F, 2.0F, -1.0F, 2, 4, 2, 0.0F, false));
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		leg_right.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
		leg_left.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
		
		EntityStoneling stoneling = (EntityStoneling) entityIn;
		ItemStack carry = stoneling.getCarryingItem();
		if(carry.isEmpty() && !stoneling.isBeingRidden()) {
			arm_right.rotateAngleX = 0F;
			arm_left.rotateAngleX = 0F;
		} else {
			arm_right.rotateAngleX = 3.1416F;
			arm_left.rotateAngleX = 3.1416F;
		}
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		body.render(scale);
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
