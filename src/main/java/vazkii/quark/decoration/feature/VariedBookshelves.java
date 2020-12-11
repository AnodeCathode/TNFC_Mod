/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * 
 * File Created @ [Aug 14, 2016, 9:38:27 PM (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.arl.block.BlockMod;
import vazkii.arl.recipe.BlacklistOreIngredient;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.RecipeProcessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCustomBookshelf;

public class VariedBookshelves extends Feature {

	public static BlockMod custom_bookshelf;

	public static boolean renameVanillaBookshelves;
	
	@Override
	public void setupConfig() {
		renameVanillaBookshelves = loadPropBool("Rename vanilla bookshelves to Oak Bookshelf", "", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		if(renameVanillaBookshelves)
			Blocks.BOOKSHELF.setTranslationKey("oak_bookshelf");
		
		custom_bookshelf = new BlockCustomBookshelf();
		
		RecipeProcessor.addWoodReplacements(Blocks.BOOKSHELF);
		
		for(int i = 0; i < 5; i++)
			RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(custom_bookshelf, 1, i),
					"WWW", "BBB", "WWW",
					'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, i + 1),
					'B', ProxyRegistry.newStack(Items.BOOK));
		
		Ingredient wood = new BlacklistOreIngredient("plankWood", (stack) -> stack.getItem() == Item.getItemFromBlock(Blocks.PLANKS));
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.BOOKSHELF),
				"WWW", "BBB", "WWW",
				'W', wood,
				'B', ProxyRegistry.newStack(Items.BOOK));
		
		addOreDict();
	}

	private void addOreDict() {
		addOreDict("bookshelf", Blocks.BOOKSHELF);
		addOreDict("bookshelf", ProxyRegistry.newStack(custom_bookshelf, 1, OreDictionary.WILDCARD_VALUE));
		
		addOreDict("bookshelfOak", Blocks.BOOKSHELF);
		addOreDict("bookshelfSpruce", ProxyRegistry.newStack(custom_bookshelf, 1, 0));
		addOreDict("bookshelfBirch", ProxyRegistry.newStack(custom_bookshelf, 1, 1));
		addOreDict("bookshelfJungle", ProxyRegistry.newStack(custom_bookshelf, 1, 2));
		addOreDict("bookshelfAcacia", ProxyRegistry.newStack(custom_bookshelf, 1, 3));
		addOreDict("bookshelfDarkOak", ProxyRegistry.newStack(custom_bookshelf, 1, 4));
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
