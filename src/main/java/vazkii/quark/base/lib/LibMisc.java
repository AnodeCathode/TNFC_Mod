/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 21:39:39 (GMT)]
 */
package vazkii.quark.base.lib;

import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public final class LibMisc {

	// Don't ask.
	public static final String DEV_ENV_HACK = "[GRADLE.VERSION-GRADLE.BUILD,),";

	// Mod Constants
	public static final String MOD_ID = "quark";
	public static final String MOD_NAME = MOD_ID;
	public static final String BUILD = "99";
	public static final String VERSION = "1.12.2-" + BUILD;
	public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2831,);required-before:autoreglib@[1.3-32,);after:jei@[4.6.0,)";
	public static final String PREFIX_MOD = MOD_ID + ":";

	// Proxy Constants
	public static final String PROXY_COMMON = "vazkii.quark.base.proxy.CommonProxy";
	public static final String PROXY_CLIENT = "vazkii.quark.base.proxy.ClientProxy";
	public static final String GUI_FACTORY = "vazkii.quark.base.client.gui.GuiFactory";

	public static final List<String> OREDICT_DYES = Arrays.asList("dyeBlack",
			"dyeRed",
			"dyeGreen",
			"dyeBrown",
			"dyeBlue",
			"dyePurple",
			"dyeCyan",
			"dyeLightGray",
			"dyeGray",
			"dyePink",
			"dyeLime",
			"dyeYellow",
			"dyeLightBlue",
			"dyeMagenta",
			"dyeOrange",
			"dyeWhite");
	
	public static final String MOD_WEBSITE = "https://quark.vazkii.net";
	
	public static final ResourceLocation GENERAL_ICONS_RESOURCE = new ResourceLocation(MOD_ID, "textures/misc/general_icons.png");
	
}
