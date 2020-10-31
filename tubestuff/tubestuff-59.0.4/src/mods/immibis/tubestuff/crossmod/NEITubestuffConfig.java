package mods.immibis.tubestuff.crossmod;

import mods.immibis.tubestuff.ContainerAutoCraftingMk2;
import mods.immibis.tubestuff.GuiAutoCraftingMk2;
import mods.immibis.tubestuff.PacketACT2RecipeUpdate;
import mods.immibis.tubestuff.TubeStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import cpw.mods.fml.common.API;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NEITubestuffConfig implements IConfigureNEI {
	
	public static class ACT2OverlayHandler implements IOverlayHandler {

		@Override
		public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
			ContainerAutoCraftingMk2 c = (ContainerAutoCraftingMk2)firstGui.inventorySlots;
			PacketACT2RecipeUpdate p = new PacketACT2RecipeUpdate();
			for(PositionedStack ps : recipe.getIngredientStacks(recipeIndex)) {
				int x = (ps.relx - 25) / 18;
				int y = (ps.rely - 6) / 18;
				int slot = x + y * 3;
				if(x < 0 || x > 2 || y < 0 || y > 2 || slot < 0 || slot > 8) {
					Minecraft.getMinecraft().thePlayer.sendChatMessage("TubeStuff NEI integration needs updating, this button is broken.");
					return;
				}
				
				p.stacks[slot] = ps.items;
			}
			c.sendActionPacket(p);
		}
	}

	@Override
	public void loadConfig() {
		API.registerGuiOverlayHandler(GuiAutoCraftingMk2.class, new ACT2OverlayHandler(), "crafting");
		API.registerGuiOverlayHandler(GuiAutoCraftingMk2.class, new ACT2OverlayHandler(), "crafting2x2");
	}

	@Override
	public String getName() {
		return TubeStuff.class.getAnnotation(Mod.class).name();
	}

	@Override
	public String getVersion() {
		return TubeStuff.class.getAnnotation(Mod.class).version();
	}
	
}