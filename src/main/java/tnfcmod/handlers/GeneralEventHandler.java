package tnfcmod.handlers;


import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.tmtravlr.jaff.entities.EntityIronFishHook;
import com.tmtravlr.jaff.items.ItemHookedFishingRod;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.food.NutritionStats;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import tnfcmod.util.MonsterGear;

import static tnfcmod.tnfcmod.MODID;
import static tnfcmod.util.MonsterGear.SHADERBAGS;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID)
public class GeneralEventHandler
{
    private static final Random RANDOM = new Random();
    public static final FoodData DEATHRATTLE = new FoodData(-2, -10.0F, 0.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, 0.0F);


    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        if (!event.isWasDeath()){return;};
        if (event.getEntityPlayer() instanceof EntityPlayerMP && !event.getEntityPlayer().isCreative() )
        {
            EntityPlayer player = event.getEntityPlayer();
            EntityPlayer oldPlayer = event.getOriginal();
            //Going to take their old nutrition and apply it to their new body, plus randomly nuke one category.
            //The death tax is waaaaay too low. But only if their nutrition is already crap. Otherwise, let the reset happen.
            // Food Stats
            IFoodStatsTFC oldFoodStats = (IFoodStatsTFC) oldPlayer.getFoodStats();
            FoodStatsTFC.replaceFoodStats(player);
            IFoodStatsTFC newFoodStats = (IFoodStatsTFC) player.getFoodStats();
            NutritionStats oldNutritionStats = oldFoodStats.getNutrition();
            NutritionStats newNutritionStats = newFoodStats.getNutrition();
            if (oldNutritionStats.getAverageNutrition() < 0.5)
            {
                newNutritionStats.deserializeNBT(oldNutritionStats.serializeNBT());

            }
            //Dying messes up your biochemistry
            newNutritionStats.addNutrients(DEATHRATTLE);
            //And makes you thirsty
            newFoodStats.addThirst(-20);



        }

    }
    @SubscribeEvent
    public static void onCropsGrow(BlockEvent.CropGrowEvent event)
    {
        ChunkDataTFC data = ChunkDataTFC.get(event.getWorld(), event.getPos());
        if (data.isSpawnProtected())
        {
            if (event.getState().getBlock() instanceof BlockShortGrassTFC || event.getState().getBlock() instanceof BlockTallGrassTFC)
            {
                event.setResult(Event.Result.DENY);
                if (event.getWorld().canSeeSky(event.getPos()))
                {
                    event.getWorld().setBlockToAir(event.getPos());
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityIronFishHook && entity.getClass().equals(EntityIronFishHook.class))
        {
            World world = event.getWorld();
            if (!world.isRemote)
            {
                EntityPlayer player = ((EntityFishHook) entity).getAngler();
                ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (stack.getItem() instanceof ItemHookedFishingRod)
                {
                    if (stack.hasTagCompound()) {
                        NBTTagList baitList = stack.getTagCompound().getTagList("Bait", 10);
                        if (baitList.isEmpty()){
                            entity.setDead();
                            player.sendStatusMessage(new TextComponentTranslation("tnfcmod.nobait"), ConfigTFC.Client.TOOLTIP.propickOutputToActionBar);
                        }
                    }
                    else
                    {
                        entity.setDead();
                        player.sendStatusMessage(new TextComponentTranslation("tnfcmod.nobait"), ConfigTFC.Client.TOOLTIP.propickOutputToActionBar);
                    }
                }
            }
       }
            if (event.getWorld().provider.getDimensionType() != DimensionType.OVERWORLD)
            {

                if (entity instanceof EntityChicken)
                {
                    event.setCanceled(true); // NO!
                }

                if (event.getEntity().isCreatureType(EnumCreatureType.MONSTER, false))
                {
                    // Set equipment to some mobs
                    MonsterGear equipment = MonsterGear.get(entity);
                    if (equipment != null)
                    {
                        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
                        {
                            equipment.getEquipment(slot, Constants.RNG).ifPresent(stack -> entity.setItemStackToSlot(slot, stack));
                        }
                    }
                }
            }
        }



    @SubscribeEvent
    public static void onEntityLivingDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityLivingBase)
        {
            if (entity instanceof EntityPlayer)
            {
                // You play with the bear, you sometimes get your head ripped clean off!
                if (RANDOM.nextDouble() * 100 <= 10 && event.getSource().getTrueSource() instanceof IPredator)
                {
                    ItemStack playerHead = new ItemStack(Items.SKULL, 1, 3);
                    playerHead.setTagCompound(new NBTTagCompound());
                    EntityPlayer player = (EntityPlayer) entity;
                    String playerName = player.getDisplayNameString();
                    playerHead.getTagCompound().setString("SkullOwner", playerName);
                    entity.entityDropItem(playerHead, 0.5F);
                    if (RANDOM.nextDouble() * 100 <= 5)
                    {
                        // Soulforged steel nugget
                        NonNullList<ItemStack> nugget = OreDictionary.getOres("nuggetSoulforgedSteel");
                        entity.entityDropItem(nugget.get(0), 0.5F);
                    }
                }
            }

            // All mobs at Y<100 chance drop of IE shader grab-bags and soul forged nuggets
            if (RANDOM.nextDouble() * 100 <= 10 && entity.posY < 100 && entity.isCreatureType(EnumCreatureType.MONSTER, false) && event.getSource().getTrueSource() instanceof EntityPlayer)
            {
                ItemStack shaderbag = new ItemStack(IEContent.itemShaderBag);
                ItemNBTHelper.setString(shaderbag, "rarity", SHADERBAGS.getRandomEntry(Constants.RNG).toString());
                entity.entityDropItem(shaderbag, 0.5F);
                if (RANDOM.nextDouble() * 100 <= 35)
                {
                    // Soulforged steel nugget
                    NonNullList<ItemStack> nugget = OreDictionary.getOres("nuggetSoulforgedSteel");
                    entity.entityDropItem(nugget.get(0), 0.5F);
                }
            }
            //Spider drops redstone at low Y levels
            if (entity.posY < 100 && entity instanceof EntitySpider & event.getSource().getTrueSource() instanceof EntityPlayer && RANDOM.nextDouble() * 100 <= 25)
            {
                entity.entityDropItem(new ItemStack(Items.REDSTONE, 1), 0.5F);
                //Add a random chance for 2?
                if (RANDOM.nextDouble() * 100 <= 25)
                {
                    entity.entityDropItem(new ItemStack(Items.REDSTONE, 1), 0.5F);
                }
            }
            //Creeper drops glowstone at low Y levels
            if (entity.posY < 100 && entity instanceof EntityCreeper & event.getSource().getTrueSource() instanceof EntityPlayer && RANDOM.nextDouble() * 100 <= 25)
            {
                entity.entityDropItem(new ItemStack(Items.GLOWSTONE_DUST, 1), 0.5F);
            }

            //Soulforged steel nugget - only on other planets
            if (entity.world.provider.getDimension() > 0 && RANDOM.nextDouble() * 100 <= 50 && entity.posY < 100 && entity.isCreatureType(EnumCreatureType.MONSTER, false) && event.getSource().getTrueSource() instanceof EntityPlayer)
            {
                // Soulforged steel nugget
                NonNullList<ItemStack> nugget = OreDictionary.getOres("nuggetSoulforgedSteel");
                entity.entityDropItem(nugget.get(0), 0.5F);
            }
        }


    }



}
