/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 24, 2019, 22:47 AM (EST)]
 */
package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.quark.world.feature.Frogs;

import java.util.Calendar;
import java.util.Set;

public class EntityAITemptButNice extends EntityAITempt {
	private final Set<Item> temptItemNice;

	private final EntityCreature nice;

	public EntityAITemptButNice(EntityCreature temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Set<Item> temptItemIn, Set<Item> temptItemNice) {
		super(temptedEntityIn, speedIn, scaredByPlayerMovementIn, temptItemIn);
		this.temptItemNice = temptItemNice;
		this.nice = temptedEntityIn;
	}

	@Override
	protected boolean isTempting(ItemStack stack) {
		Calendar calendar = nice.world.getCurrentDate();
		return Frogs.frogsDoTheFunny && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY ?
				temptItemNice.contains(stack.getItem()) : super.isTempting(stack);
	}
}
