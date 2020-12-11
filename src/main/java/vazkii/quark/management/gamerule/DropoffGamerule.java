/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/06/2016, 23:51:13 (GMT)]
 */
package vazkii.quark.management.gamerule;

import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import vazkii.quark.base.handler.DropoffHandler;
import vazkii.quark.management.feature.StoreToChests;

public class DropoffGamerule {

	@SubscribeEvent
	public void worldLoad(WorldEvent.Load event) {
		GameRules rules = event.getWorld().getGameRules();
		if(!rules.hasRule(StoreToChests.GAME_RULE))
			rules.addGameRule(StoreToChests.GAME_RULE, "true", ValueType.BOOLEAN_VALUE);
	}

	@SubscribeEvent
	public void login(PlayerLoggedInEvent event) {
		if(!event.player.getEntityWorld().getGameRules().getBoolean(StoreToChests.GAME_RULE))
			DropoffHandler.disableClientDropoff(event.player);
	}

}
