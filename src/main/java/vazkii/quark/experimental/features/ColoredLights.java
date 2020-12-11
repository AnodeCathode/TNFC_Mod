package vazkii.quark.experimental.features;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.experimental.lighting.BlockTinter;
import vazkii.quark.experimental.lighting.ColoredLightSystem;

public class ColoredLights extends Feature {

	private static boolean enabled;
	public static boolean simulateTravel;
	public static boolean cullToLightmap;

	@Override
	public void setupConfig() {
		simulateTravel = loadPropBool("Simulate Light Travel", "Simulates the way light travels to calculate the colored light value properly.\n"
				+ "This needs to be enabled to prevent light from blending through walls.\n"
				+ "Note that this feature heavily increases memory and CPU requirements. Do NOT use it if you plan on having a lot of lights.\n"
				+ "You have been warned.", false);
		
		cullToLightmap = loadPropBool("Cull To Lightmap", "Set this to true to make colored light cap out at the value of natural light.\n"
				+ "Note that having this enabled will make light values inconsistent based on when the light blocks were placed and updated.", false);
	}
	
	@Override
	public String getFeatureDescription() {
		return "This feature is an incomplete test, and not fit for regular gameplay. Feel free to enable it to mess around but it may explode horribly. Don't report bugs with it.\n"
				+ "To make a colored light, use a Lit Redstone Lamp and put the color of Stained Glass you want under it.\n"
				+ "For obvious reasons, this is incompatible with Optifine.\n";
	}
	
	public static void putColorsFlat(IBlockAccess world, IBlockState state, BlockPos pos, BufferBuilder buffer, BakedQuad quad, int lightColor) {
		if(!enabled)
			return;
		
		BlockTinter.tintBlockFlat(world, state, pos, buffer, quad, lightColor);
	}
	
	public static void addLightSource(IBlockAccess access, BlockPos pos, IBlockState state) {
		if(enabled)
			ColoredLightSystem.addLightSource(access, pos, state);
	}

	@Override
	public void onEnabled() {
		enabled = true;
	}

	@Override
	public void onDisabled() {
		enabled = false;
	}

	@SubscribeEvent
	public void preRenderTick(RenderTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(event.phase != Phase.START)
			return;
		
		ColoredLightSystem.tick(mc);
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
