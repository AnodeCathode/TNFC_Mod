// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12
// Paste this class into your mod and generate all required imports


public class custom_model extends ModelBase {
	private final ModelRenderer bb_main;

	public custom_model() {
		textureWidth = 64;
		textureHeight = 64;

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.cubeList.add(new ModelBox(bb_main, 10, 12, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
		bb_main.cubeList.add(new ModelBox(bb_main, 14, 34, -5.0F, -1.0F, -5.0F, 10, 1, 10, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bb_main.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}