package mods.immibis.core.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class TPSCommand extends CommandBase {
	
	static {
		FMLCommonHandler.instance().bus().register(new TickHandler());
	}
	
	{
		TickHandler.reset();
	}
	
	public static class TickHandler {
		
		private static List<Integer> tickTime = new LinkedList<Integer>();
		private static long lastTickTime = System.nanoTime();
		
		static void reset() {
			tickTime.clear();
			lastTickTime = System.nanoTime();
		}
		
		@SubscribeEvent
		public void tick(TickEvent.ServerTickEvent event) {
			if(event.phase != Phase.START) return;
			long thisTime = System.nanoTime();
			long tickTimeNS = thisTime - lastTickTime;
			lastTickTime = thisTime;
			
			int tickTimeMS = (int)((tickTimeNS + 500000) / 1000000);
			if(tickTime.size() == 100)
				tickTime.remove(99);
			tickTime.add(0, tickTimeMS);
		}

		public static double getTPS1() {
			return 1000.0 / getTickTime();
		}
		public static int getTickTime() {
			return tickTime.get(0);
		}
		public static double getTPS(int nTicks) {
			return 1000.0 / getTickTime(nTicks);
		}
		
		public static double getLowestTPS(int nTicks) {
			return 1000.0 / getLongestTick(nTicks);
		}
		
		private static double getTickTime(int avgTicks) {
			if(avgTicks > tickTime.size())
				return getTickTime(tickTime.size());
			
			int total = 0;
			Iterator<Integer> it = tickTime.iterator();
			for(int k = 0; k < avgTicks; k++)
				total += it.next();
			
			return total / (double)avgTicks;
		}
		
		private static int getLongestTick(int nTicks) {
			if(nTicks > tickTime.size())
				return getLongestTick(tickTime.size());
			
			int max = 0;
			Iterator<Integer> it = tickTime.iterator();
			for(int k = 0; k < nTicks; k++) {
				int t = it.next();
				if(max < t) max = t;
			}
			
			return max;
		}
	}
	
	private String name;

	public TPSCommand(String tpsCommandName) {
		this.name = tpsCommandName;
	}

	@Override
	public String getCommandName() {
		return name;
	}

	@Override
	public void processCommand(ICommandSender ply, String[] astring) {
		for(int nTicks : new int[] {200, 20, 5}) {
			ply.addChatMessage(
				new ChatComponentTranslation("mods.immibis.core.tps.avg",
					new Object[] { nTicks,
						new ChatComponentTranslation("mods.immibis.core.tps.tps", new Object[]{String.format("%.2f", TickHandler.getTPS(nTicks))}),
						new ChatComponentTranslation("mods.immibis.core.tps.tps", new Object[]{String.format("%.2f", TickHandler.getLowestTPS(nTicks))})
					}
				)
			);
		}
		
		// Time between last two ticks: X (Y TPS)
		ply.addChatMessage(
			new ChatComponentTranslation("mods.immibis.core.tps.lastTime", new Object[] {
				new ChatComponentTranslation("mods.immibis.core.tps.time", new Object[]{TickHandler.getTickTime()}),
				new ChatComponentTranslation("mods.immibis.core.tps.tps", new Object[]{String.format("%.2f", TickHandler.getTPS1())})
			})
		);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1iCommandSender) {
		return true;
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/"+name;
	}

    @Override
	public int compareTo(Object par1Obj)
    {
        return ((ICommand)par1Obj).getCommandName().compareTo(this.getCommandName());
    }
}
