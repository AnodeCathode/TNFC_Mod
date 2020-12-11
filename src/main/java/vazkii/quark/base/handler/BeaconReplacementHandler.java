package vazkii.quark.base.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.base.lib.LibObfuscation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class BeaconReplacementHandler {

	private static Potion[][] defaultEffectsList = null;
	private static List<Replacer> replacers;
	
	public static void parse(String[] lines) {
		replacers = new LinkedList<>();

		for(String s : lines) {
			Replacer r = Replacer.fromString(s);
			if(r != null)
				replacers.add(r);
		}
		
		commit();
	}
	
	private static void commit() {
		if(defaultEffectsList == null) {
			defaultEffectsList = new Potion[TileEntityBeacon.EFFECTS_LIST.length][2];
			for(int i = 0; i < TileEntityBeacon.EFFECTS_LIST.length; i++) {
				Potion[] a = TileEntityBeacon.EFFECTS_LIST[i];
				for(int j = 0; j < a.length && j < defaultEffectsList[i].length; j++)
					defaultEffectsList[i][j] = a[j];
			}
		}
		
		Set<Potion> validEffects = ObfuscationReflectionHelper.getPrivateValue(TileEntityBeacon.class, null, LibObfuscation.VALID_EFFECTS);
		for(Replacer r : replacers)
			validEffects.add(r.potion);
	}
	
	public static void update(TileEntityBeacon beacon) {
		for(int i = 0; i < TileEntityBeacon.EFFECTS_LIST.length; i++) {
			Potion[] a = TileEntityBeacon.EFFECTS_LIST[i];
			for(int j = 0; j < a.length && j < defaultEffectsList[i].length; j++)
				a[j] = defaultEffectsList[i][j];
		}
		
		BlockPos pos = beacon.getPos();
		World world = beacon.getWorld();
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++) {
				if(i == 0 && j == 0)
					continue;
				
				BlockPos targetPos = pos.add(i, 0, j);
				IBlockState state = world.getBlockState(targetPos);
				replacers.forEach(r -> r.replace(state));
			}
	}
	
	private static class Replacer {
		
		private final Block block;
		private final int meta, layer, effect;
		private final Potion potion;
		
		public Replacer(Block block, int meta, int layer, int effect, Potion potion) {
			this.block = block;
			this.meta = meta;
			this.layer = layer;
			this.effect = effect;
			this.potion = potion;
		}
		
		private static Replacer fromString(String s) {
			String[] tokens = s.split(",");
			if(tokens.length != 5)
				return null;
			
			try {
				Block block = Block.getBlockFromName(tokens[0]);
				int meta = Integer.parseInt(tokens[1]);
				int layer = Integer.parseInt(tokens[2]);
				int effect = Integer.parseInt(tokens[3]);
				Potion potion = Potion.getPotionFromResourceLocation(tokens[4]);
				
				if(potion == null || effect < 0 || effect > 1 || layer < 0 || layer > 3)
					return null;
				
				return new Replacer(block, meta, layer, effect, potion);
			} catch(NumberFormatException e) {
				return null;
			}
		}
		
		public void replace(IBlockState stateAt) {
			if(block == null || (stateAt.getBlock() == block && (meta == -1 || block.getMetaFromState(stateAt) == meta)))
				TileEntityBeacon.EFFECTS_LIST[layer][effect] = potion;
		}
		
	}
	
}
