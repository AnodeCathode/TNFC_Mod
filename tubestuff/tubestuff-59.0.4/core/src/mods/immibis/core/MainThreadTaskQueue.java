package mods.immibis.core;


import java.util.LinkedList;
import java.util.Queue;

import mods.immibis.core.api.porting.SidedProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MainThreadTaskQueue {
	private static Queue<Runnable> clientQueue = new LinkedList<Runnable>();
	private static Queue<Runnable> serverQueue = new LinkedList<Runnable>();
	
	private static Queue<Runnable> clientNextQueue = new LinkedList<Runnable>();
	private static Queue<Runnable> serverNextQueue = new LinkedList<Runnable>();
	
	private static Queue<Runnable> getQueue(Side side) {
		return side.isServer() ? serverQueue : clientQueue;
	}
	
	private static void swapQueue(Side side) {
		if(side.isServer()) {
			Queue<Runnable> temp = serverNextQueue;
			serverNextQueue = serverQueue;
			serverQueue = temp;
		} else {
			Queue<Runnable> temp = clientNextQueue;
			clientNextQueue = clientQueue;
			clientQueue = temp;
		}
	}
	
	private static boolean IS_DEDICATED_SERVER; 
	
	public static void enqueue(Runnable task, Side side) {
		if(side.isClient() && IS_DEDICATED_SERVER)
			throw new IllegalStateException("Cannot queue client tasks on dedicated server");
		
		Queue<Runnable> queue = getQueue(side);
		synchronized(queue) {
			queue.add(task);
		}
	}

	private static void runPending(Side side) {
		Queue<Runnable> queue = getQueue(side);
		swapQueue(side);
		while(true) {
			Runnable r;
			synchronized(queue) {
				if(queue.isEmpty())
					break;
				r = queue.poll();
			}
			r.run();
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void tickEventClient(TickEvent.ClientTickEvent event) {
		if(event.phase != Phase.START) return;
		runPending(Side.CLIENT);
	}
	
	@SubscribeEvent
	public void tickEventServer(TickEvent.ServerTickEvent event) {
		if(event.phase != Phase.START) return;
		runPending(Side.SERVER);
	}
	
	static void init() {
		IS_DEDICATED_SERVER = SidedProxy.instance.isDedicatedServer();
		FMLCommonHandler.instance().bus().register(new MainThreadTaskQueue());
	}
}
