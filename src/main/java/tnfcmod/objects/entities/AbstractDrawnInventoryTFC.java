package tnfcmod.objects.entities;

import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;



public abstract class AbstractDrawnInventoryTFC extends AbstractDrawnTFC
{
    public InventoryBasic inventory;
    protected IItemHandler itemHandler;

    public AbstractDrawnInventoryTFC(World worldIn) {
        super(worldIn);
    }

    protected void initInventory(String title, boolean customName, int size) {
        this.inventory = new InventoryBasic(title, customName, size);
        this.itemHandler = new InvWrapper(this.inventory);
    }

    public void onDestroyed(DamageSource source, boolean byCreativePlayer) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            super.onDestroyed(source, byCreativePlayer);
            InventoryHelper.dropInventoryItems(this.world, this, this.inventory);
        }

    }

    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);

        for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            this.inventory.setInventorySlotContents(nbttagcompound.getByte("Slot") & 255, new ItemStack(nbttagcompound));
        }

    }

    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagList nbttaglist = new NBTTagList();

        for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Items", nbttaglist);
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this.itemHandler : super.getCapability(capability, facing);
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
