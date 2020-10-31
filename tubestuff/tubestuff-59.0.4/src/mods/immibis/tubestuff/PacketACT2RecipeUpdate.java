package mods.immibis.tubestuff;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.core.api.net.PacketUtils;
import mods.immibis.core.net.AbstractContainerSyncPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;


public class PacketACT2RecipeUpdate extends AbstractContainerSyncPacket {
	
	public ItemStack[][] stacks = new ItemStack[9][];
	public boolean isPacketValid;

	@Override
	public byte getID() {
		return TubeStuff.PKT_ACT2_RECIPE_UPDATE;
	}

	@Override
	public String getChannel() {
		return TubeStuff.CHANNEL;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		final int MAX = 256;
		isPacketValid = true;
		for(int k = 0; k < 9; k++) {
			int n = in.readShort();
			stacks[k] = new ItemStack[Math.min(n, MAX)];
			for(int i = 0; i < n && i < MAX; i++) {
				stacks[k][i] = PacketUtils.readItemStack(in);
				if(stacks[k][i] == null)
					isPacketValid = false;
				else {
					try {
						stacks[k][i].getUnlocalizedName();
					} catch(Throwable t) {
						isPacketValid = false;
					}
				}
			}
		}
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		for(int k = 0; k < 9; k++) {
			out.writeShort(stacks[k] == null ? 0 : stacks[k].length);
			if(stacks[k] != null)
				for(ItemStack is : stacks[k])
					PacketUtils.writeItemStack(is, out);
		}
	}

}
