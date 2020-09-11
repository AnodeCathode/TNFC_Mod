package com.lumintorious.ambiental;

import com.lumintorious.ambiental.capability.TemperatureSystem;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = TFCAmbiental.MODID, category = "")
@Mod.EventBusSubscriber(modid = TFCAmbiental.MODID)
@Config.LangKey("config." + TFCAmbiental.MODID)
public class TFCAmbientalConfig {
		
		    @Config.Comment("Client settings")
		    @Config.LangKey("config." + TFCAmbiental.MODID + ".client")
		    public static final ClientCFG CLIENT = new ClientCFG();
		    
		    @Config.Comment("General settings")
		    @Config.LangKey("config." + TFCAmbiental.MODID + ".general")
		    public static final GeneralCFG GENERAL = new GeneralCFG();

		    @SubscribeEvent
		    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
		    {
		        if (event.getModID().equals(TFCAmbiental.MODID))
		        {
		            ConfigManager.sync(TFCAmbiental.MODID, Config.Type.INSTANCE);
		            TemperatureSystem.AVERAGE = TFCAmbientalConfig.GENERAL.averageTemperature;
		            TemperatureSystem.HOT_THRESHOLD = TFCAmbientalConfig.GENERAL.hotTemperature;
		            TemperatureSystem.COOL_THRESHOLD = TFCAmbientalConfig.GENERAL.coldTemperature;
		            TemperatureSystem.BURN_THRESHOLD = TFCAmbientalConfig.GENERAL.burningTemperature;
		            TemperatureSystem.FREEZE_THRESHOLD = TFCAmbientalConfig.GENERAL.freezingTemperature;
		        }
		    }

		    public static class ClientCFG{
		    	@Config.Comment("If true, temperature is displayed in Celsius instead of Farhenheit .")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".client.celsius")
	         	public boolean celsius = true;
		    	
	    	 	@Config.Comment("If true, you will get extra details about your temperature when sneaking, when false they are always visible.")
	    	 	@Config.LangKey("config." + TFCAmbiental.MODID + ".client.sneakyDetails")
	    	 	public boolean sneakyDetails = true;
		    }
		    
		    public static class GeneralCFG {
		    	@Config.Comment("How quickly temperature rises and decreases")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.temperatureMultiplier")
	         	public float temperatureMultiplier = 1.0f;
		    	
		    	@Config.Comment("How fast does temperature change when it's going towards the average.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.positiveModifier")
	         	public float positiveModifier = 5f;
		    	
		    	@Config.Comment("How fast does temperature change when it's going away from the average. If you think you are giving yourself a challenge by increasing this number, think twice. It makes it so that you have to warm yourself up every so often.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.negativeModifier")
	         	public float negativeModifier = 1f;
		    	
		    	@Config.Comment("How many ticks between modifier calculations. Too high values help performance but behave weirdly. 20 = 1 second means modifiers are checked every second. Also affects the packet sending interval.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.tickInterval")
	         	public int tickInterval = 20;
		    	
		    	@Config.Comment("How potent are multipliers with more than one instance. (Eg. 2 fire pits nearby means they have 2 * this effectiveness)")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.diminishedModifierMultiplier")
	         	public float diminishedModifierMultiplier = 0.7f;
		    	
		    	@Config.Comment("If true, you will start taking damage when below freezing or above burning temperatures.")
	    	 	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.takeDamage")
	    	 	public boolean takeDamage = true;
		    	
		    	@Config.Comment("If true, you will start losing hunger when below cold temperatures and losing thirst when above hot temperatures.")
	    	 	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.takeDamage")
	    	 	public boolean loseHungerThirst = true;
		    	
		    	@Config.Comment("How many modifiers of the same type until they stop adding together")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.modifierCap")
	         	public int modifierCap = 4;
		    	
		    	@Config.Comment("If true, temperate areas won't be as mild")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.harsherTemperateAreas")
	         	public boolean harsherTemperateAreas = true;
		    	
		    	@Config.Comment("If harsherTemperateAreas is true, environmental temperatures going away from the average are multiplied by this number. (The less temperate an area is, the less the modifier affects it) ")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.harsherMultiplier")
	         	public float harsherMultiplier = 1.35f;
		    	
		    	@Config.Comment("The temperature at which you are at equilibrium. It's advisable to not change this by a lot since the entire ecosystem revolves around this.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.averageTemperature")
	         	public float averageTemperature = 15f;
		    	
		    	@Config.Comment("The temperature at which your screen starts heating. It's advisable to not change this by a lot since the entire ecosystem revolves around this.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.hotTemperature")
	         	public float hotTemperature = 20f;
		    	
		    	@Config.Comment("The temperature at which your screen starts freezing. It's advisable to not change this by a lot since the entire ecosystem revolves around this.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.coldTemperature")
	         	public float coldTemperature = 10f;
		    	
		    	@Config.Comment("The temperature at which you start burning and taking damage. It's advisable to not change this by a lot since the entire ecosystem revolves around this.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.burningTemperature")
	         	public float burningTemperature = 25f;
		    	
		    	@Config.Comment("The temperature at which you start freezing and taking damage. It's advisable to not change this by a lot since the entire ecosystem revolves around this.")
	         	@Config.LangKey("config." + TFCAmbiental.MODID + ".general.freezingTemperature")
	         	public float freezingTemperature = 5f;
		    }

}
