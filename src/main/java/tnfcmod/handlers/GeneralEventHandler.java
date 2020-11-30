package tnfcmod.handlers;


import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
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
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.food.NutritionStats;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.IPredator;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockMushroomTFC;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.objects.blocks.plants.BlockTallGrassTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.types.DefaultPlants;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.config.OreTooltipMode;
import net.dries007.tfc.util.skills.SkillTier;
import net.dries007.tfc.util.skills.SkillType;
import net.dries007.tfc.util.skills.SmithingSkill;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import tnfcmod.util.ConfigTNFCMod;
import tnfcmod.util.MonsterGear;

import static tnfcmod.tnfcmod.MODID;
import static tnfcmod.util.MonsterGear.SHADERBAGS;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MODID)
public class GeneralEventHandler
{
    private static final Random RANDOM = new Random();
    public static final FoodData DEATHRATTLE = new FoodData(-2, -10.0F, 0.0F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F, 0.0F);


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
            if (oldNutritionStats.getAverageNutrition() < 0.38 && oldNutritionStats.getAverageNutrition() > 0.1)
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
    public static void onPlayerUpdate(LivingUpdateEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityPlayer))
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.isCreative() || player.isSpectator())
        {
            return;
        }
//
//
//        if (ConfigTNFCMod.GENERAL.skillbasedTempDisplay)
//        {
//            SkillTier tier= CapabilityPlayerData.getSkill(player, SkillType.SMITHING).getTier();
//            if (tier.isAtLeast(SkillTier.EXPERT))
//            {
//
//                if (ConfigTFC.Client.TOOLTIP.oreTooltipMode != OreTooltipMode.ADVANCED)
//                {
//                    ConfigTFC.Client.TOOLTIP.oreTooltipMode = OreTooltipMode.ADVANCED;
//                }
//            }
//            else
//            {
//                if (ConfigTFC.Client.TOOLTIP.oreTooltipMode == OreTooltipMode.ADVANCED)
//                {
//                    ConfigTFC.Client.TOOLTIP.oreTooltipMode = OreTooltipMode.ALL_INFO;
//                }
//            }
//        }


        if (player.getFoodStats() instanceof IFoodStatsTFC)
        {
            float healthModifier = ((IFoodStatsTFC) player.getFoodStats()).getHealthModifier();
            if (healthModifier < ConfigTFC.General.PLAYER.minHealthModifier)
            {
                healthModifier = (float) ConfigTFC.General.PLAYER.minHealthModifier;
            }
            if (healthModifier > ConfigTFC.General.PLAYER.maxHealthModifier)
            {
                healthModifier = (float) ConfigTFC.General.PLAYER.maxHealthModifier;
            }
            //So we have the healthModifier that TFC calcs from nutrition. Now we need to toss that info to FirstAid
            //So we need to scale the FirstAid body parts for the health modifier
//            double maxhealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
//            double newhealth = maxhealth * healthModifier;
//
//            tnfcmod.tnfcmod.getLog().info("MaxHealth:" + maxhealth + " | NewHealth: " + newhealth);
//            player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newhealth);
            AbstractPlayerDamageModel damageModel = Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, null));
            for (AbstractDamageablePart damageablePart : damageModel)
            {
                float currentHealth = damageablePart.currentHealth;
                int intMaxHealth = damageablePart.getMaxHealth();
                float percentage = currentHealth / intMaxHealth;
                float newMax = damageablePart.initialMaxHealth * healthModifier;
                int newInt = (int) newMax;
                tnfcmod.tnfcmod.getLog().info("old max:" + newMax + " | new max: " + newInt);
                damageablePart.setMaxHealth(newInt);
                damageablePart.currentHealth = newInt * percentage;
            }
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


    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        final EntityPlayer player = event.getPlayer();
        final ItemStack heldItem = player == null ? ItemStack.EMPTY : player.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        if (block == Blocks.BROWN_MUSHROOM || block == Blocks.RED_MUSHROOM || block instanceof BlockMushroomTFC){
            // Some sort of mushroom. Get a brand new mushroom or otherwise they are old stupid mushrooms.
            BlockPos pos = event.getPos();
            EntityItem mushroom = new net.minecraft.entity.item.EntityItem(player.world, pos.getX(), pos.getY(), pos.getZ(),
                new ItemStack(BlockMushroomTFC.get(TFCRegistries.PLANTS.getValue(DefaultPlants.PORCINI))));
            mushroom.setDefaultPickupDelay();
            player.world.spawnEntity(mushroom);


        }
        // Sequoia saplings
        if (OreDictionaryHelper.doesStackMatchOre(heldItem, "craftingToolEliteShears"))
        {
            if (block instanceof BlockLeavesTFC)
            {
                BlockLeavesTFC blockLeavesTFC = (BlockLeavesTFC) block;
                if (blockLeavesTFC.wood == Tree.SEQUOIA)
                {
                    double chance = ConfigTNFCMod.GENERAL.saplingdropchance;
                    if ((double) Constants.RNG.nextFloat() < chance)
                    {
                        BlockPos pos = event.getPos();
                        EntityItem sapling = new net.minecraft.entity.item.EntityItem(player.world, pos.getX(), pos.getY(), pos.getZ(),
                            new ItemStack(Item.getItemFromBlock(BlockSaplingTFC.get(blockLeavesTFC.wood))));
                        sapling.setDefaultPickupDelay();
                        player.world.spawnEntity(sapling);
                        heldItem.damageItem(1, player);

                    }
                }
            }
        }

    }


    @SubscribeEvent
    public static void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        final EntityPlayer player = event.getHarvester();
        final ItemStack heldItem = player == null ? ItemStack.EMPTY : player.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        // Harvest ice from saws
        if (OreDictionaryHelper.doesStackMatchOre(heldItem, "craftingToolEliteSaw"))
        {
            if (block == Blocks.ICE)
            {
                event.getDrops().add(new ItemStack(Blocks.ICE));
            }

            if (block == BlocksTFC.SEA_ICE)
            {
                event.getDrops().add(new ItemStack(Blocks.PACKED_ICE));
            }
        }
        if (block == Blocks.BROWN_MUSHROOM || block == Blocks.RED_MUSHROOM || block instanceof BlockMushroomTFC){
            // Some sort of mushroom. Get a brand new mushroom or otherwise they are old stupid mushrooms.
            event.setDropChance(0);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityMob)
        {
            if (entity.isRiding())
            {

                Entity ridingEntity = entity.getRidingEntity();
                //so we don't kill spider jockey's cause they are hilarious.
                if (ridingEntity instanceof EntityChicken){
                    entity.setDropItemsWhenDead(false);
                    entity.setDead();
                    ridingEntity.setDropItemsWhenDead(false);
                    ridingEntity.setDead();
                    event.setCanceled(true);
                }

            }
        }
        //Just nuke them from orbit. It's the only way to be sure
        if (entity instanceof EntityChicken)
        {
            entity.setDropItemsWhenDead(false);
            entity.setDead();
            event.setCanceled(true); // NO!
        }

        if (entity instanceof EntityIronFishHook && entity.getClass().equals(EntityIronFishHook.class))
        {
            World world = event.getWorld();
            if (!world.isRemote)
            {
                EntityPlayer player = ((EntityFishHook) entity).getAngler();
                ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
                if (!(stack.getItem() instanceof ItemHookedFishingRod))
                {
                    stack = player.getHeldItem(EnumHand.OFF_HAND);
                }
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
