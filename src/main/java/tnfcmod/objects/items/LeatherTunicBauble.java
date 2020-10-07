package tnfcmod.objects.items;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import tnfcmod.tnfcmod;

import static tnfcmod.tnfcmod.MODID;


public class LeatherTunicBauble extends ItemTFC implements IBauble
{
    protected String name;

    public LeatherTunicBauble(String name)
    {
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
        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5, 2, false, false));
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

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack itemStack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack itemStack)
    {
        return Weight.MEDIUM;
    }
}
