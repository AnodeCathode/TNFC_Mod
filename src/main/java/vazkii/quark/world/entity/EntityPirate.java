/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:09:46 (GMT)]
 */
package vazkii.quark.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.PirateShips;

import javax.annotation.Nonnull;

public class EntityPirate extends EntitySkeleton {

	private static final String TAG_CAPTAIN = "captain";
	
	private boolean captain;
	
	public EntityPirate(World worldIn) {
		super(worldIn);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	public void setCaptain(boolean captain) {
		this.captain = captain;
	}
	
	public boolean isCaptain() {
		return captain;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean(TAG_CAPTAIN, captain);
	}
	
	@Override
	public void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		captain = compound.getBoolean(TAG_CAPTAIN);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
		setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
	}
	
	@Override
	public void updatePassenger(@Nonnull Entity passenger) {
		if(isPassenger(passenger) && passenger instanceof EntityParrot) {
			EntityParrot parrot = (EntityParrot) passenger;
			parrot.flap = -1;
			parrot.rotationPitch = 0;

			float rotation = rotationYaw;
			float radius = 0.5F;
			float x = (float) Math.cos(rotation * 0.017453292F) * radius;
			float z = (float) Math.sin(rotation * 0.017453292F) * radius;

			parrot.setPosition(posX + x, posY + getMountedYOffset(), posZ + z);
		}
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		if(captain)
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60);
	}
	
}

