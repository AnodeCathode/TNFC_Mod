package vazkii.quark.oddities.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.oddities.block.BlockPipe;
import vazkii.quark.oddities.feature.Pipes;

import javax.annotation.Nonnull;
import java.util.*;

public class TilePipe extends TileSimpleInventory implements ITickable {

	private static final String TAG_PIPE_ITEMS = "pipeItems";

	private boolean needsSync = false;
	private boolean iterating = false;
	public final List<PipeItem> pipeItems = new LinkedList<>();
	public final List<PipeItem> queuedItems = new LinkedList<>();

	@SuppressWarnings("MagicConstant")
	public static boolean isTheGoodDay(World world) {
		Calendar calendar = world.getCurrentDate();

		return calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DAY_OF_MONTH) == 1;
	}

	@Override
	public void update() {
		if(!isPipeEnabled() && world.getTotalWorldTime() % 10 == 0 && world instanceof WorldServer) 
			((WorldServer) world).spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0);

		IBlockState blockAt = world.getBlockState(pos);
		if (isPipeEnabled() && blockAt.getBlock() instanceof BlockPipe) {
			IBlockState actualState = blockAt.getActualState(world, pos);
			for (EnumFacing side : EnumFacing.VALUES) {
				BlockPos offset = pos.offset(side);
				if (world.getBlockState(offset).getBlockFaceShape(world, offset, side.getOpposite()) != BlockFaceShape.UNDEFINED)
					continue;

				if (!world.isRemote && BlockPipe.getType(actualState, side) == null) {
					double minX = pos.getX() + 0.25 + 0.5 * Math.min(0, side.getXOffset());
					double minY = pos.getY() + 0.25 + 0.5 * Math.min(0, side.getYOffset());
					double minZ = pos.getZ() + 0.25 + 0.5 * Math.min(0, side.getZOffset());
					double maxX = pos.getX() + 0.75 + 0.5 * Math.max(0, side.getXOffset());
					double maxY = pos.getY() + 0.75 + 0.5 * Math.max(0, side.getYOffset());
					double maxZ = pos.getZ() + 0.75 + 0.5 * Math.max(0, side.getZOffset());

					EnumFacing opposite = side.getOpposite();

					boolean any = false;
					for (EntityItem item : world.getEntitiesWithinAABB(EntityItem.class,
							new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ), (entity) -> entity != null &&
									entity.isEntityAlive() && EnumFacing.getFacingFromVector((float) entity.motionX, (float) entity.motionY, (float) entity.motionZ) == opposite)) {
						passIn(item.getItem().copy(), side);
						if (Pipes.doPipesWhoosh) {
							if (isTheGoodDay(world))
								world.playSound(null, item.posX, item.posY, item.posZ, QuarkSounds.BLOCK_PIPE_PICKUP_LENNY, SoundCategory.BLOCKS, 1f, 1f);
							else
								world.playSound(null, item.posX, item.posY, item.posZ, QuarkSounds.BLOCK_PIPE_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
						}

						any = true;
						item.setDead();
					}

					if (any)
						sync();
				}
			}
		}

		int currentOut = getComparatorOutput();

		if(!pipeItems.isEmpty()) {
			if(Pipes.maxPipeItems > 0 && pipeItems.size() > Pipes.maxPipeItems && !world.isRemote) {
				world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
				dropItem(new ItemStack(getBlockType()));
				world.setBlockToAir(getPos());
			}

			ListIterator<PipeItem> itemItr = pipeItems.listIterator();
			iterating = true;
			needsSync = false;
			while(itemItr.hasNext()) {
				PipeItem item = itemItr.next();
				EnumFacing lastFacing = item.outgoingFace;
				if(item.tick(this)) {
					needsSync = true;
					itemItr.remove();

					if (item.valid)
						passOut(item);
					else {
						dropItem(item.stack, lastFacing, true);
					}
				}
			}
			iterating = false;

			pipeItems.addAll(queuedItems);
			if(needsSync || !queuedItems.isEmpty())
				sync();
			needsSync = false;
			queuedItems.clear();
		}

		if(getComparatorOutput() != currentOut)
			world.updateComparatorOutputLevel(getPos(), getBlockType());
	}

	public int getComparatorOutput() {
		return Math.min(15, pipeItems.size());
	}

	public Iterator<PipeItem> getItemIterator() {
		return pipeItems.iterator(); 
	}

	public boolean passIn(ItemStack stack, EnumFacing face, long seed, int time) {
		PipeItem item = new PipeItem(stack, face, seed);
		if(!iterating) {
			int currentOut = getComparatorOutput();
			pipeItems.add(item);
			item.timeInWorld = time;
			if(getComparatorOutput() != currentOut)
				world.updateComparatorOutputLevel(getPos(), getBlockType());
		} else queuedItems.add(item);

		return true;
	}

	public boolean passIn(ItemStack stack, EnumFacing face) {
		return passIn(stack, face, world.rand.nextLong(), 0);
	}

	protected void passOut(PipeItem item) {
		BlockPos targetPos = getPos().offset(item.outgoingFace);
		TileEntity tile = world.getTileEntity(targetPos);
		boolean did = false;
		if(tile != null) {
			if(tile instanceof TilePipe)
				did = ((TilePipe) tile).passIn(item.stack, item.outgoingFace.getOpposite(), item.rngSeed, item.timeInWorld);
			else if (!world.isRemote) {
				ItemStack result = putIntoInv(item.stack, tile, item.outgoingFace.getOpposite(), false);
				if(result.getCount() != item.stack.getCount()) {
					did = true;
					if(!result.isEmpty())
						bounceBack(item, result);
				}
			}
		}

		if(!did)
			bounceBack(item, null);
	}

	private void bounceBack(PipeItem item, ItemStack stack) {
		if(!world.isRemote)
			passIn(stack == null ? item.stack : stack, item.outgoingFace, item.rngSeed, item.timeInWorld);
	}

	public void dropItem(ItemStack stack) {
		dropItem(stack, null, false);
	}

	public void dropItem(ItemStack stack, EnumFacing facing, boolean playSound) {
		if(!world.isRemote) {
			double posX = pos.getX() + 0.5;
			double posY = pos.getY() + 0.25;
			double posZ = pos.getZ() + 0.5;

			if (facing != null) {
				posX -= facing.getXOffset() * 0.4;
				posY -= facing.getYOffset() * 0.65;
				posZ -= facing.getZOffset() * 0.4;
			}

			boolean shootOut = isPipeEnabled();

			float pitch = 1f;
			if (!shootOut)
				pitch = 0.025f;

			if (playSound && Pipes.doPipesWhoosh) {
				if (isTheGoodDay(world))
					world.playSound(null, posX, posY, posZ, QuarkSounds.BLOCK_PIPE_SHOOT_LENNY, SoundCategory.BLOCKS, 1f, pitch);
				else
					world.playSound(null, posX, posY, posZ, QuarkSounds.BLOCK_PIPE_SHOOT, SoundCategory.BLOCKS, 1f, pitch);
			}

			EntityItem entity = new EntityItem(world, posX, posY, posZ, stack);
			entity.setDefaultPickupDelay();

			double velocityMod = 0.5;
			if (!shootOut)
				velocityMod = 0.125;

			if (facing != null) {
				entity.motionX = -facing.getXOffset() * velocityMod;
				entity.motionY = -facing.getYOffset() * velocityMod;
				entity.motionZ = -facing.getZOffset() * velocityMod;
			}
			world.spawnEntity(entity);
		}
	}

	public void dropAllItems() {
		for(PipeItem item : pipeItems)
			dropItem(item.stack);
		pipeItems.clear();
	}

	@Override
	public void readSharedNBT(NBTTagCompound cmp) {
		super.readSharedNBT(cmp);

		NBTTagList pipeItemList = cmp.getTagList(TAG_PIPE_ITEMS, cmp.getId());
		pipeItems.clear();
		pipeItemList.forEach(listCmp -> {
			PipeItem item = PipeItem.readFromNBT((NBTTagCompound) listCmp);
			pipeItems.add(item);
		});
	}

	@Override
	public void writeSharedNBT(NBTTagCompound cmp) {
		super.writeSharedNBT(cmp);

		NBTTagList pipeItemList = new NBTTagList();
		for(PipeItem item : pipeItems) {
			NBTTagCompound listCmp = new NBTTagCompound();
			item.writeToNBT(listCmp);
			pipeItemList.appendTag(listCmp);
		}
		cmp.setTag(TAG_PIPE_ITEMS, pipeItemList);
	}

	protected boolean canFit(ItemStack stack, BlockPos pos, EnumFacing face) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null)
			return false;

		if(tile instanceof TilePipe)
			return ((TilePipe) tile).isPipeEnabled();
		else {
			ItemStack result = putIntoInv(stack, tile, face, true);
			return result.isEmpty();
		}
	}

	protected boolean isPipeEnabled() {
		IBlockState state = world.getBlockState(pos);
		return state.getBlock() instanceof BlockPipe && state.getValue(BlockPipe.ENABLED);
	}

	protected ItemStack putIntoInv(ItemStack stack, TileEntity tile, EnumFacing face, boolean simulate) {
		IItemHandler handler = null;
		if(tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face))
			handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
		else if(tile instanceof ISidedInventory)
			handler = new SidedInvWrapper((ISidedInventory) tile, face);
		else if(tile instanceof IInventory)
			handler = new InvWrapper((IInventory) tile);

		if(handler != null)
			return ItemHandlerHelper.insertItem(handler, stack, simulate);
		return stack;
	}

	@Override
	public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
		return index == direction.ordinal() && isPipeEnabled();
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
		if(!itemstack.isEmpty()) {
			EnumFacing side = EnumFacing.VALUES[i];
			passIn(itemstack, side);
			sync();
		}
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	protected boolean needsToSyncInventory() {
		return false;
	}

	public static class PipeItem {

		private static final String TAG_TICKS = "ticksInPipe";
		private static final String TAG_INCOMING = "incomingFace";
		private static final String TAG_OUTGOING = "outgoingFace";
		private static final String TAG_RNG_SEED = "rngSeed";
		private static final String TAG_TIME_IN_WORLD = "timeInWorld";

		private static final List<EnumFacing> HORIZONTAL_SIDES_LIST = Arrays.asList(EnumFacing.HORIZONTALS);

		public final ItemStack stack;
		public int ticksInPipe;
		public final EnumFacing incomingFace;
		public EnumFacing outgoingFace;
		public long rngSeed;
		public int timeInWorld = 0;
		public boolean valid = true;

		public PipeItem(ItemStack stack, EnumFacing face, long rngSeed) {
			this.stack = stack;
			ticksInPipe = 0;
			incomingFace = outgoingFace = face;
			this.rngSeed = rngSeed;
		}

		protected boolean tick(TilePipe pipe) {
			ticksInPipe++;
			timeInWorld++;

			if(!pipe.world.isRemote && ticksInPipe == Pipes.pipeSpeed / 2 - 1) {
				EnumFacing target = getTargetFace(pipe);
				if (outgoingFace != target)
					pipe.needsSync = true;
				outgoingFace = target;
			}

			if(outgoingFace == null) {
				valid = false;
				return true;
			}

			return ticksInPipe >= Pipes.pipeSpeed;
		}

		protected EnumFacing getTargetFace(TilePipe pipe) {
			BlockPos pipePos = pipe.getPos();
			if(incomingFace != EnumFacing.DOWN && pipe.canFit(stack, pipePos.offset(EnumFacing.DOWN), EnumFacing.UP))
				return EnumFacing.DOWN;

			EnumFacing incomingOpposite = incomingFace; // init as same so it doesn't break in the remove later
			if(incomingFace.getAxis() != Axis.Y) {
				incomingOpposite = incomingFace.getOpposite();
				if(pipe.canFit(stack, pipePos.offset(incomingOpposite), incomingFace))
					return incomingOpposite;
			}

			List<EnumFacing> sides = new ArrayList<>(HORIZONTAL_SIDES_LIST);
			sides.remove(incomingFace);
			sides.remove(incomingOpposite);

			Random rng = new Random(rngSeed);
			rngSeed = rng.nextLong();
			Collections.shuffle(sides, rng);
			for(EnumFacing side : sides) {
				if(pipe.canFit(stack, pipePos.offset(side), side.getOpposite()))
					return side;
			}

			if(incomingFace != EnumFacing.UP && pipe.canFit(stack, pipePos.offset(EnumFacing.UP), EnumFacing.DOWN))
				return EnumFacing.UP;

			return null;
		}

		public float getTimeFract(float partial) {
			return (ticksInPipe + partial) / Pipes.pipeSpeed;
		}

		public void writeToNBT(NBTTagCompound cmp) {
			stack.writeToNBT(cmp);
			cmp.setInteger(TAG_TICKS, ticksInPipe);
			cmp.setInteger(TAG_INCOMING, incomingFace.ordinal());
			cmp.setInteger(TAG_OUTGOING, outgoingFace.ordinal());
			cmp.setLong(TAG_RNG_SEED, rngSeed);
			cmp.setInteger(TAG_TIME_IN_WORLD, timeInWorld);
		}

		public static PipeItem readFromNBT(NBTTagCompound cmp) {
			ItemStack stack = new ItemStack(cmp);
			EnumFacing inFace = EnumFacing.VALUES[cmp.getInteger(TAG_INCOMING)];
			long rngSeed = cmp.getLong(TAG_RNG_SEED);
			PipeItem item = new PipeItem(stack, inFace, rngSeed);
			item.ticksInPipe = cmp.getInteger(TAG_TICKS);
			item.outgoingFace = EnumFacing.VALUES[cmp.getInteger(TAG_OUTGOING)];
			item.timeInWorld = cmp.getInteger(TAG_TIME_IN_WORLD);

			return item;
		}

	}

}
