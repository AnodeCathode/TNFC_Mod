/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:06 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.aurelienribon.tweenengine.Timeline;
import vazkii.aurelienribon.tweenengine.TweenManager;

@SideOnly(Side.CLIENT)
public abstract class EmoteBase {

	public static final float PI_F = (float) Math.PI;

	public final EmoteDescriptor desc;

	private final TweenManager emoteManager;
	private final ModelBiped model;
	private final ModelBiped armorModel;
	private final ModelBiped armorLegsModel;
	private final EmoteState state;
	private final EntityPlayer player;
	
	public float timeDone, totalTime, animatedTime;
	private long lastMs;

	public EmoteBase(EmoteDescriptor desc, EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		this.desc = desc;
		emoteManager = new TweenManager();
		state = new EmoteState(this);
		this.model = model;
		this.armorModel = armorModel;
		this.armorLegsModel = armorLegsModel;
		this.player = player;
	}
	
	public void startAllTimelines() {
		startTimeline(player, model);
		startTimeline(player, armorModel);
		startTimeline(player, armorLegsModel);
		lastMs = System.currentTimeMillis();
	}

	void startTimeline(EntityPlayer player, ModelBiped model) {
		Timeline timeline = getTimeline(player, model).start(emoteManager);
		totalTime = timeline.getFullDuration();
	}

	public abstract Timeline getTimeline(EntityPlayer player, ModelBiped model);

	public abstract boolean usesBodyPart(int part);

	public void rotateAndOffset() {
		state.rotateAndOffset(player);
	}

	public void update() {
		state.load(model);
		state.load(armorModel);
		state.load(armorLegsModel);
		
		long currTime = System.currentTimeMillis();
		long timeDiff = currTime - lastMs;
		animatedTime += timeDiff;
		emoteManager.update(timeDiff);
		state.save(model);
		
		lastMs = currTime;
		timeDone += timeDiff;
	}
	
	public boolean isDone() {
		return timeDone >= totalTime || player.swingProgress > 0 || player.hurtTime > 0;
	}


}
