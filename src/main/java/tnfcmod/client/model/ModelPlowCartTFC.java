package tnfcmod.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;


import tnfcmod.objects.entities.EntityPlowCartTFC;

public class ModelPlowCartTFC extends ModelBase
{
    private ModelRenderer axis = new ModelRenderer(this, 0, 0);
    private ModelRenderer[] triangle = new ModelRenderer[3];
    private ModelRenderer shaft;
    private ModelRenderer shaftConnector;
    private ModelRenderer[] plowShaftUpper = new ModelRenderer[3];
    private ModelRenderer[] plowShaftLower = new ModelRenderer[3];
    private ModelRenderer plowHandle;
    private ModelRenderer plowHandleGrip;
    private ModelRenderer leftWheel;
    private ModelRenderer rightWheel;

    public ModelPlowCartTFC() {
        this.axis.addBox(-12.5F, 4.0F, 0.0F, 25, 2, 2);
        this.triangle[0] = new ModelRenderer(this, 0, 4);
        this.triangle[0].addBox(-7.5F, -9.0F, 0.0F, 15, 2, 2);
        this.triangle[1] = new ModelRenderer(this, 8, 11);
        this.triangle[1].addBox(-5.0F, -9.0F, 0.5F, 2, 14, 2);
        this.triangle[1].rotateAngleZ = -0.175F;
        this.triangle[2] = new ModelRenderer(this, 8, 11);
        this.triangle[2].addBox(3.0F, -9.0F, 0.5F, 2, 14, 2);
        this.triangle[2].rotateAngleZ = 0.175F;
        this.triangle[2].mirror = true;
        this.shaft = new ModelRenderer(this, 0, 8);
        this.shaft.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaft.rotateAngleY = 1.5707964F;
        this.shaft.rotateAngleZ = -0.07F;
        this.shaft.addBox(0.0F, 0.0F, -8.0F, 20, 2, 1);
        this.shaft.addBox(0.0F, 0.0F, 7.0F, 20, 2, 1);
        this.shaftConnector = new ModelRenderer(this, 0, 27);
        this.shaftConnector.setRotationPoint(0.0F, 0.0F, -14.0F);
        this.shaftConnector.rotateAngleY = 1.5707964F;
        this.shaftConnector.rotateAngleZ = -0.26F;
        this.shaftConnector.addBox(-16.0F, 0.0F, -8.0F, 16, 2, 1);
        this.shaftConnector.addBox(-16.0F, 0.0F, 7.0F, 16, 2, 1);

        int i;
        for(i = 0; i < this.plowShaftUpper.length; ++i) {
            this.plowShaftUpper[i] = new ModelRenderer(this, 56, 0);
            this.plowShaftUpper[i].addBox(-1.0F, -2.0F, -2.0F, 2, 30, 2);
            this.plowShaftUpper[i].setRotationPoint(-3.0F + (float)(3 * i), -7.0F, 0.0F);
            this.plowShaftUpper[i].rotateAngleY = -0.523599F + 0.5235988F * (float)i;
            this.plowShaftLower[i] = new ModelRenderer(this, 42, 4);
            this.plowShaftLower[i].addBox(-1.0F, -0.7F, -0.7F, 2, 10, 2);
            this.plowShaftLower[i].setRotationPoint(0.0F, 28.0F, -1.0F);
            this.plowShaftLower[i].rotateAngleX = 0.7853982F;
            this.plowShaftUpper[i].addChild(this.plowShaftLower[i]);
        }

        this.plowHandle = new ModelRenderer(this, 50, 4);
        this.plowHandle.addBox(-0.5F, 0.0F, -0.5F, 1, 18, 1);
        this.plowHandle.setRotationPoint(0.0F, 33.0F, 5.0F);
        this.plowHandle.rotateAngleX = 1.5707964F;
        this.plowShaftUpper[1].addChild(this.plowHandle);
        this.plowHandleGrip = new ModelRenderer(this, 50, 23);
        this.plowHandleGrip.addBox(-0.5F, 0.0F, -1.0F, 1, 5, 1);
        this.plowHandleGrip.setRotationPoint(0.0F, 32.8F, 21.0F);
        this.plowHandleGrip.rotateAngleX = 0.7853982F;
        this.plowShaftUpper[1].addChild(this.plowHandleGrip);
        this.leftWheel = new ModelRenderer(this, 34, 4);
        this.leftWheel.setRotationPoint(14.5F, 5.0F, 1.0F);
        this.leftWheel.addBox(-2.0F, -1.0F, -1.0F, 1, 2, 2);

        ModelRenderer rim;
        ModelRenderer spoke;
        for(i = 0; i < 8; ++i) {
            rim = new ModelRenderer(this, 0, 11);
            rim.addBox(-1.5F, -4.5F, 9.86F, 1, 9, 1);
            rim.rotateAngleX = (float)i * 3.1415927F / 4.0F;
            this.leftWheel.addChild(rim);
            spoke = new ModelRenderer(this, 4, 11);
            spoke.addBox(-1.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = (float)i * 3.1415927F / 4.0F;
            this.leftWheel.addChild(spoke);
        }

        this.rightWheel = new ModelRenderer(this, 34, 4);
        this.rightWheel.mirror = true;
        this.rightWheel.setRotationPoint(-14.5F, 5.0F, 1.0F);
        this.rightWheel.addBox(1.0F, -1.0F, -1.0F, 1, 2, 2);

        for(i = 0; i < 8; ++i) {
            rim = new ModelRenderer(this, 0, 11);
            rim.addBox(0.5F, -4.5F, 9.86F, 1, 9, 1);
            rim.rotateAngleX = (float)i * 3.1415927F / 4.0F;
            this.rightWheel.addChild(rim);
            spoke = new ModelRenderer(this, 4, 11);
            spoke.addBox(0.5F, 1.0F, -0.5F, 1, 9, 1);
            spoke.rotateAngleX = (float)i * 3.1415927F / 4.0F;
            this.rightWheel.addChild(spoke);
        }

    }

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        this.axis.render(scale);
        this.shaft.renderWithRotation(scale);
        this.shaftConnector.renderWithRotation(scale);

        for(int i = 0; i < 3; ++i) {
            this.triangle[i].render(scale);
        }

    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
        this.leftWheel.rotateAngleX = ((EntityPlowCartTFC)entity).getWheelRotation();
        this.rightWheel.rotateAngleX = this.leftWheel.rotateAngleX;
        this.leftWheel.render(scale);
        this.rightWheel.render(scale);
        ModelRenderer[] var8;
        int var9;
        int var10;
        ModelRenderer renderer;
        if (((EntityPlowCartTFC)entity).getPlowing()) {
            var8 = this.plowShaftUpper;
            var9 = var8.length;

            for(var10 = 0; var10 < var9; ++var10) {
                renderer = var8[var10];
                renderer.rotateAngleX = 0.7853982F;
            }
        } else {
            var8 = this.plowShaftUpper;
            var9 = var8.length;

            for(var10 = 0; var10 < var9; ++var10) {
                renderer = var8[var10];
                renderer.rotateAngleX = 1.2566371F;
            }
        }

        var8 = this.plowShaftUpper;
        var9 = var8.length;

        for(var10 = 0; var10 < var9; ++var10) {
            renderer = var8[var10];
            renderer.render(scale);
        }

    }
}


