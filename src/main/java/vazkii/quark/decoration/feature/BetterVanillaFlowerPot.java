package vazkii.quark.decoration.feature;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.client.RetexturedModel;
import vazkii.quark.base.handler.OverrideRegistryHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCustomFlowerPot;
import vazkii.quark.decoration.client.state.FlowerPotStateMapper;

public class BetterVanillaFlowerPot extends Feature {

	private static BlockCustomFlowerPot flowerPot;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		flowerPot = new BlockCustomFlowerPot();
		flowerPot.setTranslationKey("flowerPot");
		OverrideRegistryHandler.registerBlock(flowerPot, "flower_pot");
	}

	@Override
	public void init() {
		ColoredFlowerPots.loadFlowersFromConfig();
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "inspirations" };
	}

	@Override
	public String getFeatureDescription() {
		return "Adds the colored flower pot features of supporting additional flowers and comparator power to the vanilla flower pot using a block substitution";
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@SubscribeEvent
	public void onModelRegister(ModelRegistryEvent event) {
		ModelLoader.setCustomStateMapper(flowerPot, flowerPot.getStateMapper());
	}

	@SubscribeEvent
	public void onModelRegister(ColorHandlerEvent.Block event) {
		event.getBlockColors().registerBlockColorHandler(flowerPot.getBlockColor(), flowerPot);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation location = FlowerPotStateMapper.LOCATION;
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().getObject(location);
		IBakedModel finalModel = new RetexturedModel(standard, model, DefaultVertexFormats.BLOCK, "plant");
		event.getModelRegistry().putObject(location, finalModel);
	}
}
