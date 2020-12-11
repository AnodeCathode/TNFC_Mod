package vazkii.quark.world.client.model;


import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import vazkii.arl.item.ModelModArmor;

public class ModelArchaeologistHat extends ModelModArmor {
	
	public final ModelRenderer hat;

	public ModelArchaeologistHat() {
		textureWidth = 64;
		textureHeight = 64;

		hat = new ModelRenderer(this);
		hat.setRotationPoint(0.0F, 0.0F, 0.0F);
		hat.cubeList.add(new ModelBox(hat, 0, 0, -4.0F, -10.0F, -4.0F, 8, 10, 8, 0.6F, false));
		hat.cubeList.add(new ModelBox(hat, 0, 18, -6.0F, -6.0F, -6.0F, 12, 1, 12, 0.0F, false));
	}

	@Override
	public void setModelParts() {
		bipedHead.showModel = false;
		bipedHeadwear = hat;
	}
	
}
