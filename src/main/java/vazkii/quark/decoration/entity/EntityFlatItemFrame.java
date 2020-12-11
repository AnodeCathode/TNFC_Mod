/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/06/2016, 23:52:04 (GMT)]
 */
package vazkii.quark.decoration.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.commons.lang3.Validate;

import java.util.function.Predicate;

public class EntityFlatItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {

	protected static final Predicate<Entity> IS_HANGING_ENTITY = entity -> entity instanceof EntityHanging;

	private static final String TAG_ITEM_DROP_CHANCE = "ItemDropChance";
	private static final String TAG_REAL_FACING_DIRECTION = "RealFacing";

	public EnumFacing realFacingDirection;
	private float itemDropChance = 1.0F;

	public EntityFlatItemFrame(World worldIn) {
		super(worldIn);
	}

	public EntityFlatItemFrame(World worldIn, BlockPos blockPos, EnumFacing face) {
		super(worldIn, blockPos, face);
	}

	@Override
	public void dropItemOrSelf(Entity entityIn, boolean creative) {
		if(!creative) {
			super.dropItemOrSelf(entityIn, false);
			return;
		}

		if(getEntityWorld().getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemstack = getDisplayedItem();

			if(entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer)entityIn;

				if(entityplayer.capabilities.isCreativeMode) {
					removeFrameFromMap(itemstack);
					return;
				}
			}

			dropFrame();

			if(!itemstack.isEmpty() && rand.nextFloat() < itemDropChance) {
				itemstack = itemstack.copy();
				removeFrameFromMap(itemstack);
				entityDropItem(itemstack, 0.0F);
			}

		}
	}

	protected void dropFrame() {
		entityDropItem(new ItemStack(Items.ITEM_FRAME, 1), 0.0F);
	}

	@Override
	public EntityItem entityDropItem(ItemStack stack, float offsetY) {
		EntityItem entityitem = new EntityItem(this.world, this.posX + (this.realFacingDirection.getXOffset() * 0.25F), this.posY + offsetY + (this.realFacingDirection.getYOffset() * 0.25F), this.posZ + (this.realFacingDirection.getZOffset() * 0.25F), stack);
		entityitem.setDefaultPickupDelay();
		if (realFacingDirection == EnumFacing.DOWN)
			entityitem.motionY = -Math.abs(entityitem.motionY);
		this.world.spawnEntity(entityitem);
		return entityitem;
	}

	@Override
	public boolean onValidSurface() {
		if(this.realFacingDirection.getAxis() == EnumFacing.Axis.Y) {
			if(!this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
				return false;
			} else {
				BlockPos blockpos = this.hangingPosition.offset(this.realFacingDirection.getOpposite());
				IBlockState iblockstate = this.world.getBlockState(blockpos);
				if(!iblockstate.isSideSolid(this.world, blockpos, this.realFacingDirection))
					if(!iblockstate.getMaterial().isSolid() && !BlockRedstoneDiode.isDiode(iblockstate))
						return false;

				return this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), IS_HANGING_ENTITY::test).isEmpty();
			}
		} else
			return super.onValidSurface();
	}

	@Override
	protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
		Validate.notNull(facingDirectionIn);
		this.realFacingDirection = facingDirectionIn;
		this.facingDirection = realFacingDirection.getAxis() == EnumFacing.Axis.Y ? EnumFacing.SOUTH : realFacingDirection;
		this.rotationYaw = realFacingDirection.getAxis() == EnumFacing.Axis.Y ? 0 : (this.realFacingDirection.getHorizontalIndex() * 90);
		this.rotationPitch = realFacingDirection.getAxis() == EnumFacing.Axis.Y ? (realFacingDirection == EnumFacing.UP ? -90.0F : 90.0F) : 0F;
		this.prevRotationYaw = this.rotationYaw;
		this.updateBoundingBox();
	}

	@Override
	protected void updateBoundingBox() {
		if(this.realFacingDirection == null)
			return;
		
		if(this.realFacingDirection.getAxis() == EnumFacing.Axis.Y) {
			double d0 = this.hangingPosition.getX() + 0.5D;
			double d1 = this.hangingPosition.getY() + 0.5D;
			double d2 = this.hangingPosition.getZ() + 0.5D;
			d1 = d1 - this.realFacingDirection.getYOffset() * 0.46875D;

			double d6 = this.getHeightPixels();
			double d7 = -this.realFacingDirection.getYOffset();
			double d8 = this.getHeightPixels();

			d6 = d6 / 32.0D;
			d7 = d7 / 32.0D;
			d8 = d8 / 32.0D;

			this.posX = d0;
			this.posY = d1 - d7;
			this.posZ = d2;
			this.height = 1.0F / 16.0F;
			this.setEntityBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
		} else
			super.updateBoundingBox();
	}

	@SuppressWarnings("ConstantConditions")
	private void removeFrameFromMap(ItemStack stack) {
		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof ItemMap) {
				MapData mapdata = ((ItemMap) stack.getItem()).getMapData(stack, getEntityWorld());
				if (mapdata != null)
					mapdata.mapDecorations.remove("frame-" + getEntityId());
			}

			stack.setItemFrame(null);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		compound.setByte(TAG_REAL_FACING_DIRECTION, (byte)this.realFacingDirection.getIndex());
		super.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		if(compound.hasKey(TAG_ITEM_DROP_CHANCE, 99)) {
			itemDropChance = compound.getFloat(TAG_ITEM_DROP_CHANCE);
		}

		super.readEntityFromNBT(compound);
		this.updateFacingWithBoundingBox(EnumFacing.byIndex(compound.getByte(TAG_REAL_FACING_DIRECTION)));
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeShort(realFacingDirection.getIndex());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		updateFacingWithBoundingBox(EnumFacing.byIndex(additionalData.readShort()));
	}

}
