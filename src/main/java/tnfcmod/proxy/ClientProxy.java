package tnfcmod.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import de.mennomax.astikorcarts.client.render.RenderCargoCart;
import de.mennomax.astikorcarts.client.render.RenderPlowCart;
import de.mennomax.astikorcarts.entity.EntityCargoCart;
import de.mennomax.astikorcarts.entity.EntityPlowCart;
import tnfcmod.client.render.RenderPlowCartTFC;
import tnfcmod.handlers.TNFCEntities;
import tnfcmod.objects.entities.EntityPlowCartTFC;
import tnfcmod.tnfcmod;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(tnfcmod.MODID + ":" + id, "inventory"));


    }

    public void preInit(FMLPreInitializationEvent event){
        TNFCEntities.registerRenders();
    }
    public void postInit(FMLPostInitializationEvent event){

    }
}
