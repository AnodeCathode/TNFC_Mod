package tnfcmod.objects.tiles;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import net.dries007.tfc.objects.te.TETickableBase;

import static tnfcmod.objects.blocks.BlockPlayerDetector.ENABLED;

public class TilePlayerDetector extends TETickableBase
{

    public int redstone_output = 0;
    public UUID owner;
    public String playername = "";
    private boolean enabled = false;
    private int updateTime = 0;

    public boolean isEnabled()
    {
        return enabled;
    }

    public void onPlaced(EntityLivingBase player)
    {
        if (player instanceof EntityPlayer)
        {
            owner = ((EntityPlayer) player).getGameProfile().getId(); // TODO check correctness
            playername = ((EntityPlayer) player).getGameProfile().getName();
            enabled = true;
            updateNow();
        }

        else
        {
            owner = new UUID(0, 0);
            enabled = false;
        }

    }

    public String getPlayername(){
            return playername;
    }

    @Override
    public void update()
    {

        if (!world.isRemote && --updateTime < 0)
        {
            updateTime = 20;
            updateNow();
        }
        super.update();

    }

    public void updateNow()
    {
        int old = redstone_output;
        boolean oldstate = enabled;
        redstone_output = 0;
        enabled = false;

        for (EntityPlayer pl : (List<EntityPlayer>) world.playerEntities)
        {
            if (pl.getGameProfile().getId().equals(owner))
            {
                redstone_output = 15;
                enabled = true;
                break;
            }
        }

        if (redstone_output != old || enabled != oldstate)
        {
            updateBlock();
        }
    }


    protected void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.withProperty(ENABLED, enabled));
        world.notifyBlockUpdate(pos, state, state, 3);
        world.notifyNeighborsOfStateChange(pos, state.getBlock(),true);
        markForSync();
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        try
        {
            owner = UUID.fromString(nbt.getString("owner"));
        }
        catch (IllegalArgumentException e)
        {
            owner = new UUID(0, 0);
        }
        playername = nbt.getString("playername");
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setString("owner", owner.toString());
        nbt.setString("playername", playername);
        return super.writeToNBT(nbt);
    }

    @Override
    public void onLoad()
    {
        if (!world.isRemote)
        {
            enabled = world.getBlockState(pos).getValue(ENABLED);
        }
    }


}
