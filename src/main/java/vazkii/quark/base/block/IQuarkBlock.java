/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [28/08/2016, 00:23:31 (GMT)]
 */
package vazkii.quark.base.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.interf.IModBlock;
import vazkii.arl.item.ItemModBlock;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;

public interface IQuarkBlock extends IModBlock {

	@Override
	default String getModNamespace() {
		return LibMisc.MOD_ID;
	}

	default void register(String name) {
		Block self = (Block) this;
		ResourceLocation regName = new ResourceLocation(LibMisc.PREFIX_MOD + name);
		self.setRegistryName(regName);
		ProxyRegistry.register(self);
		ProxyRegistry.register(createItemInstance(regName));
	}

	default ItemBlock createItemInstance(ResourceLocation regName) {
		return new ItemModBlock((Block) this, regName);
	}
	
}
