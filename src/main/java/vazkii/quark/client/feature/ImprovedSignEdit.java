package vazkii.quark.client.feature;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibObfuscation;
import vazkii.quark.base.module.Feature;
import vazkii.quark.client.gui.GuiBetterEditSign;

public class ImprovedSignEdit extends Feature {

	public static boolean enableCancel, enableClear, enableShift;
	
	@Override
	public void setupConfig() {
		enableCancel = loadPropBool("Enable Cancel Button", "", true);
		enableClear = loadPropBool("Enable Clear Button", "", true);
		enableShift = loadPropBool("Enable Shift Button", "", true);
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onOpenGUI(GuiOpenEvent event) {
		if(event.getGui() instanceof GuiEditSign) {
			TileEntitySign sign = ObfuscationReflectionHelper.getPrivateValue(GuiEditSign.class, (GuiEditSign) event.getGui(), LibObfuscation.TILE_SIGN);
			event.setGui(new GuiBetterEditSign(sign));
		}
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}
	
}
