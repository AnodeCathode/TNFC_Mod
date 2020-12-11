/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:07:15 (GMT)]
 */
package vazkii.quark.client.feature;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;

import javax.annotation.Nonnull;

public class LessIntrusiveShields extends Feature {

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		ModelHandler.registerModels(Items.SHIELD, LibMisc.PREFIX_MOD, new String[] { "shield_override" }, null, true);

		ModelLoader.setCustomMeshDefinition(Items.SHIELD, new ItemMeshDefinition() {
			@Nonnull
			@Override
			public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
				return new ModelResourceLocation("quark:shield_override", "inventory");
			}
		});
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
