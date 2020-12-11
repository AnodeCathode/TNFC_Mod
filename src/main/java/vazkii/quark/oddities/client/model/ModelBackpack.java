package vazkii.quark.oddities.client.model;

import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

/**
 * quark_backpack - wiiv
 * Created using Tabula 7.0.0
 */
public class ModelBackpack extends ModelModArmor {
	
	private final ModelRenderer straps;
	private final ModelRenderer backpack;
	private final ModelRenderer fitting;

	private final ModelRenderer base;

	public ModelBackpack() {
		base = new ModelRenderer(this);

		straps = new ModelRenderer(this, 24, 0);
		straps.setRotationPoint(0.0F, 0.0F, 0.0F);
		straps.addBox(-4.0F, 0.05F, -3.0F, 8, 8, 5, 0.0F);
		fitting = new ModelRenderer(this, 50, 0);
		fitting.setRotationPoint(0.0F, 0.0F, 0.0F);
		fitting.addBox(-1.0F, 3.0F, 6.0F, 2, 3, 1, 0.0F);
		backpack = new ModelRenderer(this, 0, 0);
		backpack.setRotationPoint(0.0F, 0.0F, 0.0F);
		backpack.addBox(-4.0F, 0.0F, 2.0F, 8, 10, 4, 0.0F);
//		lock = new ModelRenderer(this, 50, 4);
//		lock.setRotationPoint(0.0F, 0.0F, 0.0F);
//		lock.addBox(-2.0F, 4.0F, 6.0F, 4, 3, 2, 0.0F);

		base.addChild(straps);
		base.addChild(backpack);
		base.addChild(fitting);
//		base.addChild(lock);
	}

	@Override
	public void setModelParts() {
		bipedBody = base;
	}
}
