package vazkii.quark.tweaks.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class TorchesBurnInFurnaces extends Feature {

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@SubscribeEvent
	public void burnTorch(FurnaceFuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.TORCH))
			event.setBurnTime(400);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
