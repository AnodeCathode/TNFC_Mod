/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [May 20, 2019, 10:26 AM (EST)]
 */
package vazkii.quark.tweaks.block;

import net.minecraft.block.BlockSlime;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.tweaks.feature.SpringySlime;

import javax.annotation.Nonnull;

public class BlockSpringySlime extends BlockSlime {
	public BlockSpringySlime() {
		setSoundType(SoundType.SLIME);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entity) {
		SpringySlime.collideWithSlimeBlock(pos, entity);
	}

	@Override
	public void onLanded(@Nonnull World worldIn, Entity entityIn) {
		// Override slime block behavior, as it's handled in SpringySlime
		entityIn.motionY = 0.0;
	}
}
