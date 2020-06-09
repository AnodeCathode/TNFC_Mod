package tnfcmod.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import static net.minecraftforge.fml.relauncher.Side.SERVER;


import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenLooseRocks;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;
import tnfcmod.tnfcmod;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = tnfcmod.MODID)
public class RegenSurface
{

    private static final WorldGenLooseRocks ROCKS_GEN = new WorldGenLooseRocks(false);
    private static final Random RANDOM = new Random();
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {
        //Temporarily stolen from Terrafirmacraft. If they ever fix it then it can be removed.
        if (event.getWorld().provider.getDimension() == 0)
        {
            ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
            //Only run this once a year. Should check that LastUpdateTick exists. Otherwise other stuff. See notes.
            if (chunkDataTFC.isInitialized() && !chunkDataTFC.isSpawnProtected() && CalendarTFC.PLAYER_TIME.getTicks() - chunkDataTFC.getLastUpdateTick() > CalendarTFC.PLAYER_TIME.getTicks() + ConfigTFC.General.MISC.defaultMonthLength * ICalendar.TICKS_IN_DAY * 12)
            {
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
                // Check the rock and stickiness of the chunk and then add some?
                // If past the update time, then run some regeneration of natural resources
                long updateDelta = CalendarTFC.PLAYER_TIME.getTicks() - chunkDataTFC.getLastUpdateTick();
                if (updateDelta > ConfigTFC.General.WORLD_REGEN.minimumTime * ICalendar.TICKS_IN_DAY && !chunkDataTFC.isSpawnProtected())
                {
                    float regenerationModifier = MathHelper.clamp((float) updateDelta / (4 * ConfigTFC.General.WORLD_REGEN.minimumTime * ICalendar.TICKS_IN_DAY), 0, 1);

                    // Loose rocks - factors in time since last update
                    if (ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0)
                    {
                        double rockModifier = ConfigTFC.General.WORLD_REGEN.sticksRocksModifier * regenerationModifier;
                        ROCKS_GEN.setFactor(rockModifier);
                        ROCKS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                        int stickDensity = (int) (rockModifier * (1 + (int) (3f * chunkDataTFC.getFloraDensity())));
                        WorldGenTrees.generateLooseSticks(RANDOM, pos.x, pos.z, event.world, stickDensity);
                    }

                    chunkDataTFC.resetLastUpdateTick();
                }

            }
        }

    }
}