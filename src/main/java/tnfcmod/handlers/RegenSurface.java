package tnfcmod.handlers;

import java.util.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenBerryBushes;
import net.dries007.tfc.world.classic.worldgen.WorldGenLooseRocks;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;
import net.dries007.tfc.world.classic.worldgen.WorldGenWildCrops;

import static net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC.WILD;
import static tnfcmod.tnfcmod.MODID;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = MODID)
public class RegenSurface
{

    private static final WorldGenLooseRocks ROCKS_GEN = new WorldGenLooseRocks(true);
    private static final WorldGenWildCrops CROPS_GEN = new WorldGenWildCrops();
    private static final WorldGenBerryBushes BUSH_GEN = new WorldGenBerryBushes();
    private static final Random RANDOM = new Random();
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {

        if (event.getWorld().provider.getDimension() == 0 && ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0 && POSITIONS.size() < 100)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            //Only run this in the early months of each year
            if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
            {
                //tnfcmod.getLog().info("Adding chunk: " + event.getChunk().getPos().toString() + " to regen database");
                POSITIONS.add(event.getChunk().getPos());
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            if (!POSITIONS.isEmpty())
            {
                ChunkPos pos = POSITIONS.remove(0);
                ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world, pos.getBlock(0, 0, 0));
                IChunkProvider chunkProvider = event.world.getChunkProvider();
                IChunkGenerator chunkGenerator = ((ChunkProviderServer) chunkProvider).chunkGenerator;

                // Crops, sticks and rocks all regenerate once a year in the spring. Cause the spring thaw or something.
                if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
                {
                    //tnfcmod.getLog().info("Regenerating chunk at " + pos.x + " " + pos.z );
                    // Check server performance here and cancel if no tick budget? Also check if the chunk is loaded?
                    if (ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0)
                    {
                        //Nuke any rocks and sticks in chunk.
                        removeAllPlacedItems(event.world, pos);
                        List<Tree> trees = chunkDataTFC.getValidTrees();
                        double rockModifier = ConfigTFC.General.WORLD_REGEN.sticksRocksModifier;
                        ROCKS_GEN.setFactor(rockModifier);
                        ROCKS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                        final float density = chunkDataTFC.getFloraDensity();
                        int stickDensity = 3 + (int) (4f * density + 1.5f * trees.size() * rockModifier);
                        if (trees.isEmpty())
                        {
                            stickDensity = 1 + (int) (1.5f * density * rockModifier);
                        }
                        WorldGenTrees.generateLooseSticks(RANDOM, pos.x, pos.z, event.world, stickDensity);
                    }

                    //Nuke any crops in the chunk.
                    removeAllCrops(event.world, pos);
                    if (RANDOM.nextInt(5) == 0) //With dead crops not being removed, we're going to just add some odds.
                                                  // These odds are stacked with the overall crop gen odds.
                    {
                        CROPS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                    }


                    //Should nuke any bushes in the chunk. For now we just leave the bushes alone.
                    //BUSH_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                    chunkDataTFC.resetLastUpdateYear();

                //Need to add an else in here. If it doesn't meet any of the criteria, then mark it updated and move on. Need some nuance work on that though.

                }

            }
        }

    }

    private static void removeAllCrops(World world, ChunkPos pos)
    {
        //If we want to remove the dead crops, then we need something better
        Map<BlockPos, TileEntity> teTargets = world.getChunk(pos.x, pos.z).getTileEntityMap();
        ArrayList<BlockPos> removals = new ArrayList<>();

        if (!teTargets.isEmpty())
        {
            for (Map.Entry<BlockPos, TileEntity> entry : teTargets.entrySet())
            {
                if (entry.getValue() instanceof TECropBase)
                {
                    IBlockState bs = world.getBlockState(entry.getKey());
                    boolean isWild = bs.getValue(WILD);
                    if (isWild){
                        removals.add(entry.getKey());
                    }

                }
            }
        }
        if (!removals.isEmpty())
        {

           for (BlockPos remove : removals)
            {
                world.removeTileEntity(remove);
                world.setBlockToAir(remove);
            }
        }
    }

    private static void removeAllPlacedItems(World world, ChunkPos pos)
    {
        Map<BlockPos, TileEntity> teTargets = world.getChunk(pos.x, pos.z).getTileEntityMap();
        ArrayList<BlockPos> removals =  new ArrayList<>();

        if (!teTargets.isEmpty())
        {
            for (Map.Entry<BlockPos, TileEntity> entry : teTargets.entrySet())
            {
                if (entry.getValue() instanceof TEPlacedItemFlat)
                {
                    removals.add(entry.getKey());
                }
            }
        }
        if (!removals.isEmpty())
        {
            for (BlockPos remove : removals)
            {
                world.removeTileEntity(remove);
                world.setBlockToAir(remove);
            }
        }
    }
}