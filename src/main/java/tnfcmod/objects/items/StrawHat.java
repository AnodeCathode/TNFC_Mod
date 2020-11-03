package tnfcmod.objects.items;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tnfcmod.client.model.ModelStrawHat;
import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;
import static tnfcmod.tnfcmod.strawArmorMaterial;

public class StrawHat extends ItemArmor
{
    protected String name;

    public StrawHat(String name) {
        super(strawArmorMaterial,1, EntityEquipmentSlot.HEAD);
        this.setNoRepair();
        this.name= name;
        setRegistryName(name);
        setTranslationKey(MODID + "." + name);

    }

    public void registerItemModel()
    {

        tnfcmod.proxy.registerItemRenderer(this, 0, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack)
    {
        return false;
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return MODID + ":textures/models/armor/straw_hat.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nullable
    public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default) {
        return new ModelStrawHat();
    }


}


