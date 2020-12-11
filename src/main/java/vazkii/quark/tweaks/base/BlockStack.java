/**
 * This class was created by <TehNut>. It's distributed as
 * part of the Harvest Mod. Get the Source Code in github:
 * https://github.com/TehNut/Harvest
 *
 * Harvest is Open Source and distributed under the
 * MIT License: https://www.opensource.org/licenses/MIT
 *
 * File Created @ [17/04/2016]
 */
package vazkii.quark.tweaks.base;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockStack {

    public static final BlockStack EMPTY = new BlockStack(Blocks.AIR);

    private final Block block;
    private final int meta;
    private final IBlockState state;

    @SuppressWarnings("deprecation")
    public BlockStack(Block block, int meta) {
        this.block = block;
        this.meta = meta;
        this.state = block.getStateFromMeta(meta);
    }

    public BlockStack(Block block) {
        this(block, 0);
    }

    private static final Pattern BLOCK_STACK_PATTERN = Pattern.compile("(?:(\\w+):)?(\\w+)(?::(\\d+))?");

    @SuppressWarnings("ConstantConditions")
    public static BlockStack fromString(String key) {
        Matcher match = BLOCK_STACK_PATTERN.matcher(key);
        if (!match.matches())
            return BlockStack.EMPTY;

        String modid = match.group(1);
        String block = match.group(2);
        String metaString = match.group(3);

        int meta = metaString == null ? 0 : Integer.parseInt(metaString);

        ResourceLocation loc = new ResourceLocation(modid, block);
        Block blockInstance = Block.REGISTRY.getObject(loc);
        if (blockInstance == null)
            return BlockStack.EMPTY;

        return new BlockStack(blockInstance, meta);
    }

    public static BlockStack getStackFromPos(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return new BlockStack(state.getBlock(), state.getBlock().getMetaFromState(state));
    }

    public boolean isEmpty() {
        return block == Blocks.AIR;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public IBlockState getState() {
        return state;
    }

    @Override
    public String toString() {
        return Objects.toString(getBlock().getRegistryName()) + ":" + getMeta();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockStack that = (BlockStack) o;
        return Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}
