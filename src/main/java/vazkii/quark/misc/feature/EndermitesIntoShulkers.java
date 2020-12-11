package vazkii.quark.misc.feature;

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.ai.EntityAIFormShulker;

public class EndermitesIntoShulkers extends Feature {

	public static double chance = 0.005;

	@Override
	public void setupConfig() {
		chance = loadLegacyPropChance("Transform Percentage Chance",
				"Transform Chance",
				"The chance for an Endermite to turn into a Shulker.\nThe chance for a Silverfish to bury is 0.1, for reference.", chance);
	}
	
	@SubscribeEvent
	public void onEnterChunk(EnteringChunk event) {
		if(event.getEntity() instanceof EntityEndermite) {
			EntityEndermite endermite = (EntityEndermite) event.getEntity();
			for(EntityAITaskEntry task : endermite.tasks.taskEntries)
				if(task.action instanceof EntityAIFormShulker)
					return;
			
			endermite.tasks.addTask(2, new EntityAIFormShulker(endermite));
		}
	}
	
	@Override	
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "mite2shulker" };
	}
}
