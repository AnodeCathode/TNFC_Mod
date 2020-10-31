package mods.immibis.core.net;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.immibis.core.ImmibisCore;

public class PacketButtonPress extends AbstractContainerSyncPacket {
	
	public int buttonID;
	
	public PacketButtonPress(int button) {
		buttonID = button;
	}

	@Override
	public byte getID() {
		return ImmibisCore.PACKET_TYPE_C2S_BUTTON_PRESS;
	}

	@Override
	public void read(DataInputStream in) throws IOException {
		buttonID = in.readInt();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(buttonID);
	}
	
	@Override
	public String getChannel() {
		return ImmibisCore.CHANNEL;
	}
	
}
