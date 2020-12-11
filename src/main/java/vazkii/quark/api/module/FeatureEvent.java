/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Apr 30, 2019, 10:46 AM (EST)]
 */
package vazkii.quark.api.module;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * A superclass for all feature-related events.
 */
public class FeatureEvent extends Event {

	private final IFeature feature;

	public FeatureEvent(IFeature feature) {
		this.feature = feature;
	}

	public IFeature getFeature() {
		return feature;
	}

	/**
 	* Canceling this event will result in the feature being disabled completely.
 	*/
	@Cancelable
	public static class Loaded extends FeatureEvent {
		public Loaded(IFeature feature) {
			super(feature);
		}
	}

	/**
 	* This event is fired before a feature is enabled.
 	*/
	public static class Enabled extends FeatureEvent {
		public Enabled(IFeature feature) {
			super(feature);
		}
	}

	/**
 	* This event is fired after a feature is enabled.
 	*/
	public static class PostEnable extends FeatureEvent {
		public PostEnable(IFeature feature) {
			super(feature);
		}
	}

	/**
 	* This event is fired before a feature is disabled.
 	*/
	public static class Disabled extends FeatureEvent {
		public Disabled(IFeature feature) {
			super(feature);
		}
	}

	/**
 	* This event is fired after a feature is enabled.
 	*/
	public static class PostDisable extends FeatureEvent {
		public PostDisable(IFeature feature) {
			super(feature);
		}
	}
}
