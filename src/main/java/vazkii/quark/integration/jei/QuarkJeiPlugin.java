package vazkii.quark.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.client.gui.GuiButtonTrash;
import vazkii.quark.management.feature.DeleteItems;
import vazkii.quark.misc.recipe.ElytraDuplicationRecipe;
import vazkii.quark.oddities.client.gui.GuiBackpackInventory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;

@JEIPlugin
public class QuarkJeiPlugin implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		try {
			registry.addGlobalGuiHandlers(new GlobalTrashGuiHandler());
			registry.addRecipeClickArea(GuiBackpackInventory.class, 137, 29, 10, 13, VanillaRecipeCategoryUid.CRAFTING);
			registry.getRecipeTransferRegistry().addRecipeTransferHandler(
					new BackpackRecipeTransferHandler(registry.getJeiHelpers().recipeTransferHandlerHelper()), VanillaRecipeCategoryUid.CRAFTING);

		} catch (RuntimeException | LinkageError ignored) {
			// only JEI 4.14.0 or higher supports addGlobalGuiHandlers
			// ignore failures here to let Quark work with older versions of JEI
		}


		registry.handleRecipes(ElytraDuplicationRecipe.class, ElytraDuplicationRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
	}

	private static class GlobalTrashGuiHandler implements IGlobalGuiHandler {
		@Override
		@Nonnull
		public Collection<Rectangle> getGuiExtraAreas() {
			if(ModuleLoader.isFeatureEnabled(DeleteItems.class)) {
				GuiButtonTrash trash = DeleteItems.getTrash();
				if(trash != null) {
					Rectangle trashArea = new Rectangle(trash.x, trash.y, trash.width, trash.height);
					return Collections.singleton(trashArea);
				}
			}
			return Collections.emptySet();
		}
	}
}
