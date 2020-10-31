package mods.immibis.tubestuff;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EntityPlayerFakeTS extends EntityPlayer {
	
	public EntityPlayerFakeTS(World w) {
		super(w, new GameProfile(UUID.fromString("88B1FA14-0B07-3818-A33A-75735EE8406D"), "[TubeStuff]")); 
	}
	
	@Override
	public boolean canCommandSenderUseCommand(int i, String s) {return false;}
	@Override
	public ChunkCoordinates getPlayerCoordinates() {return new ChunkCoordinates(0, 0, 0);}
	@Override
	public void addChatMessage(IChatComponent chatmessagecomponent) {}

	@Override
	public void openGui(Object mod, int modGuiId, World world, int x, int y, int z) {
	}
}
