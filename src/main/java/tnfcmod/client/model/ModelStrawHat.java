package tnfcmod.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelStrawHat extends ModelBiped
{
    private ModelRenderer shape1;

    public ModelStrawHat () {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(0F, -8F, 0F);
        this.shape1.cubeList.add(new ModelBox(shape1, 10, 12, -2.0F, -4.0F, -2.0F, 4, 4, 4, 0.0F, false));
        this.shape1.cubeList.add(new ModelBox(shape1, 12, 34, -5.0F, -1.0F, -5.0F, 10, 1, 10, 0.0F, false));

        bipedHead.addChild(this.shape1);


    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }
}