/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 15, 2019, 07:07 AM (EST)]
 */
package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockRedstoneInductor;
import vazkii.quark.automation.tile.TileInductor;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;

public class RedstoneInductor extends Feature {
	public static Block redstone_inductor;

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		redstone_inductor = new BlockRedstoneInductor();
		GameRegistry.registerTileEntity(TileInductor.class, new ResourceLocation(LibMisc.MOD_ID, "inductor"));
	}

	@Override
	public void postPreInit() {
		RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(redstone_inductor),
				"RRM", "SSS",
				'M', ProxyRegistry.newStack(Items.MAGMA_CREAM),
				'R', "dustRedstone",
				'S', "stone");
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
}
