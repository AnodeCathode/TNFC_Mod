package vazkii.quark.oddities.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.decoration.feature.TallowAndCandles;
import vazkii.quark.oddities.feature.MatrixEnchanting;
import vazkii.quark.oddities.inventory.ContainerMatrixEnchanting;
import vazkii.quark.oddities.inventory.EnchantmentMatrix;
import vazkii.quark.oddities.inventory.EnchantmentMatrix.Piece;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class TileMatrixEnchanter extends TileMatrixEnchanterBase {

	public static final int OPER_ADD = 0;
	public static final int OPER_PLACE = 1;
	public static final int OPER_REMOVE = 2;
	public static final int OPER_ROTATE = 3;
	public static final int OPER_MERGE = 4;

	public static final String TAG_STACK_MATRIX = "quark:enchantingMatrix";
	
	private static final String TAG_MATRIX = "matrix";
	private static final String TAG_MATRIX_UUID_LESS = "uuidLess";
	private static final String TAG_MATRIX_UUID_MOST = "uuidMost";
	private static final String TAG_CHARGE = "charge";

	public EnchantmentMatrix matrix;
	private boolean matrixDirty = false;
	private UUID matrixId;

	public final Map<Enchantment, Integer> influences = new HashMap<>();
	public int bookshelfPower, enchantability, charge;

	@Override
	public void update() {
		super.update();

		ItemStack item = getStackInSlot(0);
		if(item.isEmpty()) {
			matrix = null;
			matrixDirty = true;
		} else {
			loadMatrix(item);

			if(world.getTotalWorldTime() % 20 == 0 || matrixDirty)
				updateEnchantPower();
		}
		
		if(charge <= 0 && !world.isRemote) {
			ItemStack lapis = getStackInSlot(1);
			if(!lapis.isEmpty()) {
				lapis.shrink(1);
				charge += MatrixEnchanting.chargePerLapis;
				sync();
			}
		}

		if(matrixDirty) {
			makeOutput();
			matrixDirty = false;
		}
	}

	public void onOperation(EntityPlayer player, int operation, int arg0, int arg1, int arg2) {
		if(matrix == null)
			return;

		switch(operation) {
		case OPER_ADD:
			apply(m -> generateAndPay(m, player));
			break;
		case OPER_PLACE:
			apply(m -> m.place(arg0, arg1, arg2));
			break;
		case OPER_REMOVE:
			apply(m -> m.remove(arg0));
			break;
		case OPER_ROTATE:
			apply(m -> m.rotate(arg0));
			break;
		case OPER_MERGE:
			apply(m -> m.merge(arg0, arg1));
			break;
		}
	}

	private void apply(Predicate<EnchantmentMatrix> oper) {
		if(oper.test(matrix)) {
			ItemStack item = getStackInSlot(0);
			commitMatrix(item);
		}
	}

	private boolean generateAndPay(EnchantmentMatrix matrix, EntityPlayer player) {
		if(matrix.canGeneratePiece(bookshelfPower, enchantability) && matrix.validateXp(player, bookshelfPower)) {
			boolean creative = player.isCreative();
			int cost = matrix.getNewPiecePrice();
			if(charge > 0 || creative) {
				if (matrix.generatePiece(influences, bookshelfPower)) {
					if (!creative)
						player.addExperienceLevel(-cost);

					charge = Math.max(charge - 1, 0);
				}
			}
		}

		return true;
	}

	private void makeOutput() {
		if(world.isRemote)
			return;

		setInventorySlotContents(2, ItemStack.EMPTY);
		ItemStack in = getStackInSlot(0);
		if(!in.isEmpty() && matrix != null && !matrix.placedPieces.isEmpty()) {
			ItemStack out = in.copy();
			boolean book = false;
			if(out.getItem() == Items.BOOK) {
				out = new ItemStack(Items.ENCHANTED_BOOK);
				book = true;
			}

			Map<Enchantment, Integer> enchantments = new HashMap<>();

			for(int i : matrix.placedPieces) {
				Piece p = matrix.pieces.get(i);

				if (p != null && p.enchant != null) {
					for (Enchantment o : enchantments.keySet())
						if (o == p.enchant || !p.enchant.isCompatibleWith(o) || !o.isCompatibleWith(p.enchant))
							return; // Incompatible

					enchantments.put(p.enchant, p.level);
				}
			}

			if(book) 
				for(Entry<Enchantment, Integer> e : enchantments.entrySet())
					ItemEnchantedBook.addEnchantment(out, new EnchantmentData(e.getKey(), e.getValue()));
			else {
				EnchantmentHelper.setEnchantments(enchantments, out);
				ItemNBTHelper.getNBT(out).removeTag(TAG_STACK_MATRIX);
			}

			setInventorySlotContents(2, out);
		}
	}

	private void loadMatrix(ItemStack stack) {
		if(matrix == null || matrix.target != stack) {
			if(matrix != null)
				matrixDirty = true;
			matrix = null;
			
			if(stack.isItemEnchantable()) {
				matrix = new EnchantmentMatrix(stack, world.rand);
				matrixDirty = true;
				makeUUID();

				if(ItemNBTHelper.verifyExistence(stack, TAG_STACK_MATRIX)) {
					NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, TAG_STACK_MATRIX, true);
					if(cmp != null)
						matrix.readFromNBT(cmp);
				}
			}
		}
	}

	private void commitMatrix(ItemStack stack) {
		if(world.isRemote)
			return;

		NBTTagCompound cmp = new NBTTagCompound();
		matrix.writeToNBT(cmp);
		ItemNBTHelper.setCompound(stack, TAG_STACK_MATRIX, cmp);

		matrixDirty = true;
		makeUUID();
		sync();
	}

	private void makeUUID() {
		if(!world.isRemote)
			matrixId = UUID.randomUUID();
	}

	private void updateEnchantPower() {
		ItemStack item = getStackInSlot(0);
		influences.clear();
		if(item.isEmpty())
			return;

		enchantability = item.getItem().getItemEnchantability(item);

		float power = 0;
		for (int j = -1; j <= 1; ++j) {
			for (int k = -1; k <= 1; ++k) {
				if ((j != 0 || k != 0) && world.isAirBlock(pos.add(k, 0, j)) && world.isAirBlock(pos.add(k, 1, j))) {
					power += getEnchantPowerAt(world, pos.add(k * 2, 0, j * 2));
					power += getEnchantPowerAt(world, pos.add(k * 2, 1, j * 2));
					if (k != 0 && j != 0) {
						power += getEnchantPowerAt(world, pos.add(k * 2, 0, j));
						power += getEnchantPowerAt(world, pos.add(k * 2, 1, j));
						power += getEnchantPowerAt(world, pos.add(k, 0, j * 2));
						power += getEnchantPowerAt(world, pos.add(k, 1, j * 2));
					}
				}
			}
		}

		bookshelfPower = Math.min((int) power, MatrixEnchanting.maxBookshelves);
	}
	
	private float getEnchantPowerAt(World world, BlockPos pos) {
		if(MatrixEnchanting.allowInfluencing) {
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if(block == TallowAndCandles.candle) {
				EnumDyeColor ord = state.getValue(TallowAndCandles.candle.variantProp).color;
				
				List<Enchantment> influencedEnchants = MatrixEnchanting.candleInfluences.get(ord);
				for(Enchantment e : influencedEnchants) {
					int curr = influences.getOrDefault(e, 0);
					if(curr < MatrixEnchanting.influenceMax)
						influences.put(e, curr + 1);
				}
			}
		}
		
		return ForgeHooks.getEnchantPower(world, pos);
	}

	@Override
	public void writeSharedNBT(NBTTagCompound cmp) {
		super.writeSharedNBT(cmp);

		NBTTagCompound matrixCmp = new NBTTagCompound();
		if(matrix != null) {
			matrix.writeToNBT(matrixCmp);

			cmp.setTag(TAG_MATRIX, matrixCmp);
			if(matrixId != null) {
				cmp.setLong(TAG_MATRIX_UUID_LESS, matrixId.getLeastSignificantBits());
				cmp.setLong(TAG_MATRIX_UUID_MOST, matrixId.getMostSignificantBits());
			}
		}
		cmp.setInteger(TAG_CHARGE, charge);
	}

	@Override
	public void readSharedNBT(NBTTagCompound cmp) {
		super.readSharedNBT(cmp);

		if(cmp.hasKey(TAG_MATRIX)) {
			long least = cmp.getLong(TAG_MATRIX_UUID_LESS);
			long most = cmp.getLong(TAG_MATRIX_UUID_MOST);
			UUID newId = new UUID(most, least);

			if(!newId.equals(matrixId)) {
				NBTTagCompound matrixCmp = cmp.getCompoundTag(TAG_MATRIX);
				matrixId = newId;
				matrix = new EnchantmentMatrix(getStackInSlot(0), new Random());
				matrix.readFromNBT(matrixCmp);
			}
		} else matrix = null;
		
		charge = cmp.getInteger(TAG_CHARGE);
	}

	@Nonnull
	@Override
	public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
		return new ContainerMatrixEnchanting(playerInventory, this);
	}

}
