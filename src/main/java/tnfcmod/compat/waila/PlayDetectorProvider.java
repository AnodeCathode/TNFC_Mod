package tnfcmod.compat.waila;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;
import net.dries007.tfc.util.Helpers;
import tnfcmod.objects.tiles.TilePlayerDetector;

public class PlayDetectorProvider implements IWailaBlock
{
    public PlayDetectorProvider() {
    }

    @Nonnull
    public List<String> getTooltip(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt) {
        List<String> currentTooltip = new ArrayList();
        TilePlayerDetector te = (TilePlayerDetector) Helpers.getTE(world, pos, TilePlayerDetector.class);
        if (te != null) {
            boolean isEnabled = te.isEnabled();
            String player = te.getPlayername();
            if (isEnabled)
            {
                currentTooltip.add((new TextComponentTranslation("waila.tnfc.devices.playerdetector_online", new Object[] {player})).getFormattedText());
            }
            else
                {
                    currentTooltip.add((new TextComponentTranslation("waila.tnfc.devices.playerdetector_offline", new Object[]{player})).getFormattedText());
                }

        }


        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(TilePlayerDetector.class);
    }

}
