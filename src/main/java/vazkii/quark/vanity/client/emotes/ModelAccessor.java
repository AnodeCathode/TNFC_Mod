/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:37:50 (GMT)]
 */
package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.aurelienribon.tweenengine.TweenAccessor;

import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class ModelAccessor implements TweenAccessor<ModelBiped> {

	public static final ModelAccessor INSTANCE = new ModelAccessor();

	private static final int ROT_X = 0;
	private static final int ROT_Y = 1;
	private static final int ROT_Z = 2;
	private static final int OFF_X = 3;
	private static final int OFF_Y = 4;
	private static final int OFF_Z = 5;
	
	protected static final int MODEL_PROPS = 6;
	protected static final int BODY_PARTS = 7;
	protected static final int STATE_COUNT = MODEL_PROPS * BODY_PARTS;
	
	public static final int HEAD = 0;
	public static final int BODY = MODEL_PROPS;
	public static final int RIGHT_ARM = 2 * MODEL_PROPS;
	public static final int LEFT_ARM = 3 * MODEL_PROPS;
	public static final int RIGHT_LEG = 4 * MODEL_PROPS;
	public static final int LEFT_LEG = 5 * MODEL_PROPS;
	public static final int MODEL = 6 * MODEL_PROPS;

	public static final int HEAD_X = HEAD + ROT_X;
	public static final int HEAD_Y = HEAD + ROT_Y;
	public static final int HEAD_Z = HEAD + ROT_Z;
	public static final int BODY_X = BODY + ROT_X;
	public static final int BODY_Y = BODY + ROT_Y;
	public static final int BODY_Z = BODY + ROT_Z;
	public static final int RIGHT_ARM_X = RIGHT_ARM + ROT_X;
	public static final int RIGHT_ARM_Y = RIGHT_ARM + ROT_Y;
	public static final int RIGHT_ARM_Z = RIGHT_ARM + ROT_Z;
	public static final int LEFT_ARM_X = LEFT_ARM + ROT_X;
	public static final int LEFT_ARM_Y = LEFT_ARM + ROT_Y;
	public static final int LEFT_ARM_Z = LEFT_ARM + ROT_Z;
	public static final int RIGHT_LEG_X = RIGHT_LEG + ROT_X;
	public static final int RIGHT_LEG_Y = RIGHT_LEG + ROT_Y;
	public static final int RIGHT_LEG_Z = RIGHT_LEG + ROT_Z;
	public static final int LEFT_LEG_X = LEFT_LEG + ROT_X;
	public static final int LEFT_LEG_Y = LEFT_LEG + ROT_Y;
	public static final int LEFT_LEG_Z = LEFT_LEG + ROT_Z;

	public static final int MODEL_X = MODEL + ROT_X;
	public static final int MODEL_Y = MODEL + ROT_Y;
	public static final int MODEL_Z = MODEL + ROT_Z;
	
	public static final int HEAD_OFF_X = HEAD + OFF_X;
	public static final int HEAD_OFF_Y = HEAD + OFF_Y;
	public static final int HEAD_OFF_Z = HEAD + OFF_Z;
	public static final int BODY_OFF_X = BODY + OFF_X;
	public static final int BODY_OFF_Y = BODY + OFF_Y;
	public static final int BODY_OFF_Z = BODY + OFF_Z;
	public static final int RIGHT_ARM_OFF_X = RIGHT_ARM + OFF_X;
	public static final int RIGHT_ARM_OFF_Y = RIGHT_ARM + OFF_Y;
	public static final int RIGHT_ARM_OFF_Z = RIGHT_ARM + OFF_Z;
	public static final int LEFT_ARM_OFF_X = LEFT_ARM + OFF_X;
	public static final int LEFT_ARM_OFF_Y = LEFT_ARM + OFF_Y;
	public static final int LEFT_ARM_OFF_Z = LEFT_ARM + OFF_Z;
	public static final int RIGHT_LEG_OFF_X = RIGHT_LEG + OFF_X;
	public static final int RIGHT_LEG_OFF_Y = RIGHT_LEG + OFF_Y;
	public static final int RIGHT_LEG_OFF_Z = RIGHT_LEG + OFF_Z;
	public static final int LEFT_LEG_OFF_X = LEFT_LEG + OFF_X;
	public static final int LEFT_LEG_OFF_Y = LEFT_LEG + OFF_Y;
	public static final int LEFT_LEG_OFF_Z = LEFT_LEG + OFF_Z;

	public static final int MODEL_OFF_X = MODEL + OFF_X;
	public static final int MODEL_OFF_Y = MODEL + OFF_Y;
	public static final int MODEL_OFF_Z = MODEL + OFF_Z;

	private final Map<ModelBiped, float[]> MODEL_VALUES = new WeakHashMap<>();

	public static ModelRenderer getEarsModel(ModelPlayer model) {
		return model.boxList.get(model.boxList.indexOf(model.bipedLeftArm) - 2);
	}

	public void resetModel(ModelBiped model) {
		MODEL_VALUES.remove(model);
	}

	@Override
	public int getValues(ModelBiped target, int tweenType, float[] returnValues) {
		int axis = tweenType % MODEL_PROPS;
		int bodyPart = tweenType - axis;

		if (bodyPart == MODEL) {
			if (!MODEL_VALUES.containsKey(target)) {
				returnValues[0] = 0;
				return 1;
			}

			float[] values = MODEL_VALUES.get(target);
			returnValues[0] = values[axis];
			return 1;
		}

		ModelRenderer model = getBodyPart(target, bodyPart);
		if(model == null)
			return 0;

		switch(axis) {
			case ROT_X:
				returnValues[0] = model.rotateAngleX; break;
			case ROT_Y:
				returnValues[0] = model.rotateAngleY; break;
			case ROT_Z:
				returnValues[0] = model.rotateAngleZ; break;
			case OFF_X:
				returnValues[0] = model.offsetX; break;
			case OFF_Y:
				returnValues[0] = model.offsetY; break;
			case OFF_Z:
				returnValues[0] = model.offsetZ; break;
		}

		return 1;
	}

	private ModelRenderer getBodyPart(ModelBiped model, int part) {
		switch(part) {
			case HEAD : return model.bipedHead;
			case BODY : return model.bipedBody;
			case RIGHT_ARM : return model.bipedRightArm;
			case LEFT_ARM : return model.bipedLeftArm;
			case RIGHT_LEG : return model.bipedRightLeg;
			case LEFT_LEG : return model.bipedLeftLeg;
		}
		return null;
	}

	@Override
	public void setValues(ModelBiped target, int tweenType, float[] newValues) {
		int axis = tweenType % MODEL_PROPS;
		int bodyPart = tweenType - axis;

		if (bodyPart == MODEL) {
			float[] values = MODEL_VALUES.get(target);
			if (values == null)
				MODEL_VALUES.put(target, values = new float[MODEL_PROPS]);

			values[axis] = newValues[0];

			return;
		}

		ModelRenderer model = getBodyPart(target, bodyPart);
		messWithModel(target, model, axis, newValues[0]);
	}

	private void messWithModel(ModelBiped biped, ModelRenderer part, int axis, float val) {
		setPartAxis(part, axis, val);
		
		if(biped instanceof ModelPlayer)
			messWithPlayerModel((ModelPlayer) biped, part, axis, val);
	}

	private void messWithPlayerModel(ModelPlayer biped, ModelRenderer part, int axis, float val) {
		if(part == biped.bipedHead) {
			setPartAxis(biped.bipedHeadwear, axis, val);
			setPartOffset(getEarsModel(biped), axis, val);
		} else if(part == biped.bipedLeftArm)
			setPartAxis(biped.bipedLeftArmwear, axis, val);
		else if(part == biped.bipedRightArm)
			setPartAxis(biped.bipedRightArmwear, axis, val);
		else if(part == biped.bipedLeftLeg)
			setPartAxis(biped.bipedLeftLegwear, axis, val);
		else if(part == biped.bipedRightLeg)
			setPartAxis(biped.bipedRightLegwear, axis, val);
		else if(part == biped.bipedBody)
			setPartAxis(biped.bipedBodyWear, axis, val);
	}

	private void setPartOffset(ModelRenderer part, int axis, float val) {
		if(part == null)
			return;

		switch(axis) {
			case OFF_X:
				part.offsetX = val; break;
			case OFF_Y:
				part.offsetY = val; break;
			case OFF_Z:
				part.offsetZ = val; break;
		}
	}

	private void setPartAxis(ModelRenderer part, int axis, float val) {
		if(part == null)
			return;
		
		switch(axis) {
			case ROT_X:
				part.rotateAngleX = val; break;
			case ROT_Y:
				part.rotateAngleY = val; break;
			case ROT_Z:
				part.rotateAngleZ = val; break;
			case OFF_X:
				part.offsetX = val; break;
			case OFF_Y:
				part.offsetY = val; break;
			case OFF_Z:
				part.offsetZ = val; break;
		}
	}

}

