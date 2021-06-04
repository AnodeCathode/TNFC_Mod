package tnfcmod.handlers;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.plants.BlockMushroomTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TECropBase;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.types.DefaultPlants;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenBerryBushes;
import net.dries007.tfc.world.classic.worldgen.WorldGenPlantTFC;
import tnfcmod.util.ConfigTNFCMod;
import tnfcmod.util.RegenRocksSticks;
import tnfcmod.util.RegenWildCrops;
import tnfcmod.util.ServerUtils;

import static net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC.WILD;
import static tnfcmod.tnfcmod.MODID;

@SuppressWarnings({"unused", "WeakerAccess"})
@Mod.EventBusSubscriber(modid = MODID)
public class RegenSurface
{

    private static final RegenRocksSticks ROCKS_GEN = new RegenRocksSticks(true);
    private static final RegenWildCrops CROPS_GEN = new RegenWildCrops();
    private static final WorldGenBerryBushes BUSH_GEN = new WorldGenBerryBushes();
    public static final WorldGenPlantTFC PLANT_GEN = new WorldGenPlantTFC();
    private static final Random RANDOM = new Random();
    private static final List<ChunkPos> POSITIONS = new LinkedList<>();


    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event)
    {

        ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.getChunk());


        //stick/rock/crop/mushroom/other? regen
        if (event.getWorld().provider.getDimension() == 0 &&  chunkDataTFC.isInitialized() && POSITIONS.size() < 1000)
        {
            //Only run this in the early months of each year
            if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
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
                ServerUtils su = new ServerUtils();
                double tps = su.getTPS(event.world, 0);
                if (tps > 16)
                {
                    ChunkPos pos = POSITIONS.remove(0);
                    Chunk chunk = event.world.getChunk(pos.x, pos.z);
                    BlockPos blockPos = pos.getBlock(0, 0, 0);
                    ChunkDataTFC chunkDataTFC = ChunkDataTFC.get(event.world, pos.getBlock(0, 0, 0));
                    IChunkProvider chunkProvider = event.world.getChunkProvider();
                    IChunkGenerator chunkGenerator = ((ChunkProviderServer) chunkProvider).chunkGenerator;


                    if (CalendarTFC.CALENDAR_TIME.getMonthOfYear().isWithin(Month.APRIL, Month.JULY) && !chunkDataTFC.isSpawnProtected() && CalendarTFC.CALENDAR_TIME.getTotalYears() > chunkDataTFC.getLastUpdateYear())
                    {
                        if (ConfigTFC.General.WORLD_REGEN.sticksRocksModifier > 0)
                        {
                            //Nuke any rocks and sticks in chunk.
                            removeAllPlacedItems(event.world, pos);
                            List<Tree> trees = chunkDataTFC.getValidTrees();
                            double rockModifier = ConfigTFC.General.WORLD_REGEN.sticksRocksModifier;
                            ROCKS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);

                            final float density = chunkDataTFC.getFloraDensity();
                            int stickDensity = 3 + (int) (4f * density + 1.5f * trees.size() * rockModifier);
                            if (trees.isEmpty())
                            {
                                stickDensity = 1 + (int) (1.5f * density * rockModifier);
                            }
                            RegenRocksSticks.generateLooseSticks(RANDOM, pos.x, pos.z, event.world, stickDensity);
                        }

                        //Nuke crops/mushrooms/dead crops (not sure the latter is working.
                        removeAllSurfaceCrap(event.world, pos);
                        removeSeedbags(event.world, pos);

                        float avgTemperature = ClimateTFC.getAvgTemp(event.world, blockPos);
                        float rainfall = ChunkDataTFC.getRainfall(event.world, blockPos);
                        float floraDensity = chunkDataTFC.getFloraDensity(); // Use for various plant based decoration (tall grass, those vanilla jungle shrub things, etc.)
                        float floraDiversity = chunkDataTFC.getFloraDiversity();
                        Plant plant = TFCRegistries.PLANTS.getValue(DefaultPlants.PORCINI);
                        PLANT_GEN.setGeneratedPlant(plant);
                        int mushroomCount = 3;
                        for (float i = RANDOM.nextInt(Math.round(mushroomCount / floraDiversity)); i < (1 + floraDensity) * 5; i++)
                        {
                            BlockPos blockMushroomPos = event.world.getHeight(blockPos.add(RANDOM.nextInt(16) + 8, 0, RANDOM.nextInt(16) + 8));
                            PLANT_GEN.generate(event.world, RANDOM, blockMushroomPos);
                        }
                        CROPS_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);



                        //Should nuke any bushes in the chunk. For now we just leave the bushes alone.
                        //BUSH_GEN.generate(RANDOM, pos.x, pos.z, event.world, chunkGenerator, chunkProvider);
                        chunkDataTFC.resetLastUpdateYear();


                    }
                    chunk.markDirty();
                    ((ChunkProviderServer) chunkProvider).queueUnload(chunk);

                }
                else //TPS too low. Just remove it and move on. Or do we leave it?
                {
                    ChunkPos pos = POSITIONS.remove(0);
                }
            }
        }

    }

    private static void removeAllSurfaceCrap(World world, ChunkPos pos)
    {
        ArrayList<BlockPos> removals = new ArrayList<>();

        int xX;
        int zZ;
        for (xX = 0; xX < 16; ++xX)
        {
            for (zZ = 0; zZ < 16; ++zZ)
            {
                BlockPos topBlock = world.getTopSolidOrLiquidBlock(pos.getBlock(xX, 0, zZ));
                //If I'm not completely missing the point, then we have the top block for each in a chunk. Which is apparently not the top solid block ffs.
                IBlockState blockstate = world.getBlockState(topBlock);
                Block block = blockstate.getBlock();
                if (!blockstate.getMaterial().isLiquid())
                {
                    if (block instanceof BlockCropDead || block instanceof BlockMushroomTFC)
                    {
                        IBlockState soil = world.getBlockState(topBlock.down());
                        if (soil.getBlock() instanceof BlockRockVariant){
                            BlockRockVariant soilRock = (BlockRockVariant) soil.getBlock();
                            //Stop removing dead crops from farmland please!
                            if (soilRock.getType() != Rock.Type.FARMLAND){
                                removals.add(topBlock);
                            }
                        }
                    }
                }

            }
        }
        //Remove all the crops
        Map<BlockPos, TileEntity> teTargets = world.getChunk(pos.x, pos.z).getTileEntityMap();

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


    private static void removeSeedbags(World world, ChunkPos pos)
    {
        ClassInheritanceMultiMap<Entity>[] targets = world.getChunk(pos.x, pos.z).getEntityLists();
        ArrayList<Entity> removals = new ArrayList<>();
        if (targets.length > 0)
        {


            //we gots some entities. Now let's see if any of them are the target
            for (int i = 0; i < targets.length; i++)
            {
                for (Entity select : targets[i])
                {
                    if (select instanceof EntityItem)
                    {
                        if (((EntityItem) select).getItem().getItem() instanceof ItemSeedsTFC)
                        {
                            //mark for destruction
                            removals.add(select);
                        }
                    }

                }
            }
            if (!removals.isEmpty())
            {
                for (Entity remove : removals)
                {
                    world.removeEntity(remove);
                }

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
