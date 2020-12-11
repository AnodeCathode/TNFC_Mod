package vazkii.quark.base.handler;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public final class ModIntegrationHandler {

	public static void addCharsetCarry(Block b) {
		FMLInterModComms.sendMessage("charset", "addCarry", b.getRegistryName());
	}

	public static void registerChiselVariant(String group, ItemStack stack) {
		NBTTagCompound cmp = new NBTTagCompound();
		cmp.setString("group", group);
		cmp.setTag("stack", stack.serializeNBT());

		FMLInterModComms.sendMessage("chisel", "add_variation", cmp);
	}
	
	public static void allowChiselAndBitsChiseling(Block b) {
		FMLInterModComms.sendMessage("chiselsandbits", "ignoreblocklogic", b.getRegistryName());
	}
	
}
