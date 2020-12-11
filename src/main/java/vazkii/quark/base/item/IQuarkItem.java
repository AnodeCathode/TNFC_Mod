/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [28/08/2016, 00:25:35 (GMT)]
 */
package vazkii.quark.base.item;

import vazkii.arl.interf.IVariantHolder;
import vazkii.quark.base.lib.LibMisc;

public interface IQuarkItem extends IVariantHolder {
	
	@Override
	default String getModNamespace() {
		return LibMisc.MOD_ID;
	}

}
