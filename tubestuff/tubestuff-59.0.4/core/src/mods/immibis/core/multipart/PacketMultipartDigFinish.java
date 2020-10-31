package mods.immibis.core.multipart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.multipart.PartCoordinates;
import mods.immibis.core.api.net.IPacket;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Sent by the client when it thinks it has finished breaking a part.
 * 
 * The server either does nothing (if the client prediction was correct)
 * or re-sends the block (if the client prediction was wrong).
 */
public class PacketMultipartDigFinish implements IPacket {
	public int x, y, z, part;
	public boolean isCSPart;

	public PacketMultipartDigFinish(PartCoordinates coord) {
		this.x = coord.x;
		this.y = coord.y;
		this.z = coord.z;
		this.part = coord.part;
		this.isCSPart = coord.isCoverSystemPart;
	}
	
	public PacketMultipartDigFinish() {}

	@Override
	public byte getID() {
		return ImmibisCore.PACKET_TYPE_C2S_MULTIPART_DIG_FINISH;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		part = in.readInt();
		isCSPart = in.readBoolean();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeInt(y);
		out.writeInt(z);
		out.writeInt(part);
		out.writeBoolean(isCSPart);
	}

	@Override
	public void onReceived(EntityPlayer source) {
		if(source != null) {
			if(!ImmibisCore.multipartSystem.didClientJustBreakPart(source, new PartCoordinates(x, y, z, part, isCSPart))) {
				// this resends to all nearby players.
				// TODO change this if someone decides to exploit it to start DoSing other players
				// although there are easier ways to DoS players e.g. big flashing CC monitors
				source.worldObj.markBlockForUpdate(x, y, z);
			}
		}
	}
	
	@Override
	public String getChannel() {
		return ImmibisCore.CHANNEL;
	}
}
