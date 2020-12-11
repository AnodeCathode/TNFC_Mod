/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:35:41 (GMT)]
 */
package vazkii.quark.decoration;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.decoration.feature.BetterVanillaFlowerPot;
import vazkii.quark.decoration.feature.BlazeLantern;
import vazkii.quark.decoration.feature.CharcoalBlock;
import vazkii.quark.decoration.feature.ColoredFlowerPots;
import vazkii.quark.decoration.feature.ColoredItemFrames;
import vazkii.quark.decoration.feature.FlatItemFrames;
import vazkii.quark.decoration.feature.GlassItemFrame;
import vazkii.quark.decoration.feature.Grate;
import vazkii.quark.decoration.feature.IronLadders;
import vazkii.quark.decoration.feature.LeafCarpets;
import vazkii.quark.decoration.feature.LitLamp;
import vazkii.quark.decoration.feature.MoreBannerLayers;
import vazkii.quark.decoration.feature.MoreBanners;
import vazkii.quark.decoration.feature.NetherBrickFenceGate;
import vazkii.quark.decoration.feature.PaperLantern;
import vazkii.quark.decoration.feature.PaperWall;
import vazkii.quark.decoration.feature.PlaceBlazeRods;
import vazkii.quark.decoration.feature.Rope;
import vazkii.quark.decoration.feature.TallowAndCandles;
import vazkii.quark.decoration.feature.TieFences;
import vazkii.quark.decoration.feature.VariedBookshelves;
import vazkii.quark.decoration.feature.VariedButtonsAndPressurePlates;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.feature.VariedTrapdoors;

public class QuarkDecoration extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new LitLamp());
		registerFeature(new BlazeLantern());
		registerFeature(new PaperWall());
		registerFeature(new VariedTrapdoors());
		registerFeature(new MoreBanners());
		registerFeature(new NetherBrickFenceGate());
		registerFeature(new ColoredItemFrames());
		registerFeature(new CharcoalBlock());
		registerFeature(new VariedChests());
		registerFeature(new LeafCarpets());
		registerFeature(new VariedBookshelves());
		registerFeature(new IronLadders());
		registerFeature(new FlatItemFrames());
		registerFeature(new GlassItemFrame());
		registerFeature(new ColoredFlowerPots());
		registerFeature(new BetterVanillaFlowerPot());
		registerFeature(new TallowAndCandles());
		registerFeature(new MoreBannerLayers());
		registerFeature(new VariedButtonsAndPressurePlates());
		registerFeature(new PaperLantern());
		registerFeature(new TieFences());
		registerFeature(new Rope());
		registerFeature(new PlaceBlazeRods());
		registerFeature(new Grate());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Blocks.RED_FLOWER);
	}

}
