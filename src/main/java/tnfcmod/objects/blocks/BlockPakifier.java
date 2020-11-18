package tnfcmod.objects.blocks;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.multiplayer.WorldClient;

import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCSounds;
import tnfcmod.objects.tiles.TilePakifier;
import tnfcmod.tnfcmod;
import tnfcmod.util.SoundUtils;

import static tnfcmod.tnfcmod.MODID;

public class BlockPakifier extends Block
{

    protected String name;
    public BlockPakifier()
    {
        super(Material.CLOTH);
        name = "pakifier";
        setHardness(2.0F);
        setTickRandomly(false);
        this.setRegistryName(MODID ,name);
        setTranslationKey(MODID + "." + name);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TilePakifier();
    }


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void supressSound(PlaySoundEvent event) {
        WorldClient clientWorld = Minecraft.getMinecraft().world;
        if (clientWorld == null) return;
        ISound sound = event.getSound();
        if (sound instanceof ITickableSound) return;

        if (event.getSound().getSoundLocation() == TFCSounds.ANIMAL_ROOSTER_CRY.getSoundName()){
            AxisAlignedBB expand = new AxisAlignedBB(sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF()).grow(8, 8, 8);

            List<TilePakifier> tilePakifiers= TilePakifier.searchAABBForTiles(clientWorld, expand, TilePakifier.class, true, null);
            if (tilePakifiers.isEmpty()) return;

            float volume = 0.03F;

            event.setResultSound(new SoundUtils(sound, volume));
        }

    }

    public void registerItemModel() {

        tnfcmod.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, name);
    }

}
