/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/06/2016, 22:18:02 (GMT)]
 */
package vazkii.quark.world.feature;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.WorldTypeRealistic;

public class RealisticWorldType extends Feature {

	public static WorldType realistic;
	public static boolean makeRealisticDefault;

	public static double realisticCloudHeight;
	
	@Override
	public void setupConfig() {
		makeRealisticDefault = loadPropBool("Make Realistic Default", "Makes realistic the default world type. Only works for singleplayer.", false);
		realisticCloudHeight = loadPropDouble("Realistic Cloud Height", "What cloud height should realistic worlds have? 128 is default for vanilla worlds.", 260);
	}
	
	@Override
	public void postInit() {
		realistic = new WorldTypeRealistic("quark_realistic");
	}

	@Override
	public String getFeatureDescription() {
		return "Allows for usage of a new Realistic world type, made by /u/Soniop.\n"
				+ "https://www.reddit.com/r/Minecraft/comments/4nfw3t/more_realistic_generation_preset_in_comment/\n"
				+ "If you want to use it in multiplayer, set the world type to \"quark_realistic\"";
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void openGUI(InitGuiEvent.Pre event) {
		if(makeRealisticDefault && event.getGui() instanceof GuiCreateWorld) {
			GuiCreateWorld create = (GuiCreateWorld) event.getGui();
			int index = ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, create, LibObfuscation.SELECTED_INDEX);
			if(index == WorldType.DEFAULT.getId())
				ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, create, realistic.getId(), LibObfuscation.SELECTED_INDEX);
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
