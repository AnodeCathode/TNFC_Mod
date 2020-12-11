/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [19/03/2016, 16:37:16 (GMT)]
 */
package vazkii.quark.client.feature;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.client.render.RenderCreeperAngry;

public class AngryCreepers extends Feature {

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		RenderingRegistry.registerEntityRenderingHandler(EntityCreeper.class, RenderCreeperAngry.factory());
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Creepers Turn Red";
	}

}
