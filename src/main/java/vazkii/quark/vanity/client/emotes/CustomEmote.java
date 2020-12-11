package vazkii.quark.vanity.client.emotes;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.vanity.feature.EmoteSystem;

@SideOnly(Side.CLIENT)
public class CustomEmote extends TemplateSourcedEmote {

	public CustomEmote(EmoteDescriptor desc, EntityPlayer player, ModelBiped model, ModelBiped armorModel, ModelBiped armorLegsModel) {
		super(desc, player, model, armorModel, armorLegsModel);
	}
	
	@Override
	boolean shouldLoadTimelineOnLaunch() {
		return EmoteSystem.customEmoteDebug;
	}

}
