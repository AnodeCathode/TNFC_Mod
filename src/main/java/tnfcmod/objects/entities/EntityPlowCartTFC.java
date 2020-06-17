package tnfcmod.objects.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import de.mennomax.astikorcarts.config.ModConfig;

import de.mennomax.astikorcarts.entity.AbstractDrawnInventory;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlockPlacedItemFlat;
import net.dries007.tfc.objects.items.metal.ItemMetalHoe;
import net.dries007.tfc.objects.items.metal.ItemMetalTool;
import tnfcmod.objects.items.TNFCItems;

import static tnfcmod.tnfcmod.instance;

public class EntityPlowCartTFC extends AbstractDrawnInventory implements IInventoryChangedListener
{
    private static final DataParameter<Boolean> PLOWING = EntityDataManager.<Boolean>createKey(EntityPlowCartTFC.class, DataSerializers.BOOLEAN);
    private static final double BLADEOFFSET = 1.7D;
    @SuppressWarnings("rawtypes")
    private static final DataParameter[] TOOLS = {
        EntityDataManager.<ItemStack>createKey(EntityPlowCartTFC.class, DataSerializers.ITEM_STACK),
        EntityDataManager.<ItemStack>createKey(EntityPlowCartTFC.class, DataSerializers.ITEM_STACK),
        EntityDataManager.<ItemStack>createKey(EntityPlowCartTFC.class, DataSerializers.ITEM_STACK)
    };

    public EntityPlowCartTFC(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.4F);
        this.spacing = 2.4D;
        this.initInventory(this.getName(), true, 3);
        this.inventory.addInventoryChangeListener(this);
    }

    @Override
    public boolean canBePulledBy(Entity pullingIn)
    {
        if (this.isPassenger(pullingIn))
        {
            return false;
        }
        for (String entry : ModConfig.plowCart.canPull)
        {
            if (entry.equals(pullingIn instanceof EntityPlayer ? "minecraft:player" : EntityList.getKey(pullingIn).toString()))
            {
                return true;
            }
        }
        return false;
    }

    public Item getCartItem() {
        return TNFCItems.PLOWCARTTNFC;
    }

    public boolean getPlowing() {
        return (Boolean)this.dataManager.get(PLOWING);
    }

    public void onUpdate()
    {
        super.onUpdate();
        EntityPlayer player = this.pulling != null && this.pulling.getControllingPassenger() instanceof EntityPlayer ? (EntityPlayer) this.pulling.getControllingPassenger() : (this.pulling instanceof EntityPlayer ? (EntityPlayer) this.pulling : null);
        if (!this.world.isRemote && this.dataManager.get(PLOWING) && player != null)
        {
            if (this.prevPosX != this.posX || this.prevPosZ != this.posZ)
            {
                for (int i = 0; i < this.inventory.getSizeInventory(); i++)
                {
                    if(inventory.getStackInSlot(i) != ItemStack.EMPTY)
                    {
                        float offset = 38.0F+i*-38.0F;
                        double blockPosX = this.posX + MathHelper.sin((this.rotationYaw-offset) * 0.017453292F) * BLADEOFFSET;
                        double blockPosZ = this.posZ - MathHelper.cos((this.rotationYaw-offset) * 0.017453292F) * BLADEOFFSET;
                        BlockPos blockPos = new BlockPos(blockPosX, this.posY - 0.5D, blockPosZ);
                        BlockPos upPos = blockPos.up();
                        Material upMaterial = this.world.getBlockState(upPos).getMaterial();
                        if (upMaterial == Material.AIR)
                        {
                            handleTool(blockPos, i, player);
                        }
                        else if (upMaterial == Material.PLANTS || upMaterial == Material.VINE ||this.world.getBlockState(upPos).getBlock() instanceof BlockPlacedItemFlat)
                        {
                            this.world.destroyBlock(upPos, false);
                            handleTool(blockPos, i, player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        for(int i = 0; i < TOOLS.length; ++i) {
            if (this.dataManager.get(TOOLS[i]) != invBasic.getStackInSlot(i)) {
                this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
            }
        }

    }

    private void damageAndUpdateOnBreak(int slot, ItemStack itemstack, EntityPlayer player) {
        itemstack.damageItem(1, player);
        if (itemstack.isEmpty()) {
            this.dataManager.set(TOOLS[slot], ItemStack.EMPTY);
        }

    }
    private void handleTool(BlockPos pos, int slot, EntityPlayer player) {
        IBlockState state = this.world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack itemstack = this.inventory.getStackInSlot(slot);
        Item item = itemstack.getItem();
        if (item instanceof ItemHoe || item instanceof ItemMetalHoe) {
            if (block != Blocks.GRASS && block != Blocks.GRASS_PATH) {
                if (block == Blocks.DIRT) {
                    switch((BlockDirt.DirtType)state.getValue(BlockDirt.VARIANT)) {
                        case DIRT:
                            this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
                            this.damageAndUpdateOnBreak(slot, itemstack, player);
                            break;
                        case COARSE_DIRT:
                            this.world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), 11);
                            this.damageAndUpdateOnBreak(slot, itemstack, player);
                    }
                }
            } else {
                this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
                this.damageAndUpdateOnBreak(slot, itemstack, player);
            }
        } else if (itemstack.getItem() instanceof ItemSpade && block == Blocks.GRASS) {
            this.world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
            this.damageAndUpdateOnBreak(slot, itemstack, player);
        } else if(itemstack.getItem() instanceof ItemMetalTool){
                ItemMetalTool metaltool = (ItemMetalTool) itemstack.getItem();
            if (metaltool.getType() == Metal.ItemType.SHOVEL && block == Blocks.GRASS){
                this.world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
                this.damageAndUpdateOnBreak(slot, itemstack, player);
            }
        }

    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (!this.world.isRemote) {
            if (player.isSneaking()) {
                player.openGui(instance, 0, this.world, this.getEntityId(), 0, 0);
            } else {
                this.dataManager.set(PLOWING, !(Boolean)this.dataManager.get(PLOWING));
            }
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        dataManager.set(PLOWING, compound.getBoolean("Plowing"));
        for(int i = 0; i < TOOLS.length; i++)
        {
            this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Plowing", dataManager.get(PLOWING));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(PLOWING, false);
        for(int i = 0; i < TOOLS.length; i++)
        {
            this.dataManager.register(TOOLS[i], ItemStack.EMPTY);
        }
    }

    @SuppressWarnings("unchecked")
    public ItemStack getTool(int i)
    {
        return (ItemStack) this.dataManager.get(TOOLS[i]);
    }
}



