package mods.immibis.core.impl;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import mods.immibis.core.MainThreadTaskQueue;
import mods.immibis.core.api.net.FakeSpecialPacket;
import mods.immibis.core.api.net.INetworkingManager;
import mods.immibis.core.api.net.IPacket;
import mods.immibis.core.api.net.IPacketMap;
import mods.immibis.core.api.net.IPacketWrapper;
import mods.immibis.core.api.net.ISimplePacket;
import mods.immibis.core.api.net.PacketCapture;
import mods.immibis.core.api.porting.SidedProxy;
import mods.immibis.core.net.PacketFragment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;

public class NetworkingManager implements INetworkingManager, ChannelInboundHandler {
	
	
	private static final AtomicInteger nextFragmentSequenceID = new AtomicInteger(0);
	
	private static final int MAX_PACKET_SIZE = Integer.MAX_VALUE; // not needed in 1.7
	private static final int FRAGMENT_SIZE = 32500;
	
	private static class PacketDecoderThread extends Thread {
		private LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
		
		public PacketDecoderThread() {
			setName("Immibis Core - asynchronous packet codec thread (low priority)");
			setDaemon(true);
			setPriority(3);
		}
		
		public void queue(Runnable task) {
			tasks.add(task);
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					tasks.take().run();
				} catch(InterruptedException e) {
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static PacketDecoderThread asyncDecoderThread;
	private static void queueTaskPDT(Runnable task) {
		PacketDecoderThread pdt;
		synchronized(NetworkingManager.class) {
			pdt = asyncDecoderThread;
			if(pdt == null) {
				pdt = asyncDecoderThread = new PacketDecoderThread();
				pdt.start();
			}
		}
		pdt.queue(task);
	}
	
	private void sendOnThisThread(IPacket packet, final NetworkManager conn, boolean isThisNotMainThread, final boolean isFromServer) {
		final String channel = packet.getChannel();
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		
		try {
			out.writeByte(packet.getID());
			
			packet.write(out);
			
			final byte[] data = bytes.toByteArray();
			
			if(isThisNotMainThread)
				MainThreadTaskQueue.enqueue(new Runnable() {
					@Override
					public void run() {
						send250(channel, data, conn, isFromServer);
					}
				}, isFromServer ? Side.SERVER : Side.CLIENT);
			else
				send250(channel, data, conn, isFromServer);
			
		} catch(IOException e) {
			FMLLog.getLogger().log(Level.ERROR, "While trying to send packet of type "+packet.getClass().getName(), e);
		} finally {
			try {
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void send(final IPacket packet, final NetworkManager conn, final boolean isFromServer) {
		if(conn == null) throw new NullPointerException("conn");

		if(packet instanceof IPacket.Asynchronous) {
			queueTaskPDT(new Runnable() {
				@Override
				public void run() {
					sendOnThisThread(packet, conn, true, isFromServer);
				}
			});
		} else
			sendOnThisThread(packet, conn, false, isFromServer);
		
	
	}
	
	
	private static Packet createCustomPayloadPacket(String channel, byte[] data, boolean isFromServer) {
		if(isFromServer)
			return new S3FPacketCustomPayload(channel, data);
		else
			return new C17PacketCustomPayload(channel, data);
	}
	
	
	private void send250(String channel, byte[] data, NetworkManager target, boolean isFromServer) {
		if(data.length <= MAX_PACKET_SIZE) {
			target.scheduleOutboundPacket(createCustomPayloadPacket(channel, data, isFromServer));
			return;
		}
		int seqID = nextFragmentSequenceID.incrementAndGet();
		
		int numFragments = (data.length + FRAGMENT_SIZE - 1) / FRAGMENT_SIZE;
		int start = 0;
		
		//System.out.println("Splitting "+p250.data.length+"-size packet into "+numFragments+" fragments");
		
		for(int k = 0; k < numFragments; k++) {
			int fragmentLen = Math.min(FRAGMENT_SIZE, data.length - start);
			
			PacketFragment fragment = new PacketFragment();
			fragment.fragmentIndex = k;
			fragment.numFragments = numFragments;
			fragment.senderSeqID = seqID;
			fragment.channel = channel;
			fragment.data = new byte[fragmentLen];
			
			System.arraycopy(data, start, fragment.data, 0, fragmentLen);
			start += fragmentLen;
			
			send250(fragment, target, isFromServer);
		}
	}
	
	private void send250(IPacket packet, NetworkManager target, boolean isFromServer) {
		ByteArrayOutputStream temp = new ByteArrayOutputStream();
		temp.write(packet.getID());
		try {
			packet.write(new DataOutputStream(temp));
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
		send250(packet.getChannel(), temp.toByteArray(), target, isFromServer);
	}

	// Always closes "in"
	public static void onReceivePacket(String channel, DataInputStream in, boolean isReceivedOnClient, ChannelHandlerContext handlerContext) {
		IPacketMap mod = channels.get(channel);
		if(mod == null) {
			System.err.println("Received fragmented packet on unknown channel '"+channel+"' from "+(isReceivedOnClient ? "client" : "server"));
			try {
				in.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return;
		}
		
		try {
			byte id = in.readByte();
			final IPacket packet = isReceivedOnClient ? mod.createS2CPacket(id) : mod.createC2SPacket(id);
			if(packet == null) {
				System.err.println("[Immibis Core] Received packet with invalid ID "+id+" (on channel "+channel+", mod "+mod+")");
				return;
			}
			packet.read(in);
			handlerContext.fireChannelRead(new IPacketWrapper(packet));
		} catch(IOException e) {
			FMLLog.getLogger().log(Level.ERROR, "While trying to receive packet on channel " + channel, e);
		} finally {
			try {
				in.close();
			} catch(Exception e) {e.printStackTrace();}
		}
	}
	
	static Map<String, IPacketMap> channels = new HashMap<String, IPacketMap>();

	@Override
	public Packet wrap(IPacket packet, boolean isFromServer) {
		String channel = packet.getChannel();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		try {
			out.writeByte(packet.getID());
			packet.write(out);
			
			byte[] data = bytes.toByteArray();
			
			return createCustomPayloadPacket(channel, data, isFromServer);
			
		} catch(IOException e) {
			FMLLog.getLogger().log(Level.ERROR, "While trying to send packet of type "+packet.getClass().getName(), e);
			return null;
		} finally {
			try {
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendToServer(IPacket packet) {
		if(SidedProxy.instance.isDedicatedServer())
			throw new IllegalStateException("can't send packets to the server on the server.");
		send250(packet, Minecraft.getMinecraft().thePlayer.sendQueue.getNetworkManager(), false);
	}

	@Override
	public void sendToClient(IPacket packet, EntityPlayer target) {
		if(!(target instanceof EntityPlayerMP))
			return;
		send250(packet, ((EntityPlayerMP)target).playerNetServerHandler.netManager, true);
	}

	@Override
	public void listen(final IPacketMap handler) {
		channels.put(handler.getChannel(), handler);
	}
	
	
	
	
	
	
	
	/* *************** SIMPLE PACKETS ***************** */
	
	private String SIMPLE_PACKET_CHANNEL = "immibis";
	
	private class SimplePacketTypeMap {
		Map<Integer, Constructor<? extends ISimplePacket>> recvMap = new HashMap<Integer, Constructor<? extends ISimplePacket>>();
		Map<Class<? extends ISimplePacket>, Integer> sendMap = new HashMap<Class<? extends ISimplePacket>, Integer>();
		int nextSendID = 2;
		
		{
			try {
				recvMap.put(1, PacketRegisterSimplePacket.class.getConstructor());
				sendMap.put(PacketRegisterSimplePacket.class, 1);
			} catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private WeakHashMap<NetworkManager, SimplePacketTypeMap> simplePacketTypeMap = new WeakHashMap<NetworkManager, SimplePacketTypeMap>();
	
	private SimplePacketTypeMap getSPTM(NetworkManager manager) {
		SimplePacketTypeMap rv = simplePacketTypeMap.get(manager);
		if(rv == null)
			simplePacketTypeMap.put(manager, rv = new SimplePacketTypeMap());
		return rv;
	}
	
	
	private class PacketRegisterSimplePacket implements ISimplePacket {
		// Associates an ID with a class
		int id;
		String clazz;
		
		@Override
		public void read(DataInputStream in) throws IOException {
			id = in.readInt();
			clazz = in.readUTF();
		}
		
		@Override
		public void write(DataOutputStream out) throws IOException {
			out.writeInt(id);
			out.writeUTF(clazz);
		}
		
		@Override 	
		public void onReceived(EntityPlayer source, NetworkManager connection) {
			Constructor<? extends ISimplePacket> constructor;
			try {
				constructor = Class.forName(clazz).asSubclass(ISimplePacket.class).getConstructor();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			getSPTM(connection).recvMap.put(id, constructor);
		}
	}
	
	private ISimplePacket unwrapSimplePacket(NetworkManager manager, byte[] data) {
		ISimplePacket p = null;
		
		try {
			SimplePacketTypeMap types = getSPTM(manager);
			
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
			try {
				short type = in.readShort();
				
				Constructor<? extends ISimplePacket> constructor = types.recvMap.get(type);
				if(constructor == null) {
					return null;
				}
				
				p = constructor.newInstance();
				p.read(in);
				
			} finally {
				in.close();
			}
			
			return p;
			
		} catch(Exception e) {
			FMLLog.getLogger().log(Level.ERROR, "While trying to receive simple packet" + (p == null ? "" : " " + p.toString()), e);
			return null;
		}
	}
	
	static {
		// TODO make simple packets work again
		/*customPacketHandlersByChannel.put(SIMPLE_PACKET_CHANNEL, new ICustomPacketHandler() {
			@Override
			public void onCustomPacket(NetworkManager connection, byte[] data, Channel nettyChannel) {
				ISimplePacket p = unwrapSimplePacket(connection, data);
				if(p != null)
					p.onReceived(from, connection);
			}
		});*/
	}

	@Override
	public void sendToServer(ISimplePacket packet) {
		NetworkManager netManager = Minecraft.getMinecraft().thePlayer.sendQueue.getNetworkManager();
		Packet wrapped = wrap(packet, netManager, false);
		if(wrapped != null)
			netManager.scheduleOutboundPacket(wrapped);
	}

	@Override
	public void sendToClient(ISimplePacket packet, EntityPlayerMP target) {
		Packet wrapped = wrap(packet, target.playerNetServerHandler.netManager, true);
		if(wrapped != null)
			target.playerNetServerHandler.netManager.scheduleOutboundPacket(wrapped);
	}

	private Packet wrap(ISimplePacket packet, NetworkManager conn, boolean isFromServer) {
		SimplePacketTypeMap sptm = getSPTM(conn);
		Integer id = sptm.sendMap.get(packet.getClass());
		if(id == null) {
			if(packet instanceof PacketRegisterSimplePacket)
				throw new AssertionError();
			
			sptm.sendMap.put(packet.getClass(), id = sptm.nextSendID++);
			
			PacketRegisterSimplePacket rp = new PacketRegisterSimplePacket();
			rp.id = id;
			rp.clazz = packet.getClass().getName();
			conn.scheduleOutboundPacket(wrap(rp, conn, isFromServer));
		}
		
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bytes);
			out.writeShort(id);
			packet.write(out);
			out.close();
			
			return createCustomPayloadPacket(SIMPLE_PACKET_CHANNEL, bytes.toByteArray(), isFromServer);
		
		} catch(IOException e) {
			FMLLog.getLogger().log(Level.ERROR, "While trying to send packet of type "+packet.getClass().getName(), e);
			return null;
		}
	}
	
	
	
	
	
	/* **** Custom Packet registration **** */
	static {
		try {
			for(Field f : EnumConnectionState.class.getDeclaredFields()) {
				f.setAccessible(true);
				if(f.getType() == Map.class && (f.getModifiers() & Modifier.STATIC) != 0 && ((Map)f.get(null)).containsKey(S3FPacketCustomPayload.class)) {
					// packet -> connectionstate map
					((Map)f.get(null)).put(IPacketWrapper.class, EnumConnectionState.PLAY);
				}
			}
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	/* **** ChannelInboundHandler registration *******/
	
	{
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent evt) {
		ChannelPipeline pipeline = evt.manager.channel().pipeline();
		// NB. With Cauldron and BungeeCord, we might get two events for the same pipeline.
		if(pipeline.names().contains("immibis core mod packet handler"))
			pipeline.remove("immibis core mod packet handler");
		if(pipeline.names().contains("immibis core special packet handler"))
			pipeline.remove("immibis core special packet handler");
		if(pipeline.names().contains("immibis core packet capture"))
			pipeline.remove("immibis core packet capture");
		pipeline.addBefore("fml:packet_handler", "immibis core mod packet handler", this);
		pipeline.addLast("immibis core special packet handler", new FakeSpecialPacketHandler());
		pipeline.addFirst("immibis core packet capture", new PacketCaptureHandler());
		
	}
	
	@SubscribeEvent
	public void onServerConnection(FMLNetworkEvent.ServerConnectionFromClientEvent evt) {
		ChannelPipeline pipeline = evt.manager.channel().pipeline();
		
		// NB. With Cauldron and BungeeCord, we might get two events for the same pipeline.
		if(pipeline.names().contains("immibis core mod packet handler"))
			pipeline.remove("immibis core mod packet handler");
		if(pipeline.names().contains("immibis core special packet handler"))
			pipeline.remove("immibis core special packet handler");
		if(pipeline.names().contains("immibis core packet capture"))
			pipeline.remove("immibis core packet capture");
		
		pipeline.addBefore("fml:packet_handler", "immibis core mod packet handler", this);
		pipeline.addLast("immibis core special packet handler", new FakeSpecialPacketHandler());
		pipeline.addFirst("immibis core packet capture", new PacketCaptureHandler());
	}
	
	private static class FakeSpecialPacketHandler extends ChannelOutboundHandlerAdapter {
		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			if(msg instanceof FakeSpecialPacket)
				((FakeSpecialPacket)msg).send(ctx, promise);
			else
				ctx.write(msg, promise);
		}
	}
	
	
	
	/* **** ChannelInboundHandler *******/

	@Override
	public void handlerAdded(ChannelHandlerContext arg0) throws Exception {
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext arg0) throws Exception {
	}

	@Override
	public void channelActive(ChannelHandlerContext arg0) throws Exception {
		arg0.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext arg0) throws Exception {
		arg0.fireChannelInactive();
	}

	@Override
	public void channelRead(ChannelHandlerContext arg0, Object arg1) throws Exception {
		if(arg1 instanceof C17PacketCustomPayload) {
			C17PacketCustomPayload packet = ((C17PacketCustomPayload)arg1);
			if(channels.containsKey(packet.func_149559_c())) {
				onReceivePacket(packet.func_149559_c(), new DataInputStream(new ByteArrayInputStream(packet.func_149558_e())), false, arg0);
				return;
			}
		}
		if(arg1 instanceof S3FPacketCustomPayload) {
			S3FPacketCustomPayload packet = ((S3FPacketCustomPayload)arg1);
			if(channels.containsKey(packet.func_149169_c())) {
				onReceivePacket(packet.func_149169_c(), new DataInputStream(new ByteArrayInputStream(packet.func_149168_d())), true, arg0);
				return;
			}
		}
		arg0.fireChannelRead(arg1);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext arg0) throws Exception {
		arg0.fireChannelReadComplete();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext arg0) throws Exception {
		arg0.fireChannelRegistered();
	}

	@Override
	@Deprecated
	public void channelUnregistered(ChannelHandlerContext arg0) throws Exception {
		arg0.fireChannelUnregistered();
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext arg0) throws Exception {
		arg0.fireChannelWritabilityChanged();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {
		arg0.fireExceptionCaught(arg1);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext arg0, Object arg1) throws Exception {
		arg0.fireUserEventTriggered(arg1);
	}
	
	
	
	
	
	/* **** PacketCapture methods (NOT IMPLEMENTED) **** */
	
	private class PacketCaptureHandler extends ChannelOutboundHandlerAdapter {
		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			/*synchronized(packetCaptureStack) {
				Deque<PacketCapture> thisStack = packetCaptureStack.get(ctx.channel().eventLoop());
				if(thisStack != null) {
					PacketCapture topCapture = thisStack.peek();
					if(topCapture != null) {
						if(topCapture == 
					}
				}
			}*/
			ctx.write(msg, promise);
		}
	}
	
	private WeakHashMap<EventLoop, Deque<PacketCapture>> packetCaptureStack = new WeakHashMap<>();
	@Override
	public void startCapturingPackets(PacketCapture cap) {
		//packetCaptureStack.addLast(cap);
	}
	@Override
	public void stopCapturingPackets(PacketCapture cap) {
		//if(packetCaptureStack.peekLast() == cap) {
		//	packetCaptureStack.pollLast();
		//} else {
		//	throw new AssertionError("top of stack is '"+packetCaptureStack.peekLast()+"' but argument is '"+cap+"'");
		//}
	}
	
	
}
