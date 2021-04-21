package se.gory_moon.horsepower.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.HPRecipeBase;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.PressRecipe;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nullable;

public class TileEntityPress extends TileEntityHPHorseBase {

    private FluidTank tank = new FluidTank(Configs.general.pressFluidTankSize);
    private int currentPressStatus;

    public TileEntityPress() {
        super(2);
        tank.setCanFill(false);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("currentPressStatus", currentPressStatus);
        compound.setTag("fluid", tank.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tank.readFromNBT(compound.getCompoundTag("fluid"));

        if (getStackInSlot(0).getCount() > 0) {
            currentPressStatus = compound.getInteger("currentPressStatus");
        } else {
            currentPressStatus = 0;
        }
    }

    @Override
    public void markDirty() {
        if (getStackInSlot(0).isEmpty())
            currentPressStatus = 0;

        super.markDirty();
    }

    @Override
    public boolean validateArea() {
        if (searchPos == null) {
            searchPos = Lists.newArrayList();

            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if ((x <= 1 && x >= -1) && (z <= 1 && z >= -1))
                        continue;
                    searchPos.add(getPos().add(x, 0, z));
                    searchPos.add(getPos().add(x, 1, z));
                }
            }
        }

        for (BlockPos pos: searchPos) {
            if (!getWorld().getBlockState(pos).getBlock().isReplaceable(world, pos))
                return false;
        }
        return true;
    }

    @Override
    public boolean targetReached() {
        currentPressStatus++;

        int totalPress = Configs.general.pointsForPress;
        if (currentPressStatus >= (totalPress <= 0 ? 1: totalPress)) {
            currentPressStatus = 0;

            pressItem();
            return true;
        }
        markDirty();
        return false;
    }

    @Override
    public HPRecipeBase getRecipe() {
        return HPRecipes.instance().getPressRecipe(getStackInSlot(0));
    }

    @Override
    public ItemStack getRecipeItemStack() {
        return HPRecipes.instance().getPressResult(getStackInSlot(0));
    }

    @Override
    public int getPositionOffset() {
        return 0;
    }

    private void pressItem() {
        if (canWork()) {
            PressRecipe recipe = (PressRecipe) getRecipe();
            ItemStack result = recipe.getOutput();
            FluidStack fluidResult = recipe.getOutputFluid();

            ItemStack input = getStackInSlot(0);
            ItemStack output = getStackInSlot(1);

            if (recipe.isLiquidRecipe()) {
                tank.fillInternal(fluidResult, true);
                if (result != ItemStack.EMPTY){
                    if (output.isEmpty()) {
                        setInventorySlotContents(1, result.copy());
                    } else if (output.isItemEqual(result)) {
                        output.grow(result.getCount());
                    }
                }
                //HorsePowerMod.logger.info("Tank: " + tank.getFluid().amount);
            } else {
                if (output.isEmpty()) {
                    setInventorySlotContents(1, result.copy());
                } else if (output.isItemEqual(result)) {
                    output.grow(result.getCount());
                }
            }

            input.shrink(input.getCount());
            markDirty();
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag)
            currentPressStatus = 0;

        markDirty();
    }

    @Override
    public boolean canWork() {
        if (getStackInSlot(0).isEmpty()) {
            return false;
        } else {
            PressRecipe recipe = (PressRecipe) getRecipe();
            if (recipe == null) return false;

            ItemStack input = recipe.getInput();
            ItemStack itemstack = recipe.getOutput();
            FluidStack fluidOutput = recipe.getOutputFluid();

            if (getStackInSlot(0).getCount() < input.getCount())
                return false;
            if (itemstack.isEmpty() && !recipe.isLiquidRecipe())
                return false;

            ItemStack output = getStackInSlot(1);
            if (recipe.isMixedRecipe()) {
                return (output.isItemEqual(itemstack) && output.getCount() + itemstack.getCount() <= output.getMaxStackSize() || output.isEmpty()) &&
                    (tank.getFluidAmount() == 0 || tank.fillInternal(fluidOutput, false) >= fluidOutput.amount);
            }
            if (recipe.isLiquidRecipe()) {
                return output.isEmpty() && (tank.getFluidAmount() == 0 || tank.fillInternal(fluidOutput, false) >= fluidOutput.amount);
            } else {
                return tank.getFluidAmount() == 0 && (output.isEmpty() || output.isItemEqual(itemstack) && output.getCount() + itemstack.getCount() <= output.getMaxStackSize());
            }
        }
    }

    @Override
    public int getInventoryStackLimit(ItemStack stack) {
        PressRecipe recipe = HPRecipes.instance().getPressRecipe(stack);
        if (recipe == null)
            return getInventoryStackLimit();
        return recipe.getInput().getCount();
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return currentPressStatus == 0 ? super.removeStackFromSlot(index): ItemStack.EMPTY;
    }

    @Override
    public int getInventoryStackLimit() {
        PressRecipe recipe = HPRecipes.instance().getPressRecipe(getStackInSlot(0));
        if (recipe == null)
            return 64;
        return recipe.getInput().getCount();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && HPRecipes.instance().hasPressRecipe(stack) && currentPressStatus == 0 && getStackInSlot(1).isEmpty();
    }

    public IFluidTankProperties[] getTankFluidStack() {
        return tank.getTankProperties();
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return currentPressStatus;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                currentPressStatus = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public String getName() {
        return "container.press";
    }

    @Override
    public int getOutputSlot() {
        return 1;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        if (valid)
            return super.getDisplayName();
        else
            return new TextComponentTranslation(Localization.INFO.PRESS_INVALID.key()).setStyle(new Style().setColor(TextFormatting.RED));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return ((capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)) || super.hasCapability(capability, facing));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (facing == null || facing == EnumFacing.DOWN)
                return (T) tank;
        }
        return super.getCapability(capability, facing);
    }
}
