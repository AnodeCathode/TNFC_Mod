package vazkii.quark.world.tile;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.feature.MonsterBoxes;

import java.util.List;

public class TileMonsterBox extends TileMod implements ITickable {

	private int breakProgress;
	
	@Override
	public void update() {
		if(world.getDifficulty() == EnumDifficulty.PEACEFUL)
			return;
		
		BlockPos pos = getPos();
		
		if(world.isRemote) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			world.spawnParticle(breakProgress == 0 ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_LARGE, x + Math.random(), y + Math.random(), z + Math.random(), 0, 0, 0);
		}
		
		boolean doBreak = breakProgress > 0;
		if(!doBreak) {
			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(2.5));
			doBreak = players.size() > 0;
		}
		
		if(doBreak) {
			if(breakProgress == 0)
				world.playSound(null, pos, QuarkSounds.BLOCK_MONSTER_BOX_GROWL, SoundCategory.BLOCKS, 1F, 1F);
			
			breakProgress++;
			if(breakProgress > 40) {
				world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
				world.setBlockToAir(pos);
				spawnMobs();
			}
		}
	}
	
	private void spawnMobs() {
		if(world.isRemote)
			return;
		
		BlockPos pos = getPos();

		int mobCount = MonsterBoxes.minMobs + world.rand.nextInt(Math.max(MonsterBoxes.maxMobs - MonsterBoxes.minMobs + 1, 1));
		for(int i = 0; i < mobCount; i++) {
			Entity e;
			
			float r = world.rand.nextFloat();
			if(r < 0.1)
				e = new EntityWitch(world);
			else if(r < 0.3)
				e = new EntityCaveSpider(world);
			else e = new EntityZombie(world);
			
			double motionMultiplier = 0.4;
			e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			e.motionX = (world.rand.nextFloat() - 0.5) * motionMultiplier;
			e.motionY = (world.rand.nextFloat() - 0.5) * motionMultiplier;
			e.motionZ = (world.rand.nextFloat() - 0.5) * motionMultiplier;
			
			world.spawnEntity(e);
		}
	}

}
