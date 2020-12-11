/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:14:30 (GMT)]
 */
package vazkii.quark.world.client.model;

import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

public class ModelPirateHat extends ModelModArmor {

	public final ModelRenderer hat;

	public ModelPirateHat() {
		float modelOff = -6F;

		textureWidth = 64;
		textureHeight = 64;
		ModelRenderer hatPart3 = new ModelRenderer(this, 0, 30);
		hatPart3.setRotationPoint(0.0F, 0.0F, 0.0F);
		hatPart3.addBox(-5.0F, -4.0F + modelOff, -3.0F, 1, 4, 8, 0.0F);
		setRotateAngle(hatPart3, -0.08726646259971647F, 0.0F, -0.08726646259971647F);
		hat = new ModelRenderer(this, 0, 0);
		hat.setRotationPoint(0.0F, -8.53F, 0.0F);
		hat.addBox(-5.0F, 0F + modelOff, -5.0F, 10, 1, 10, 0.0F);
		setRotateAngle(hat, 0.05235987755982988F, 0.08726646259971647F, 0.0F);
		ModelRenderer hatPart1 = new ModelRenderer(this, 0, 11);
		hatPart1.setRotationPoint(0.0F, 0.0F, 0.0F);
		hatPart1.addBox(-3.5F, -4.0F + modelOff, -3.5F, 7, 4, 8, 1.0F);
		ModelRenderer hatPart4 = new ModelRenderer(this, 0, 23);
		hatPart4.setRotationPoint(0.0F, 0.0F, 0.0F);
		hatPart4.addBox(-5.0F, -6.4F + modelOff, -5.0F, 10, 6, 1, 0.0F);
		setRotateAngle(hatPart4, 0.08726646259971647F, 0.0F, 0.0F);
		ModelRenderer hatPart2 = new ModelRenderer(this, 0, 30);
		hatPart2.mirror = true;
		hatPart2.setRotationPoint(0.0F, 0.0F, 0.0F);
		hatPart2.addBox(4.0F, -4.0F + modelOff, -3.0F, 1, 4, 8, 0.0F);
		setRotateAngle(hatPart2, -0.08726646259971647F, 0.0F, 0.08726646259971647F);
		hat.addChild(hatPart3);
		hat.addChild(hatPart1);
		hat.addChild(hatPart4);
		hat.addChild(hatPart2);
	}

	@Override
	public void setModelParts() {
		bipedHead.showModel = false;
		bipedHeadwear = hat;
	}

}
