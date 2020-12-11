package vazkii.quark.world.client.model;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.entity.EntityCrab;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ModelCrab extends ModelBase {
	
	private final ModelRenderer group;
	private final ModelRenderer leftThigh1;
	private final ModelRenderer leftLeg1;
	private final ModelRenderer leftThigh2;
	private final ModelRenderer leftLeg2;
	private final ModelRenderer leftThigh3;
	private final ModelRenderer leftLeg3;
	private final ModelRenderer leftThigh4;
	private final ModelRenderer leftLeg4;
	private final ModelRenderer rightThigh1;
	private final ModelRenderer rightLeg1;
	private final ModelRenderer rightThigh2;
	private final ModelRenderer rightLeg2;
	private final ModelRenderer rightThigh3;
	private final ModelRenderer rightLeg3;
	private final ModelRenderer rightThigh4;
	private final ModelRenderer rightLeg4;
	private final ModelRenderer leftArm;
	private final ModelRenderer leftForearm;
	private final ModelRenderer leftPincers;
	private final ModelRenderer leftUpperClaw;
	private final ModelRenderer leftLowerClaw;
	private final ModelRenderer rightArm;
	private final ModelRenderer rightForearm;
	private final ModelRenderer rightPincers;
	private final ModelRenderer rightUpperClaw;
	private final ModelRenderer rightLowerClaw;
	
	private final List<Runnable> resetFunctions;
	private final Set<ModelRenderer> leftLegs;
	private final Set<ModelRenderer> rightLegs;

	public ModelCrab() {
		resetFunctions = new LinkedList<>();
		textureWidth = 32;
		textureHeight = 32;

		group = new ModelRenderer(this);
		group.setRotationPoint(0.0F, 18.0F, 0.0F);
		setRotationAngle(group, 0F, 0F, 0F);
		group.cubeList.add(new ModelBox(group, 0, 0, -4.0F, -2.0F, -4.0F, 8, 4, 8, 0.0F, false));
		group.cubeList.add(new ModelBox(group, 0, 0, -2.0F, -4.0F, -4.0F, 1, 2, 1, 0.0F, false));
		group.cubeList.add(new ModelBox(group, 0, 0, 1.0F, -4.0F, -4.0F, 1, 2, 1, 0.0F, false));

		leftThigh1 = new ModelRenderer(this);
		leftThigh1.setRotationPoint(-4.0F, 1.0F, -3.0F);
		setRotationAngle(leftThigh1, 0.0F, -0.5236F, 0.0F);
		group.addChild(leftThigh1);
		leftThigh1.cubeList.add(new ModelBox(leftThigh1, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		leftLeg1 = new ModelRenderer(this);
		leftLeg1.setRotationPoint(-4.0F, 1.0F, 0.0F);
		setRotationAngle(leftLeg1, 0.0F, 0.0F, 0.2618F);
		leftThigh1.addChild(leftLeg1);
		leftLeg1.cubeList.add(new ModelBox(leftLeg1, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		leftThigh2 = new ModelRenderer(this);
		leftThigh2.setRotationPoint(-4.0F, 1.0F, -1.0F);
		setRotationAngle(leftThigh2, 0.0F, -0.2618F, 0.0F);
		group.addChild(leftThigh2);
		leftThigh2.cubeList.add(new ModelBox(leftThigh2, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		leftLeg2 = new ModelRenderer(this);
		leftLeg2.setRotationPoint(-4.0F, 1.0F, 0.0F);
		setRotationAngle(leftLeg2, 0.0F, 0.0F, 0.5236F);
		leftThigh2.addChild(leftLeg2);
		leftLeg2.cubeList.add(new ModelBox(leftLeg2, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		leftThigh3 = new ModelRenderer(this);
		leftThigh3.setRotationPoint(-4.0F, 1.0F, 1.0F);
		setRotationAngle(leftThigh3, 0.0F, 0.2618F, 0.0F);
		group.addChild(leftThigh3);
		leftThigh3.cubeList.add(new ModelBox(leftThigh3, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		leftLeg3 = new ModelRenderer(this);
		leftLeg3.setRotationPoint(-4.0F, 1.0F, 0.0F);
		setRotationAngle(leftLeg3, 0.0F, 0.0F, 0.5236F);
		leftThigh3.addChild(leftLeg3);
		leftLeg3.cubeList.add(new ModelBox(leftLeg3, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		leftThigh4 = new ModelRenderer(this);
		leftThigh4.setRotationPoint(-4.0F, 1.0F, 3.0F);
		setRotationAngle(leftThigh4, 0.0F, 0.5236F, 0.0F);
		group.addChild(leftThigh4);
		leftThigh4.cubeList.add(new ModelBox(leftThigh4, 0, 12, -4.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		leftLeg4 = new ModelRenderer(this);
		leftLeg4.setRotationPoint(-4.0F, 1.0F, 0.0F);
		setRotationAngle(leftLeg4, 0.0F, 0.0F, 0.2618F);
		leftThigh4.addChild(leftLeg4);
		leftLeg4.cubeList.add(new ModelBox(leftLeg4, 4, 0, 0.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		rightThigh1 = new ModelRenderer(this);
		rightThigh1.setRotationPoint(4.0F, 1.0F, -3.0F);
		setRotationAngle(rightThigh1, 0.0F, 0.5236F, 0.0F);
		group.addChild(rightThigh1);
		rightThigh1.cubeList.add(new ModelBox(rightThigh1, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		rightLeg1 = new ModelRenderer(this);
		rightLeg1.setRotationPoint(4.0F, 1.0F, 0.0F);
		setRotationAngle(rightLeg1, 0.0F, 0.0F, -0.2618F);
		rightThigh1.addChild(rightLeg1);
		rightLeg1.cubeList.add(new ModelBox(rightLeg1, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		rightThigh2 = new ModelRenderer(this);
		rightThigh2.setRotationPoint(4.0F, 1.0F, -1.0F);
		setRotationAngle(rightThigh2, 0.0F, 0.2618F, 0.0F);
		group.addChild(rightThigh2);
		rightThigh2.cubeList.add(new ModelBox(rightThigh2, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		rightLeg2 = new ModelRenderer(this);
		rightLeg2.setRotationPoint(4.0F, 1.0F, 0.0F);
		setRotationAngle(rightLeg2, 0.0F, 0.0F, -0.5236F);
		rightThigh2.addChild(rightLeg2);
		rightLeg2.cubeList.add(new ModelBox(rightLeg2, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		rightThigh3 = new ModelRenderer(this);
		rightThigh3.setRotationPoint(4.0F, 1.0F, 1.0F);
		setRotationAngle(rightThigh3, 0.0F, -0.2618F, 0.0F);
		group.addChild(rightThigh3);
		rightThigh3.cubeList.add(new ModelBox(rightThigh3, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		rightLeg3 = new ModelRenderer(this);
		rightLeg3.setRotationPoint(4.0F, 1.0F, 0.0F);
		setRotationAngle(rightLeg3, 0.0F, 0.0F, -0.5236F);
		rightThigh3.addChild(rightLeg3);
		rightLeg3.cubeList.add(new ModelBox(rightLeg3, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		rightThigh4 = new ModelRenderer(this);
		rightThigh4.setRotationPoint(4.0F, 1.0F, 3.0F);
		setRotationAngle(rightThigh4, 0.0F, -0.5236F, 0.0F);
		group.addChild(rightThigh4);
		rightThigh4.cubeList.add(new ModelBox(rightThigh4, 0, 12, 0.0F, -1.0F, -1.0F, 4, 2, 2, 0.0F, false));

		rightLeg4 = new ModelRenderer(this);
		rightLeg4.setRotationPoint(4.0F, 1.0F, 0.0F);
		setRotationAngle(rightLeg4, 0.0F, 0.0F, -0.2618F);
		rightThigh4.addChild(rightLeg4);
		rightLeg4.cubeList.add(new ModelBox(rightLeg4, 4, 0, -1.0F, -2.0F, -0.5F, 1, 6, 1, 0.0F, false));

		leftArm = new ModelRenderer(this);
		leftArm.setRotationPoint(-2.0F, 1.0F, -4.0F);
		setRotationAngle(leftArm, -0.5236F, 0.5236F, 0.0F);
		group.addChild(leftArm);
		leftArm.cubeList.add(new ModelBox(leftArm, 24, 0, -1.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F, false));

		leftForearm = new ModelRenderer(this);
		leftForearm.setRotationPoint(-1.0F, 0.0F, -4.0F);
		leftArm.addChild(leftForearm);
		leftForearm.cubeList.add(new ModelBox(leftForearm, 12, 12, -0.5F, -0.5F, -2.0F, 2, 2, 4, 0.0F, false));

		leftPincers = new ModelRenderer(this);
		leftPincers.setRotationPoint(-1.0F, 0.0F, -4.0F);
		leftArm.addChild(leftPincers);

		leftUpperClaw = new ModelRenderer(this);
		leftUpperClaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		setRotationAngle(leftUpperClaw, -0.2618F, 0.0F, 0.0F);
		leftPincers.addChild(leftUpperClaw);
		leftUpperClaw.cubeList.add(new ModelBox(leftUpperClaw, 12, 18, 0.0F, -1.0F, -4.0F, 1, 2, 4, 0.1F, false));

		leftLowerClaw = new ModelRenderer(this);
		leftLowerClaw.setRotationPoint(0.0F, 0.5F, 0.0F);
		leftPincers.addChild(leftLowerClaw);
		setRotationAngle(leftLowerClaw, 0.0F, 0.0F, 0.0F);
		leftLowerClaw.cubeList.add(new ModelBox(leftLowerClaw, 12, 24, 0.0F, 0.5F, -4.0F, 1, 1, 4, 0.0F, false));

		rightArm = new ModelRenderer(this);
		rightArm.setRotationPoint(2.0F, 1.0F, -4.0F);
		setRotationAngle(rightArm, -0.5236F, -0.5236F, 0.0F);
		group.addChild(rightArm);
		rightArm.cubeList.add(new ModelBox(rightArm, 24, 0, 0.0F, 0.0F, -2.0F, 1, 1, 2, 0.0F, false));

		rightForearm = new ModelRenderer(this);
		rightForearm.setRotationPoint(1.0F, 0.0F, -4.0F);
		rightArm.addChild(rightForearm);
		rightForearm.cubeList.add(new ModelBox(rightForearm, 12, 12, -1.5F, -0.5F, -2.0F, 2, 2, 4, 0.0F, false));

		rightPincers = new ModelRenderer(this);
		rightPincers.setRotationPoint(-5.0F, 0.0F, -4.0F);
		rightArm.addChild(rightPincers);

		rightUpperClaw = new ModelRenderer(this);
		rightUpperClaw.setRotationPoint(6.0F, 0.0F, 0.0F);
		setRotationAngle(rightUpperClaw, -0.2618F, 0.0F, 0.0F);
		rightPincers.addChild(rightUpperClaw);
		rightUpperClaw.cubeList.add(new ModelBox(rightUpperClaw, 12, 18, -1.0F, -1.0F, -4.0F, 1, 2, 4, 0.1F, false));

		rightLowerClaw = new ModelRenderer(this);
		rightLowerClaw.setRotationPoint(6.0F, 0.5F, 0.0F);
		rightPincers.addChild(rightLowerClaw);
		setRotationAngle(rightLowerClaw, 0.0F, 0.0F, 0.0F);
		rightLowerClaw.cubeList.add(new ModelBox(rightLowerClaw, 12, 24, -1.0F, 0.5F, -4.0F, 1, 1, 4, 0.0F, false));
		
		leftLegs = ImmutableSet.of(leftLeg1, leftLeg2, leftLeg3, leftLeg4);
		rightLegs = ImmutableSet.of(rightLeg1, rightLeg2, rightLeg3, rightLeg4);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		resetModel();
		
		leftLeg1.rotateAngleZ = 0.2618F + (-1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		leftLeg2.rotateAngleZ = 0.5236F + (-1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		leftLeg3.rotateAngleZ = 0.5236F + (-1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		leftLeg4.rotateAngleZ = 0.2618F + (-1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		rightLeg1.rotateAngleZ = -0.2618F + (1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		rightLeg2.rotateAngleZ = -0.5236F + (1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		rightLeg3.rotateAngleZ = -0.5236F + (1 + MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI)) * 0.7F * limbSwingAmount;
		rightLeg4.rotateAngleZ = -0.2618F + (1 + MathHelper.cos(limbSwing * 0.6662F)) * 0.7F * limbSwingAmount;
		
		if(entityIn instanceof EntityCrab && ((EntityCrab) entityIn).isRaving()) {
			float crabRaveBPM = 125F / 4;
			float freq = (20F / crabRaveBPM);
			float tick = ageInTicks * freq;
			float sin = (float) (Math.sin(tick) * 0.5 + 0.5);
			
			float legRot = sin * 1.4F;
			leftLegs.forEach(l -> l.rotateAngleZ = legRot);
			rightLegs.forEach(l -> l.rotateAngleZ = -legRot);
			
			float maxHeight = 0.15F;
			float horizontalOff = 0.2F;
			group.offsetY = sin * maxHeight;
			
			float slowSin = (float) Math.sin(tick / 2);
			group.offsetX = slowSin * horizontalOff;
			
			float armRot = sin * 0.5F - 1.2F;
			leftArm.rotateAngleX = armRot;
			rightArm.rotateAngleX = armRot;
			
			float pincerRot = sin * -0.3F;
			leftLowerClaw.rotateAngleX = pincerRot;
			rightLowerClaw.rotateAngleX = pincerRot;
		}
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		EntityCrab crab = (EntityCrab) entity;

		GlStateManager.pushMatrix();
		float sizeModifier = crab.getSizeModifier();

		if(isChild) 
			sizeModifier /= 2;

		GlStateManager.translate(0, 1.5 - sizeModifier * 1.5, 0);
		GlStateManager.scale(sizeModifier, sizeModifier, sizeModifier);
		group.render(f5);
		GlStateManager.popMatrix();
	}
	
	private void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		float offX = modelRenderer.offsetX;
		float offY = modelRenderer.offsetY;
		float offZ = modelRenderer.offsetZ;
		
		resetFunctions.add(() -> {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
			
			modelRenderer.offsetX = offX;
			modelRenderer.offsetY = offY;
			modelRenderer.offsetZ = offZ;
		});
	}
	
	private void resetModel() {
		resetFunctions.forEach(Runnable::run);
	}
	
}
