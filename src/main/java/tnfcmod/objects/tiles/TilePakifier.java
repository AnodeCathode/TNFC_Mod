package tnfcmod.objects.tiles;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TilePakifier extends TileEntity
{


    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> List<T> searchAABBForTiles(World world, AxisAlignedBB area, Class<T> tileClazz, boolean firstOnly, List<T> list)
    {
        int x0 = ((int) Math.floor(area.minX) >> 4);
        int x1 = ((int) Math.ceil(area.maxX) >> 4);
        int z0 = ((int) Math.floor(area.minZ) >> 4);
        int z1 = ((int) Math.ceil(area.maxZ) >> 4);

        if (list == null) list = Lists.newArrayList();

        for (int x = x0; x <= x1; x++)
        {
            for (int z = z0; z <= z1; z++)
            {
                Chunk chunk = world.getChunk(x, z);
                for (Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntityMap().entrySet())
                {
                    BlockPos pos = entry.getKey();
                    if (tileClazz == entry.getValue().getClass() && area.contains(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)))
                    {
                        list.add((T) entry.getValue());
                        if (firstOnly) return list;
                    }
                }
            }
        }
        return list;
    }
}
