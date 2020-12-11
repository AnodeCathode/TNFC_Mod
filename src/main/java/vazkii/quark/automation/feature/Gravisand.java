package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockGravisand;
import vazkii.quark.automation.entity.EntityGravisand;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.module.Feature;

public class Gravisand extends Feature {

	public static Block gravisand;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		gravisand = new BlockGravisand();
		
		String sandName = "quark:gravisand";
		EntityRegistry.registerModEntity(new ResourceLocation(sandName), EntityGravisand.class, sandName, LibEntityIDs.GRAVISAND, Quark.instance, 160, 20, true);
		
		RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(gravisand, 8),
				"SSS", "SES", "SSS",
				'S', "sand",
				'E', "enderpearl");
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
