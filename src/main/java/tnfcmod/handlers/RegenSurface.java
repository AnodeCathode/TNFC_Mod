package tnfcmod.handlers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import net.dries007.tfc.ConfigTFC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import net.dries007.tfc.api.types.*;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import tnfcmod.util.ServerUtils;

import static com.google.common.math.DoubleMath.mean;
import static tnfcmod.tnfcmod.MODID;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = MODID)
public class RegenSurface
{

    private static final Random RANDOM = new Random();
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();
    private static Stream<EntityEntry> LIVESTOCK = null;


    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {

        ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());
        //livestock regen!
        if (event.getWorld().provider.getDimension() == 0 &&  chunkDataTFC.isInitialized() && POSITIONS.size() < 1000)
        {
            //Only run this in the early months of each year
            if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
            {
                POSITIONS.add(event.getChunk().getPos());
            }
        }
    }

    public static double getTPS(World world, int dimId)
    {
        if (world == null || world.getMinecraftServer() == null) return -1D;
        double worldTickTime = mean(world.getMinecraftServer().worldTickTimes.get(dimId)) * 1.0E-6D;
        return Math.min(1000.0D / worldTickTime, 20.0D);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {

        if (!event.world.isRemote && event.phase == TickEvent.Phase.END)
        {
            if (LIVESTOCK == null)
                LIVESTOCK = ForgeRegistries.ENTITIES.getValuesCollection().stream().filter((x) -> {
                    if (ICreatureTFC.class.isAssignableFrom(x.getEntityClass())) {
                        Entity ent = x.newInstance(event.world);
                        return ent instanceof ILivestock;
                    }
                    return false;
                });
            if (!POSITIONS.isEmpty() && CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY))
            {
                double tps = getTPS(event.world, 0);
                ChunkPos pos = POSITIONS.remove(0);
                if (tps > 16)
                {
                    Chunk chunk = event.world.getChunk(pos.x, pos.z);
                    ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world, pos.getBlock(0, 0, 0));
                    IChunkProvider chunkProvider = event.world.getChunkProvider();

                    if (!chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear() && countLivestock(event.world, pos) == 0)
                    {
                        int worldX = pos.x << 4;
                        int worldZ = pos.z << 4;
                        BlockPos blockpos = new BlockPos(worldX, 0, worldZ);
                        Biome biome = event.world.getBiome(blockpos.add(16, 0, 16));
                        regenLivestock(event.world, biome, worldX + 8, worldZ + 8, 16, 16, RANDOM);
                    }
                    chunkDataTFC.resetLastUpdateYear();
                    chunk.markDirty();
                    ((ChunkProviderServer) chunkProvider).queueUnload(chunk);
                }
            }
        }
    }

    private static int countLivestock(World world, ChunkPos pos)
    {
        int count = 0;
        for (ClassInheritanceMultiMap<Entity> target : world.getChunk(pos.x, pos.z).getEntityLists())
            count += target.stream().filter(entity -> {
                return entity instanceof ILivestock;
            }).count();
        return count;
    }

    public static void regenLivestock(World worldIn, Biome biomeIn, int centerX, int centerZ, int diameterX, int diameterZ, Random randomIn) {
        BlockPos chunkBlockPos = new BlockPos(centerX, 0, centerZ);
        float temperature = ClimateTFC.getAvgTemp(worldIn, chunkBlockPos);
        float rainfall = ChunkDataTFC.getRainfall(worldIn, chunkBlockPos);
        float floraDensity = ChunkDataTFC.getFloraDensity(worldIn, chunkBlockPos);
        float floraDiversity = ChunkDataTFC.getFloraDiversity(worldIn, chunkBlockPos);

        Stream<EntityEntry> stream = LIVESTOCK.filter((x) -> {
            Entity ent = x.newInstance(worldIn);
            int weight = ((ICreatureTFC)ent).getSpawnWeight(biomeIn, temperature, rainfall, floraDensity, floraDiversity);
            return weight > 0 && randomIn.nextInt(weight) == 0;
        });
        int index = randomIn.nextInt((int)stream.count());
        stream.skip(index).findFirst().ifPresent((entityEntry) -> {
            doGroupSpawning(entityEntry, worldIn, centerX, centerZ, diameterX, diameterZ, randomIn);
        });
    }

    private static void doGroupSpawning(EntityEntry entityEntry, World worldIn, int centerX, int centerZ, int diameterX, int diameterZ, Random randomIn) {
        List<EntityLiving> group = new ArrayList<>();
        EntityLiving creature = (EntityLiving)entityEntry.newInstance(worldIn);
        if (creature instanceof ICreatureTFC) {
            ICreatureTFC creatureTFC = (ICreatureTFC)creature;
            int fallback = 5;
            int individuals = Math.max(1, creatureTFC.getMinGroupSize()) + randomIn.nextInt(creatureTFC.getMaxGroupSize() - Math.max(0, creatureTFC.getMinGroupSize() - 1));

            while(individuals > 0) {
                int j = centerX + randomIn.nextInt(diameterX);
                int k = centerZ + randomIn.nextInt(diameterZ);
                BlockPos blockpos = worldIn.getTopSolidOrLiquidBlock(new BlockPos(j, 0, k));
                creature.setLocationAndAngles((double)((float)j + 0.5F), (double)blockpos.getY(), (double)((float)k + 0.5F), randomIn.nextFloat() * 360.0F, 0.0F);
                if (creature.getCanSpawnHere()) {
                    if (ForgeEventFactory.canEntitySpawn(creature, worldIn, (float)j + 0.5F, (float)blockpos.getY(), (float)k + 0.5F, (MobSpawnerBaseLogic)null) == Event.Result.DENY) {
                        --fallback;
                        if (fallback > 0) {
                            continue;
                        }
                        break;
                    } else {
                        fallback = 5;
                        worldIn.spawnEntity(creature);
                        group.add(creature);
                        creature.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(creature)), (IEntityLivingData)null);
                        --individuals;
                        if (individuals > 0) {
                            creature = (EntityLiving)entityEntry.newInstance(worldIn);
                            creatureTFC = (ICreatureTFC)creature;
                        }
                    }
                } else {
                    --fallback;
                    if (fallback <= 0) {
                        break;
                    }
                }
            }

            creatureTFC.getGroupingRules().accept(group, randomIn);
        }
    }
}
