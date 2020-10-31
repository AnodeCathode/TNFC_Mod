package mods.immibis.core.multipart;

import mods.immibis.core.api.multipart.ICoverSystem;
import mods.immibis.core.api.multipart.ICoverableTile;
import mods.immibis.core.api.multipart.IMultipartRenderingBlockMarker;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MCTHooks {
	/** Somewhat equivalent to a BlockEvent.BreakEvent, but on the client.
	 * Returns true to skip normal processing.
	 */
	@SideOnly(Side.CLIENT)
	public static boolean client_onBlockBreak(int x, int y, int z) {
		EntityPlayer ply = Minecraft.getMinecraft().thePlayer;
		BlockEvent.BreakEvent evt = new BlockEvent.BreakEvent(x, y, z, ply.worldObj, ply.worldObj.getBlock(x, y, z), ply.worldObj.getBlockMetadata(x, y, z), ply);
		MultipartSystem.instance.onBlockBreak(evt);
		return evt.isCanceled();
	}
	
	@SideOnly(Side.CLIENT)
	public static void client_onBlockClicked() {
		// ensures a PacketMicroblockDigStart will be sent immediately
		MultipartSystem.instance.clearBreakingPart(Minecraft.getMinecraft().thePlayer);
		
		MultipartSystem.instance.updateBreakingPartIfOnClient();
	}
	
	private static ThreadLocal<ICoverSystem> savedCoverSystem = new ThreadLocal<>();
	private static ThreadLocal<String> lastRemovedBlockInfo = new ThreadLocal<>();
	public static void server_removeBlockStart(ItemInWorldManager m, int x, int y, int z) {
		TileEntity te = m.theWorld.getTileEntity(x, y, z);
		if(te instanceof ICoverableTile) {
			savedCoverSystem.set(((ICoverableTile)te).getCoverSystem());
		} else {
			if(savedCoverSystem.get() != null) {
				System.err.println("[Immibis Core Multipart] Previously failed to restore cover system for "+lastRemovedBlockInfo.get()+", covers were lost");
			}
			savedCoverSystem.set(null);
		}
		lastRemovedBlockInfo.set("a "+Block.blockRegistry.getNameForObject(m.theWorld.getBlock(x, y, z))+" with tile entity "+(te == null ? "null" : te.getClass().getName()));
		MultipartSystem.isDoingMultipartCompatibleBlockBreak.get().set(true);
	}
	
	public static void server_removeBlockEnd(ItemInWorldManager m, int x, int y, int z) {
		
		MultipartSystem.isDoingMultipartCompatibleBlockBreak.get().set(false);
		
		if(savedCoverSystem.get() != null) {
			
			// if a block does weird stuff on removal, better to lose covers than to overwrite it. Covers are assumed to not be the important thing.
			if(m.theWorld.getBlock(x, y, z).isAir(m.theWorld, x, y, z))
				savedCoverSystem.get().convertToContainerBlock();
			else
				System.err.println("[Immibis Core Multipart] Failed to restore cover system for "+lastRemovedBlockInfo.get()+", covers will be lost");

			savedCoverSystem.set(null);
		}
	}
	
	public static void server_harvestStart() {
		MultipartSystem.isDoingMultipartCompatibleBlockBreak.get().set(true);
	}
	
	public static void server_harvestEnd() {
		MultipartSystem.isDoingMultipartCompatibleBlockBreak.get().set(false);
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean client_postRenderBlockInWorld(RenderBlocks rb, Block block, int x, int y, int z) {
		if(block instanceof IMultipartRenderingBlockMarker) {
			if(ForgeHooksClient.getWorldRenderPass() != 0)
				return false;
			return MultipartSystem.instance.renderMultiparts(rb.blockAccess, x, y, z, rb);
		}
		return false;
	}
}
