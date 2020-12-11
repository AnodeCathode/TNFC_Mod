package vazkii.quark.oddities.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import vazkii.arl.block.tile.TileSimpleInventory;

import javax.annotation.Nonnull;
import java.util.Random;

// mostly a copy of TileEntityEnchantmentTable
public abstract class TileMatrixEnchanterBase extends TileSimpleInventory implements ITickable, IInteractionObject {

	public int tickCount;
	public float pageFlip, pageFlipPrev, flipT, flipA, bookSpread, bookSpreadPrev, bookRotation, bookRotationPrev, tRot;

	private static final Random rand = new Random();
	private String customName;

	@Override
	public int getSizeInventory() {
		return 3;
	}

	@Override
	public boolean isAutomationEnabled() {
		return false;
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		if(hasCustomName())
			compound.setString("CustomName", customName);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);

		if(compound.hasKey("CustomName", 8))
			customName = compound.getString("CustomName");
	}

	@Override
	public void update() {
		performVanillaUpdate();
	}

	private void performVanillaUpdate() {
		this.bookSpreadPrev = this.bookSpread;
		this.bookRotationPrev = this.bookRotation;
		EntityPlayer entityplayer = this.world.getClosestPlayer((this.pos.getX() + 0.5F), (this.pos.getY() + 0.5F), (this.pos.getZ() + 0.5F), 3.0D, false);

		if (entityplayer != null)
		{
			double d0 = entityplayer.posX - (this.pos.getX() + 0.5F);
			double d1 = entityplayer.posZ - (this.pos.getZ() + 0.5F);
			this.tRot = (float)MathHelper.atan2(d1, d0);
			this.bookSpread += 0.1F;

			if (this.bookSpread < 0.5F || rand.nextInt(40) == 0)
			{
				float f1 = this.flipT;

				do {
					this.flipT += (rand.nextInt(4) - rand.nextInt(4));
				} while (!(f1 != this.flipT));
			}
		}
		else
		{
			this.tRot += 0.02F;
			this.bookSpread -= 0.1F;
		}

		while (this.bookRotation >= (float)Math.PI)
		{
			this.bookRotation -= ((float)Math.PI * 2F);
		}

		while (this.bookRotation < -(float)Math.PI)
		{
			this.bookRotation += ((float)Math.PI * 2F);
		}

		while (this.tRot >= (float)Math.PI)
		{
			this.tRot -= ((float)Math.PI * 2F);
		}

		while (this.tRot < -(float)Math.PI)
		{
			this.tRot += ((float)Math.PI * 2F);
		}

		float f2 = this.tRot - this.bookRotation;

		while (f2 >= Math.PI)
			f2 -= (Math.PI * 2F);

		while (f2 < -Math.PI)
			f2 += (Math.PI * 2F);

		this.bookRotation += f2 * 0.4F;
		this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
		++this.tickCount;
		this.pageFlipPrev = this.pageFlip;
		float f = (this.flipT - this.pageFlip) * 0.4F;
		f = MathHelper.clamp(f, -0.2F, 0.2F);
		this.flipA += (f - this.flipA) * 0.9F;
		this.pageFlip += this.flipA;
	}

	public void dropItem(int i) {
		ItemStack stack = getStackInSlot(i);
		if(!stack.isEmpty())
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	}

	@Nonnull
	@Override
	public String getName() {
		return hasCustomName() ? customName : "container.enchant";
	}

	@Override
	public boolean hasCustomName() {
		return customName != null && !customName.isEmpty();
	}

	public void setCustomName(String customNameIn) {
		customName = customNameIn;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return hasCustomName() ? new TextComponentString(getName()) : new TextComponentTranslation(getName());
	}

	@Override
	public void inventoryChanged(int i) {
		super.inventoryChanged(i);
		sync();
	}
	
	@Nonnull
	@Override
	public String getGuiID() {
		return "minecraft:enchanting_table";
	}

}
