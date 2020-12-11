package vazkii.quark.experimental.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;

import java.util.Random;

public class CustomSplashes extends Feature {

	private static String[] splashes;
	
	private static final Random rand = new Random();
	private static boolean inMainMenu = false;
	
	@Override
	public void setupConfig() {
		splashes = loadPropStringList("Splashes", "", new String[] { "Psidust is now completely uncraftable", "No bypass commands work" });
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			boolean currentlyMainMenu = gui instanceof GuiMainMenu;
			
			if(!inMainMenu && currentlyMainMenu && splashes.length > 0) {
				String splash = splashes[rand.nextInt(splashes.length)];
				ObfuscationReflectionHelper.setPrivateValue(GuiMainMenu.class, ((GuiMainMenu) gui), splash, LibObfuscation.SPLASH_TEXT);
			}
			
			inMainMenu = currentlyMainMenu;
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
}
