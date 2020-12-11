package vazkii.quark.misc.feature;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.entity.EntitySoulPowder;
import vazkii.quark.misc.item.ItemSoulPowder;
import vazkii.quark.world.feature.Wraiths;

public class SoulPowder extends Feature {

	public static Item soul_powder;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		soul_powder = new ItemSoulPowder();
		
		String soulPowderName = "quark:soul_powder";
		EntityRegistry.registerModEntity(new ResourceLocation(soulPowderName), EntitySoulPowder.class, soulPowderName, LibEntityIDs.SOUL_POWDER, Quark.instance, 80, 10, false);
	}
	
	@Override
	public void postPreInit() {
		if(ModuleLoader.isFeatureEnabled(Wraiths.class))
			RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(soul_powder), ProxyRegistry.newStack(Wraiths.soul_bead), ProxyRegistry.newStack(Blocks.SOUL_SAND), ProxyRegistry.newStack(Blocks.SOUL_SAND), ProxyRegistry.newStack(Blocks.SOUL_SAND));
		else RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(soul_powder), ProxyRegistry.newStack(Items.MAGMA_CREAM), ProxyRegistry.newStack(Blocks.SOUL_SAND), ProxyRegistry.newStack(Blocks.SOUL_SAND), ProxyRegistry.newStack(Blocks.SOUL_SAND));
	}
	
}
