/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 08, 2019, 16:40 AM (EST)]
 */
package vazkii.quark.misc.feature;

import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.ai.EntityAIHorseFollow;
import vazkii.quark.misc.item.ItemHorseWhistle;

public class HorseWhistle extends Feature {

	public static boolean horsesAreMagical;

	public static double horseSummonRange;

	public static ItemHorseWhistle horse_whistle;

	@Override
	public void setupConfig() {
		horsesAreMagical = loadPropBool("Horses Teleport When Whistled", "Should horses teleport when you call for them if they can't find a way to get to you?", false);

		horseSummonRange = loadPropDouble("Horse Summoning Range", "How far away should horses be able to hear the whistle?", 64.0);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		horse_whistle = new ItemHorseWhistle("horse_whistle");

		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(horse_whistle),
				"PPP", "NSP",
				'P', "plankWood",
				'N', "nuggetIron",
				'S', "stickWood");
	}

	@SubscribeEvent
	public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
		if(event.getEntity() instanceof AbstractHorse) {
			AbstractHorse horse = (AbstractHorse) event.getEntity();
			for(EntityAITasks.EntityAITaskEntry task : horse.tasks.taskEntries)
				if(task.action instanceof EntityAIHorseFollow)
					return;

			horse.tasks.addTask(1, new EntityAIHorseFollow(horse, 2.0));
		}
	}


	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
