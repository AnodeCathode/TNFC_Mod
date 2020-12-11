package vazkii.quark.experimental.features;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockModSlab;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.experimental.block.BlockFramed;
import vazkii.quark.experimental.block.BlockFramedSlab;
import vazkii.quark.experimental.client.model.FramedBlockModel;
import vazkii.quark.experimental.tile.TileFramed;

public class FramedBlocks extends Feature {

	public static Block frame;
	public static BlockModSlab frameSlab;
	public static BlockModSlab frameSlabDouble;

	public static boolean setFrame(World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand) {
		TileFramed tile = (TileFramed) worldIn.getTileEntity(pos);

		ItemStack stack = playerIn.getHeldItem(hand);
		if(stack.getItem() instanceof ItemBlock && tile != null)
			tile.setInventorySlotContents(0, stack.copy());

		worldIn.markBlockRangeForRenderUpdate(pos, pos);

		return true;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		frame = new BlockFramed();
		
		frameSlab = new BlockFramedSlab(false);
		frameSlabDouble = new BlockFramedSlab(true);
		BlockModSlab.initSlab(frame, 0, frameSlab, frameSlabDouble);
		
		registerTile(TileFramed.class, "framed");
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelBake(ModelBakeEvent event) {
		applyCustomModel(event, "frame", "normal");
		
		applyCustomModel(event, "frame_slab", "half=top");
		applyCustomModel(event, "frame_slab", "half=bottom");
		applyCustomModel(event, "frame_slab", "normal");
		
		applyCustomModel(event, "frame_slab_double", "normal");
		applyCustomModel(event, "frame_slab_double", "prop=blarg");
	}
	
	private void applyCustomModel(ModelBakeEvent event, String modelName, String prop) {
		ModelResourceLocation location = new ModelResourceLocation(new ResourceLocation(LibMisc.MOD_ID, modelName), prop);
		IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Error loading model for " + location);
		IBakedModel standard = event.getModelRegistry().getObject(location);
		IBakedModel finalModel = new FramedBlockModel(standard, model);
		event.getModelRegistry().putObject(location, finalModel);
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
