package tnfcmod.handlers;


import java.util.Map;
import java.util.Optional;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.food.NutritionStats;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
import net.dries007.tfc.objects.entity.animal.EntityBlackBearTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import tnfcmod.util.MonsterGear;

import static tnfcmod.tnfcmod.MODID;

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
                event.getWorld().setBlockToAir(event.getPos());
            }
        }
    }


    @SubscribeEvent
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        if (event.getWorld().provider.getDimensionType() != DimensionType.OVERWORLD)
        {
            Entity entity = event.getEntity();

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
                        Optional<ItemStack> gear = equipment.getEquipment(slot, Constants.RNG);
                        if (gear.isPresent())
                        {
                            ItemStack actual = gear.get();
                            if (RANDOM.nextInt(5) == 0)
                            {
                                EnchantmentHelper.addRandomEnchantment(Constants.RNG, actual, RANDOM.nextInt(2) + 1, false);
                            }

                            entity.setItemStackToSlot(slot, actual);
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public static void onEntityLivingDeath(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayer)
        {
            // You play with the bear, you sometimes get your head ripped clean off!
            if(event.getSource().getTrueSource() instanceof EntityBlackBearTFC && event.getEntityLiving().world.rand. nextDouble() * 100 <= 50)
            {
                ItemStack playerHead = new ItemStack(Items.SKULL, 1, 3);
                playerHead.setTagCompound(new NBTTagCompound());
                EntityPlayer player = (EntityPlayer)entity;
                String playerName = player.getDisplayNameString();
                playerHead.getTagCompound().setString("SkullOwner", playerName);
                event.getEntityLiving().entityDropItem(playerHead, 0.5F);

            }
        }
    }



}
