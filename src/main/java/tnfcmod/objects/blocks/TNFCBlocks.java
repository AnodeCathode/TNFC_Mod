package tnfcmod.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import tnfcmod.objects.tiles.TilePlayerDetector;

import static tnfcmod.tnfcmod.MODID;

public class TNFCBlocks
{
    @GameRegistry.ObjectHolder(MODID + ":player_detector")
    public static BlockPlayerDetector blockPlayerDetector;


    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
           event.getRegistry().register(new BlockPlayerDetector());
        TileEntity.register(MODID + ":" + "player_detector", TilePlayerDetector.class);
    }


}
