package tnfcmod.inventory;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;

import de.mennomax.astikorcarts.entity.AbstractDrawn;
import net.dries007.tfc.objects.items.metal.ItemMetalTool;

public class ContainerPlowCartTFC extends Container {
    private final IInventory plowInventory;
    private final AbstractDrawn drawn;

    public ContainerPlowCartTFC(InventoryPlayer playerInventory, IInventory plowInventory, AbstractDrawn drawn, EntityPlayer player) {
        this.plowInventory = plowInventory;
        this.drawn = drawn;
        plowInventory.openInventory(player);
        this.addSlotToContainer(new ContainerPlowCartTFC.Tool(plowInventory, 0, 57, 24));
        this.addSlotToContainer(new ContainerPlowCartTFC.Tool(plowInventory, 1, 80, 17));
        this.addSlotToContainer(new ContainerPlowCartTFC.Tool(plowInventory, 2, 103, 24));

        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.plowInventory.isUsableByPlayer(playerIn) && this.drawn.isEntityAlive() && this.drawn.getDistance(playerIn) < 8.0F;
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 3) {
                if (!this.mergeItemStack(itemstack1, 3, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.plowInventory.closeInventory(playerIn);
    }

    static class Tool extends Slot {
        public Tool(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        public int getSlotStackLimit() {
            return 1;
        }

        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemSpade || stack.getItem() instanceof ItemMetalTool;
        }
    }
}
