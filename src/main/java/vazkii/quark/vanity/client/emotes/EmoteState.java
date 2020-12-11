/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:30 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static vazkii.quark.vanity.client.emotes.EmoteBase.PI_F;

@SideOnly(Side.CLIENT)
public class EmoteState {

	private float[] states = new float[0];
	private final EmoteBase emote;

	public EmoteState(EmoteBase emote) {
		this.emote = emote;
	}

	public void save(ModelBiped model) {
		float[] values = new float[1];
		for(int i = 0; i < ModelAccessor.STATE_COUNT; i++) {
			ModelAccessor.INSTANCE.getValues(model, i, values);
			states[i] = values[0];
		}
	}

	public void load(ModelBiped model) {
		if(states.length == 0) {
			states = new float[ModelAccessor.STATE_COUNT];
		} else {
			float[] values = new float[1];
			for(int i = 0; i < ModelAccessor.STATE_COUNT; i++) {
				values[0] = states[i];

				int part = (i / ModelAccessor.MODEL_PROPS) * ModelAccessor.MODEL_PROPS;
				if(emote.usesBodyPart(part))
					ModelAccessor.INSTANCE.setValues(model, i, values);
			}
		}
	}

	public void rotateAndOffset(EntityPlayer player) {
		if(states.length == 0)
			return;

		float offsetX = states[ModelAccessor.MODEL_OFF_X];
		float offsetY = states[ModelAccessor.MODEL_OFF_Y];
		float offsetZ = states[ModelAccessor.MODEL_OFF_Z];
		float rotX = states[ModelAccessor.MODEL_X];
		float rotY = states[ModelAccessor.MODEL_Y];
		float rotZ = states[ModelAccessor.MODEL_Z];

		float height = player.height;

		GlStateManager.translate(0, height / 2, 0);

		GlStateManager.translate(offsetX, offsetY, offsetZ);

		if (rotY != 0)
			GlStateManager.rotate(rotY * 180 / PI_F, 0, 1, 0);
		if (rotX != 0)
			GlStateManager.rotate(rotX * 180 / PI_F, 1, 0, 0);
		if (rotZ != 0)
			GlStateManager.rotate(rotZ * 180 / PI_F, 0, 0, 1);

		GlStateManager.translate(0, -height / 2, 0);
	}
}

