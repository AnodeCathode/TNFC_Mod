package vazkii.quark.base.client.gui.config;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiConfigModule extends GuiConfigBase {

	private static final int FEATURES_PER_PAGE = 12;
	
	private final Module module;
	private final List<Feature> features;
	private int page = 0;
	private final int totalPages;
	
	private GuiButton left, right;
	
	public GuiConfigModule(GuiScreen parent, Module module) {
		super(parent);
		this.module = module;
		
		features = new ArrayList<>();
		module.forEachFeature(features::add);
		Collections.sort(features);

		totalPages = (features.size() - 1) / FEATURES_PER_PAGE + 1;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		title += " - " + I18n.format("quark.config.module." + module.name) + " (" + features.size() + ")";

		int x = width / 2 - 100;
		int y = height / 6 + 167;
		
		buttonList.add(backButton = new GuiButton(0, x, y, 200, 20, I18n.format("gui.done")));

		if(totalPages > 1) {
			x = width / 2;
			y = height / 6 - 12;
			buttonList.add(left = new GuiButton(0, x - 40, y, 20, 20, "<"));
			buttonList.add(right = new GuiButton(0, x + 20, y, 20, 20, ">"));
		}
		
		addFeatureButtons();
	}

	public void addFeatureButtons() {
		int startX = width / 2 - 195;
		int startY = height / 6 + 20;
		
		buttonList.removeIf((b) -> b instanceof GuiButtonConfigSetting || b instanceof GuiButtonFeatureSettings);

		int start = page * FEATURES_PER_PAGE;
		for(int i = start; i < Math.min(start + FEATURES_PER_PAGE, features.size()); i++) {
			int j = i - start;
			int x = startX + j % 2 * 200;
			int y = startY + j / 2 * 22;
			
			Feature feature = features.get(i);
			
			buttonList.add(new GuiButtonConfigSetting(x + 150, y, feature.prop, true, feature.getFeatureIngameConfigName()));
			
			if(ModuleLoader.config.hasCategory(feature.configCategory))
				buttonList.add(new GuiButtonFeatureSettings(x + 170, y, feature.configCategory));
		}
		
		if(left != null) {
			left.enabled = (page > 0);
			right.enabled = (page < totalPages - 1);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if(button instanceof GuiButtonFeatureSettings) {
			GuiButtonFeatureSettings featureButton = (GuiButtonFeatureSettings) button;
			mc.displayGuiScreen(new GuiConfigCategory(this, featureButton.category));
		} else if(button == left || button == right) {
			if(button == left)
				page = Math.max(page - 1, 0);
			else page = Math.min(page + 1, totalPages - 1);

			addFeatureButtons();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if(totalPages > 1) {
			int x = width / 2;
			int y = height / 6 - 7;
			drawCenteredString(mc.fontRenderer, (page + 1) + "/" + totalPages, x, y, 0xFFFFFF);
		}
		
		if(mayRequireRestart) {
			String s = I18n.format("quark.config.needrestart");
			drawCenteredString(mc.fontRenderer, s, width / 2, backButton.y + 22, 0xFFFF00);
		}
	}
	
}
