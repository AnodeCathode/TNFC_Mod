package mods.immibis.tubestuff;

import net.minecraft.block.Block;
import mods.immibis.core.ItemCombined;

public class ItemTubestuff extends ItemCombined {
	public ItemTubestuff(Block id) {
		super(id, "tubestuff", new String[] {
			"buffer",
			"act2",
			"bhc",
			"incinerator",
			"duplicator",
			"retrievulator",
			"breaker",
			"liquidincinerator",
			"liquidduplicator",
			"playerdetector",
			"mct2",
			"deployer"
		});
	}
}
