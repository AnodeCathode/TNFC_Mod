package tnfcmod.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
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
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenBerryBushes;
import net.dries007.tfc.world.classic.worldgen.WorldGenLooseRocks;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;
import net.dries007.tfc.world.classic.worldgen.WorldGenWildCrops;
import tnfcmod.tnfcmod;

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

        if (event.getWorld().provider.getDimension() == 0)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            //Only run this 4 times a year approx. per chunk
            if (chunkDataTFC.isInitialized() && !chunkDataTFC.isSpawnProtected() && CalendarTFC.PLAYER_TIME.getTicks() - chunkDataTFC.getLastUpdateTick() > ConfigTFC.General.MISC.defaultMonthLength * ICalendar.TICKS_IN_DAY * 3)
            {
                tnfcmod.getLog().info("Adding chunk: " + event.getChunk().getPos().toString() + " to regen database");
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

                // If past the update time, then run regeneration of natural resources
                long updateDelta = CalendarTFC.PLAYER_TIME.getTicks() - chunkDataTFC.getLastUpdateTick();
                if (updateDelta > ConfigTFC.General.WORLD_REGEN.minimumTime * ICalendar.TICKS_IN_DAY && !chunkDataTFC.isSpawnProtected())
                {
                    float regenerationModifier = MathHelper.clamp((float) updateDelta / (4 * ConfigTFC.General.WORLD_REGEN.minimumTime * ICalendar.TICKS_IN_DAY), 0, 1);
                    tnfcmod.getLog().info("Regenerating chunk at " + pos.x + " " + pos.z );
                    // Loose rocks - factors in time since last update
                    if (ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0)
                    {
                        List<Tree> trees = chunkDataTFC.getValidTrees();
                        //nuke any rocks and sticks in chunk.
                        removeAllPlacedItems(event.world, pos);
                        double rockModifier = ConfigTFC.General.WORLD_REGEN.sticksRocksModifier * regenerationModifier;
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

                    chunkDataTFC.resetLastUpdateTick();
                }
                // Plants + crops. Only runs once (maximum) each year
                if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
                {
                    //Should nuke any crops in the chunk.
                    removeAllCrops(event.world, pos);
                    if (RANDOM.nextInt(20) == 0)
                    {
                        CROPS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                    }
                    //Should nuke any bushes in the chunk. For now we just leave the bushes alone.
                    //BUSH_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                    chunkDataTFC.resetLastUpdateYear();
                }

            }
        }

    }

    private static void removeAllCrops(World world, ChunkPos pos)
    {
        Map<BlockPos, TileEntity> teTargets = world.getChunk(pos.x, pos.z).getTileEntityMap();
        Map<BlockPos, TileEntity> removals = Maps.newHashMap();

        if (!teTargets.isEmpty())
        {
            for (Map.Entry<BlockPos, TileEntity> entry : teTargets.entrySet())
            {
                if (entry.getValue() instanceof TECropBase)
                {
                    removals.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (!removals.isEmpty())
        {
            for (Map.Entry<BlockPos, TileEntity> remove : removals.entrySet())
            {
                world.removeTileEntity(remove.getKey());
                world.setBlockToAir(remove.getKey());
            }
        }
    }

    private static void removeAllPlacedItems(World world, ChunkPos pos)
    {
        Map<BlockPos, TileEntity> teTargets = world.getChunk(pos.x, pos.z).getTileEntityMap();
        Map<BlockPos, TileEntity> removals = Maps.newHashMap();

        if (!teTargets.isEmpty())
        {
            for (Map.Entry<BlockPos, TileEntity> entry : teTargets.entrySet())
            {
                if (entry.getValue() instanceof TEPlacedItemFlat)
                {
                    removals.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (!removals.isEmpty())
        {
            for (Map.Entry<BlockPos, TileEntity> remove : removals.entrySet())
            {
                world.removeTileEntity(remove.getKey());
                world.setBlockToAir(remove.getKey());
            }
        }
    }
}