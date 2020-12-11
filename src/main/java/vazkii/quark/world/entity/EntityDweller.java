/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 20:02:38 (GMT)]
 */
package vazkii.quark.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.DepthMobs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityDweller extends EntityZombie {

	public EntityDweller(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0);
		getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
		// NO-OP
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
		addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, Integer.MAX_VALUE));
		return super.onInitialSpawn(difficulty, data);
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
		if(amount > 0)
			removePotionEffect(MobEffects.INVISIBILITY);
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		removePotionEffect(MobEffects.INVISIBILITY);
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && posY < DepthMobs.upperBound;
	}

}
