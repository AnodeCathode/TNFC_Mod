/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 12, 2019, 11:31 AM (EST)]
 */
package vazkii.quark.oddities.feature;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.feature.MoreBanners;
import vazkii.quark.oddities.block.BlockTinyPotato;
import vazkii.quark.oddities.client.render.RenderTileTinyPotato;
import vazkii.quark.oddities.tile.TileTinyPotato;

public class TinyPotato extends Feature {

	public static boolean enableBannerVariant;

	public static BlockTinyPotato tiny_potato;

	@Override
	public void setupConfig() {
		enableBannerVariant = loadPropBool("Enable Banner Variant", "Should the potato get a banner variant?", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		tiny_potato = new BlockTinyPotato();
		GameRegistry.registerTileEntity(TileTinyPotato.class, new ResourceLocation(LibMisc.MOD_ID, "tiny_potato"));

		RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(tiny_potato),
				"D",
				"P",
				'D', "gemDiamond",
				'P', "cropPotato");

		RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(tiny_potato),
				"D",
				"P",
				'D', "gemEmerald",
				'P', "cropPotato");
	}

	@Override
	public void init() {
		MoreBanners.addPattern(enableBannerVariant, "tiny_potato", "tp", ProxyRegistry.newStack(tiny_potato));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileTinyPotato.class, new RenderTileTinyPotato());
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "botania" };
	}
}
