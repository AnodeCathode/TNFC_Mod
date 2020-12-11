package vazkii.quark.oddities.feature;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.OverrideRegistryHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.oddities.block.BlockEnchantingTableReplacement;
import vazkii.quark.oddities.client.render.RenderTileMatrixEnchanter;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

import java.util.*;

public class MatrixEnchanting extends Feature {

	private static BlockEnchantingTableReplacement enchantingTable;

	public static int maxBookshelves, piecePriceScale, bookEnchantability, baseMaxPieceCount, baseMaxPieceCountBook,
			minLevelCutoff, chargePerLapis;
	public static float minLevelScaleFactor, minLevelScaleFactorBook, dupeMultiplier, incompatibleMultiplier;
	public static boolean allowBooks, allowTreasures, showTooltip, normalizeRarity;

	public static List<String> disallowedEnchantments;
	private static String[] influencesArr;

	public static boolean allowInfluencing;
	public static int influenceMax;
	public static float influencePower;
	public static Map<EnumDyeColor, List<Enchantment>> candleInfluences;

	@Override
	public void setupConfig() {
		maxBookshelves = loadPropInt("Max Bookshelves", "The maximum enchanting power the matrix enchanter can accept",
				15);
		piecePriceScale = loadPropInt("Piece Price Scale",
				"Should this be X, the price of a piece increase by 1 every X pieces you generate", 9);
		bookEnchantability = loadPropInt("Book Enchantability",
				"The higher this is, the better enchantments you'll get on books", 12);
		baseMaxPieceCount = loadPropInt("Base Max Piece Count",
				"How many pieces you can generate without any bookshelves", 3);
		baseMaxPieceCountBook = loadPropInt("Base Max Piece Count for Books",
				"How many pieces you can generate without any bookshelves (for Books)", 1);
		allowBooks = loadPropBool("Allow Enchanted Books",
				"Set to false to disable the ability to create Enchanted Books", true);
		allowTreasures = loadPropBool("Allow Treasure Enchantments",
				"Set this to true to allow treasure enchantments to be rolled as pieces", false);
		showTooltip = loadPropBool("Show Tooltip",
				"Set to false to disable the tooltip for items with pending enchantments", true);
		minLevelCutoff = loadPropInt("Min Level Cutoff",
				"At which piece count the calculation for the min level should default to increasing one per piece rather than using the scale factor",
				10);
		minLevelScaleFactor = (float) loadPropDouble("Min Level Scale Factor",
				"How much the min level requirement for adding a new piece should increase for each piece added (up until the value of Min Level Cutoff)",
				1.5F);
		minLevelScaleFactorBook = (float) loadPropDouble("Book Min Level Scale Factor",
				"How much the min level requirement for adding a new piece to a book should increase per each bookshelf being used",
				2F);
		normalizeRarity = loadPropBool("Normalize Rarity",
				"By default, enchantment rarities are fuzzed a bit to work better with the new system. Set this to false to override this behaviour.",
				true);
		chargePerLapis = loadPropInt("Charge per Lapis", "How many pieces a single Lapis can generate", 4);
		dupeMultiplier = (float) loadPropDouble("Dupe Multiplier",
				"How much to multiply the frequency of pieces where at least one of the same type has been generated",
				1.4);
		incompatibleMultiplier = (float) loadPropDouble("Incompatible Multiplier",
				"How much to multiply the frequency of pieces where incompatible pieces have been generated", 0);

		String[] enchArr = loadPropStringList("Disallowed Enchantments",
				"A list of enchantment IDs you don't want the enchantment table to be able to create", new String[0]);
		disallowedEnchantments = Arrays.asList(enchArr);

		allowInfluencing = loadPropBool("Influencing Enabled",
				"Set to false to disable the ability to influence enchantment outcomes with candles", true);
		influenceMax = loadPropInt("Influencing Max",
				"The max amount of candles that can influence a single enchantment", 4);
		influencePower = (float) loadPropDouble("Influencing Power",
				"How much each candle influences an enchantment. This works as a multiplier to its weight", 0.125);

		influencesArr = loadPropStringList("Influences", "An array of influences each candle should ",
				new String[] { "minecraft:unbreaking", "minecraft:fire_protection",
						"minecraft:knockback,minecraft:punch", "minecraft:feather_falling",
						"minecraft:looting,minecraft:fortune,minecraft:luck_of_the_sea", "minecraft:blast_protection",
						"minecraft:silk_touch", "minecraft:bane_of_arthropods", "minecraft:protection",
						"minecraft:respiration", "minecraft:sweeping",
						"minecraft:efficiency,minecraft:sharpness,minecraft:lure,minecraft:power",
						"minecraft:aqua_affinity,minecraft:depth_strider", "minecraft:thorns",
						"minecraft:fire_aspect,minecraft:flame", "minecraft:smite" });
		if (influencesArr.length != 16) {
			Quark.LOG.error("Matrix Enchanting Influences Array isn't the right length. Defaulting to an empty one.");
			influencesArr = new String[16];
		}

		if (candleInfluences != null)
			parseInfluences();
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		enchantingTable = new BlockEnchantingTableReplacement();
		enchantingTable.setTranslationKey("enchantmentTable");
		OverrideRegistryHandler.registerBlock(enchantingTable, "enchanting_table");

		registerTile(TileMatrixEnchanter.class, "matrix_enchanter");
	}

	@Override
	public void init() {
		parseInfluences();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileMatrixEnchanter.class, new RenderTileMatrixEnchanter());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (showTooltip && ItemNBTHelper.verifyExistence(stack, TileMatrixEnchanter.TAG_STACK_MATRIX))
			event.getToolTip().add(TextFormatting.AQUA + I18n.format("quarkmisc.pendingEnchants"));
	}

	private void parseInfluences() {
		candleInfluences = new HashMap<>();

		for (int i = 0; i < 16; i++) {
			List<Enchantment> list = new LinkedList<>();
			candleInfluences.put(EnumDyeColor.byMetadata(i), list);

			String s = influencesArr[i];
			String[] tokens = s.split(",");

			for (String enchStr : tokens) {
				enchStr = enchStr.trim();

				Enchantment ench = Enchantment.REGISTRY.getObject(new ResourceLocation(enchStr));
				if (ench == null)
					Quark.LOG.error("Matrix Enchanting Influencing: Enchantment " + enchStr + " does not exist!");
				else
					list.add(ench);
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
