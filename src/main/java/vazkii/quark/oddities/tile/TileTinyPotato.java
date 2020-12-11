/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Jul 18, 2014, 8:05:08 PM (GMT)]
 */
package vazkii.quark.oddities.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.base.network.message.MessageSpamlessChat;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.oddities.feature.TinyPotato;

public class TileTinyPotato extends TileSimpleInventory implements ITickable {

	private static final String TAG_NAME = "name";
	private static final String TAG_ANGERY = "angery";

	private static final int TINY_POTATO_CHAT = "tiny potate".hashCode();

	public int jumpTicks = 0;
	public String name = "";
	public int nextDoIt = 0;

	public boolean angery = false;

	public void interact(EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side) {
		int index = side.getIndex();
		if(index >= 0) {
			ItemStack stackAt = getStackInSlot(index);
			if(!stackAt.isEmpty() && stack.isEmpty()) {
				player.setHeldItem(hand, stackAt);
				setInventorySlotContents(index, ItemStack.EMPTY);
			} else if(!stack.isEmpty()) {
				ItemStack copy = stack.splitStack(1);

				if(stack.isEmpty())
					player.setHeldItem(hand, stackAt);
				else if(!stackAt.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(player, stackAt);
				}

				setInventorySlotContents(index, copy);
			}
		}

		jump();

		if(!world.isRemote) {
			if(name.toLowerCase().trim().endsWith("shia labeouf") && nextDoIt == 0) {
				nextDoIt = 40;
				world.playSound(null, pos, QuarkSounds.BLOCK_TINY_POTATO_SHIA_LABEOUF, SoundCategory.BLOCKS, 1F, 1F);
			}

			for(int i = 0; i < getSizeInventory(); i++) {
				ItemStack stackAt = getStackInSlot(i);
				if(!stackAt.isEmpty() && stackAt.getItem() == Item.getItemFromBlock(TinyPotato.tiny_potato)) {
					MessageSpamlessChat.sendToPlayer(player, TINY_POTATO_CHAT, new TextComponentTranslation("quarkmisc.me_or_my_son"));
					return;
				}
			}
		}
	}

	public void jump() {
		if(jumpTicks == 0)
			jumpTicks = 20;
	}

	@Override
	public void update() {
		if(world.rand.nextInt(100) == 0)
			jump();

		if(jumpTicks > 0)
			jumpTicks--;
		if(nextDoIt > 0)
			nextDoIt--;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		sync();
	}

	@Override
	public void writeSharedNBT(NBTTagCompound compound) {
		super.writeSharedNBT(compound);
		compound.setString(TAG_NAME, name);
		compound.setBoolean(TAG_ANGERY, angery);
	}

	@Override
	public void readSharedNBT(NBTTagCompound compound) {
		super.readSharedNBT(compound);
		name = compound.getString(TAG_NAME);
		angery = compound.getBoolean(TAG_ANGERY);
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

}
