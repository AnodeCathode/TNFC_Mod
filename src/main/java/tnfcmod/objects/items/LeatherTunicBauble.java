package tnfcmod.objects.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;


public class LeatherTunicBauble extends ItemArmor implements IBauble
{
    protected String name;

    public LeatherTunicBauble(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
    {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.name = name;
        setTranslationKey(MODID + "." + name);
        setRegistryName(name);

    }



    @Override
    public BaubleType getBaubleType(ItemStack itemStack)
    {
        return BaubleType.BODY;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player)
    {

    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player)
    {

    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player)
    {

    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player)
    {
        return true;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player)
    {
        return false;
    }

    public void registerItemModel() {

        tnfcmod.proxy.registerItemRenderer(this, 0, name);
    }
}
