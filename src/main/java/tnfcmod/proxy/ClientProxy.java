package tnfcmod.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import tnfcmod.handlers.TNFCEntities;
import tnfcmod.qfc.module.ModuleLoader;
import tnfcmod.tnfcmod;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(tnfcmod.MODID + ":" + id, "inventory"));


    }

    @Override
    public void registerNormalItemRenderer(Item item, int meta, String id)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "normal"));
    }

    public void preInit(FMLPreInitializationEvent event){
        super.preInit(event);
        ModuleLoader.preInitClient(event);

        FMLClientHandler.instance().refreshResources(VanillaResourceType.TEXTURES);
        TNFCEntities.registerRenders();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ModuleLoader.initClient(event);
    }

    public void postInit(FMLPostInitializationEvent event){

    }
}
