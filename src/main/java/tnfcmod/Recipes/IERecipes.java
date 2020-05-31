package tnfcmod.Recipes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientStack;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.api.tool.BelljarHandler;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.plant.BlockIECrop;
import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.ItemPowder;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.util.agriculture.Crop;
import tnfcmod.objects.items.TNFCItems;

import static blusunrize.immersiveengineering.api.tool.BelljarHandler.*;
import static net.dries007.tfc.api.types.Metal.ItemType.DUST;


public class IERecipes
{
    private static final DefaultPlantHandler tfcBelljarHandler = new DefaultPlantHandler()
    {
        private HashMap<ComparableItemStack, IngredientStack> seedSoilMap = new HashMap();
        private HashMap<ComparableItemStack, ItemStack[]> seedOutputMap = new HashMap();
        private HashMap<ComparableItemStack, IBlockState[]> seedRenderMap = new HashMap();
        private HashSet<ComparableItemStack> validSeeds = new HashSet<>();

        @Override
        @SideOnly(Side.CLIENT)
        public float getRenderSize(ItemStack seed, ItemStack soil, float growth, TileEntity tile)
        {
            return .6675f;
        }

        @Override
        protected HashSet<ComparableItemStack> getSeedSet() {return validSeeds;}

        @Override
        public boolean isValid(ItemStack seed)
        {
            return seed != null && this.getSeedSet().contains(new ComparableItemStack(seed, false, false));
        }

        @Override
        public boolean isCorrectSoil(final ItemStack seed, final ItemStack soil)
        {
            IngredientStack reqSoil = (IngredientStack) this.seedSoilMap.get(new ComparableItemStack(seed, false, false));
            return reqSoil.matchesItemStack(soil);
        }

        @Override
        public ItemStack[] getOutput(ItemStack seed, ItemStack soil, TileEntity tile)
        {
            return (ItemStack[]) this.seedOutputMap.get(new ComparableItemStack(seed, false, false));
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IBlockState[] getRenderedPlant(ItemStack seed, ItemStack soil, float growth, TileEntity tile)
        {
            // Will need to handle vanilla crops? IE Hemp in particular
            IBlockState[] states = (IBlockState[]) this.seedRenderMap.get(new ComparableItemStack(seed, false, false));
            //IBlockState[] states = null;
            if (states == null)
            {
                return null;
            }
            else
            {
                IBlockState[] ret = new IBlockState[states.length];

                label58:
                for (int i = 0; i < states.length; ++i)
                {
                    if (states[i] != null)
                    {
                        if (states[i].getBlock() instanceof BlockCrops)
                        {
                            int max = ((BlockCrops) states[i].getBlock()).getMaxAge();
                            ret[i] = ((BlockCrops) states[i].getBlock()).withAge(Math.min(max, Math.round((float) max * growth)));
                        }
                        else if (states[i].getBlock() instanceof BlockCropTFC)
                        {
                            BlockCropTFC bs = (BlockCropTFC) states[i].getBlock();
                            ICrop crop = bs.getCrop();
                            int curStage = states[i].getValue(bs.getStageProperty());
                            int maxStage = crop.getMaxStage();
                            int age = Math.min(maxStage, Math.round(growth * maxStage));
                            return new IBlockState[] {states[i].getBlock().getStateFromMeta(age)};


                        }
                        //IE Block
                        else if (states[i].getBlock() instanceof BlockIECrop)
                        {
                            int age = Math.min(4, Math.round(growth * 4));
                            if (age == 4)
                                return new IBlockState[] {states[i].getBlock().getStateFromMeta(age), states[i].getBlock().getStateFromMeta(age + 1)};
                            return new IBlockState[] {states[i].getBlock().getStateFromMeta(age)};
                        }
                        else
                        {
                            Iterator var8 = states[i].getPropertyKeys().iterator();

                            while (true)
                            {
                                IProperty prop;
                                do
                                {
                                    do
                                    {
                                        if (!var8.hasNext())
                                        {
                                            if (ret[i] == null)
                                            {
                                                ret[i] = states[i];
                                            }
                                            continue label58;
                                        }

                                        prop = (IProperty) var8.next();
                                    } while (!"age".equals(prop.getName()));
                                } while (!(prop instanceof PropertyInteger));

                                int maxx = 0;
                                Iterator var11 = ((PropertyInteger) prop).getAllowedValues().iterator();

                                while (var11.hasNext())
                                {
                                    Integer allowed = (Integer) var11.next();
                                    if (allowed != null && allowed > maxx)
                                    {
                                        maxx = allowed;
                                    }
                                }

                                ret[i] = states[i].withProperty(prop, Math.min(maxx, Math.round((float) maxx * growth)));
                            }
                        }
                    }
                }

                return ret;
            }
        }

        @Override
        public void register(ItemStack seed, ItemStack[] output, Object soil, IBlockState... render)
        {
            this.register(seed, output, ApiUtils.createIngredientStack(soil), render);
        }

        public void register(ItemStack seed, ItemStack[] output, IngredientStack soil, IBlockState... render)
        {
            ComparableItemStack comp = new ComparableItemStack(seed, false, false);
            this.getSeedSet().add(comp);
            this.seedSoilMap.put(comp, soil);
            this.seedOutputMap.put(comp, output);
            this.seedRenderMap.put(comp, render);

        }
    };

    public static void registerMetalPressRecipes()
    {
        //Mold recipes
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_blank, 1), "sheetDoubleSteel", new ItemStack(IEContent.blockStorage, 1, 8), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_doubleingot, 1), "ingotDoubleSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_sheet, 1), "sheetSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_bucket, 1), "bucketRedSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_bucket, 1), "bucketBlueSteel", new ItemStack(TNFCItems.mold_blank, 1), 2400);
        Ingredient ingredientVanillaBucket = Ingredient.fromStacks(new ItemStack(Items.BUCKET));
        MetalPressRecipe.addRecipe(new ItemStack(TNFCItems.mold_bucket, 1), ingredientVanillaBucket, new ItemStack(TNFCItems.mold_blank, 1), 2400);


        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            // are there non-tool metals that we want sheets/doubles from?
            if (metal.isToolMetal() | metal.toString() == "lead")
            {
                ItemStack outputDoubleIngot = new ItemStack(ItemMetal.get(metal, Metal.ItemType.DOUBLE_INGOT), 1);
                ItemStack outputSheet = new ItemStack(ItemMetal.get(metal, Metal.ItemType.SHEET), 1);
                ItemStack outputDoubleSheet = new ItemStack(ItemMetal.get(metal, Metal.ItemType.DOUBLE_SHEET), 1);

                Ingredient ingredientIngot = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.INGOT)));
                Ingredient ingredientDoubleIngot = Ingredient.fromStacks(outputDoubleIngot);
                Ingredient ingredientSheet = Ingredient.fromStacks(outputSheet);
                Ingredient ingredientDoubleSheet = Ingredient.fromStacks(outputDoubleSheet);
                Ingredient ingredientKnives = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.KNIFE_BLADE)));

                //Sheets and doubles
                MetalPressRecipe.addRecipe(outputDoubleIngot, ingredientIngot, new ItemStack(TNFCItems.mold_doubleingot), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(outputSheet, ingredientIngot, new ItemStack(TNFCItems.mold_sheet), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(outputSheet, ingredientDoubleIngot, new ItemStack(TNFCItems.mold_sheet), 2400);
                MetalPressRecipe.addRecipe(outputDoubleSheet, ingredientSheet, new ItemStack(TNFCItems.mold_sheet), 2400).setInputSize(2);

                //Tools

                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.AXE_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_axe), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.CHISEL_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_chisel), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_hammer), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.HAMMER_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_hoe), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.JAVELIN_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_javelin), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.KNIFE_BLADE), 1), ingredientIngot, new ItemStack(TNFCItems.mold_knife), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.LAMP), 1), ingredientIngot, new ItemStack(TNFCItems.mold_lamp), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.MACE_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_mace), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.PICK_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_pick), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.PROPICK_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_propick), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SAW_BLADE), 1), ingredientIngot, new ItemStack(TNFCItems.mold_saw), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCYTHE_BLADE), 1), ingredientIngot, new ItemStack(TNFCItems.mold_scythe), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SHEARS), 1), ingredientKnives, new ItemStack(TNFCItems.mold_shears), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SHOVEL_HEAD), 1), ingredientIngot, new ItemStack(TNFCItems.mold_shovel), 2400).setInputSize(2);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SWORD_BLADE), 1), ingredientDoubleIngot, new ItemStack(TNFCItems.mold_sword), 2400);
                MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.TUYERE), 1), ingredientDoubleSheet, new ItemStack(TNFCItems.mold_tuyere), 2400).setInputSize(2);
                if (metal == Metal.RED_STEEL | metal == Metal.BLUE_STEEL)
                {
                    MetalPressRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, Metal.ItemType.BUCKET), 1), ingredientSheet, new ItemStack(TNFCItems.mold_bucket), 2400);
                }

            }

        }
    }

    public static void registerCrusherRecipes(){


        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            //Basic ingot to dust
            if (DUST.hasType(metal)){
                Ingredient ingredientIngot = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.INGOT)));
                CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST), 1), ingredientIngot, 8000);
            }
            //Turn scrap into dust. Now to decide what turns into scrap.
            if (Metal.ItemType.SCRAP.hasType(metal) && DUST.hasType(metal)){
                Ingredient ingredientScrap = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.SCRAP)));
                CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST), 1), ingredientScrap, 8000);
                //Do we want to be able to crush tools/armour/etc to scrap?

            }
            //Throw your used anvils in the grinder for a satisfying crunch
            if (Metal.ItemType.ANVIL.hasType(metal) && DUST.hasType(metal))
            {
                Ingredient ingredientAnvil = Ingredient.fromStacks(new ItemStack(ItemMetal.get(metal, Metal.ItemType.ANVIL)));
                CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST), 14), ingredientAnvil, 112000);
            }
        }
        for (Ore ore : TFCRegistries.ORES.getValuesCollection())
        {
            if (ore.canMelt())
            {
                Metal metal = ore.getMetal();
                // need to check grading and return different amounts.
                if (Metal.ItemType.DUST.hasType(metal))
                {
                    Ingredient ingredientOre = Ingredient.fromStacks(new ItemStack(ItemOreTFC.get(ore)));
                    CrusherRecipe.addRecipe(new ItemStack(ItemMetal.get(metal, DUST), 2), ingredientOre, 4000);
                }
            }
        }
        // Clear out the recipes related
        for (QuernRecipe quernRecipe : TFCRegistries.QUERN.getValuesCollection())
        {
            CrusherRecipe.removeRecipesForOutput(quernRecipe.getOutputs().get(0));
        }
        // Churn the quern and steal its recipes
        for (QuernRecipe quernRecipe : TFCRegistries.QUERN.getValuesCollection())
        {

            NonNullList<IIngredient<ItemStack>> ingredientlist = quernRecipe.getIngredients();
            IIngredient iingredient = ingredientlist.get(0);
            NonNullList foo = iingredient.getValidIngredients();
            Ingredient ingredient = Ingredient.fromStacks((ItemStack) foo.get(0));
            ItemStack output = quernRecipe.getOutputs().get(0);


            if (ingredient != null)
            {
                CrusherRecipe.addRecipe(output, ingredient, 4000);
            }


        }


    }

    public static void registerGardenClocheRecipes()
    {

        java.util.List<ItemStack> soils = Lists.newArrayList();
        BelljarHandler.registerHandler(tfcBelljarHandler);
        BlocksTFC.getAllBlockRockVariants().forEach(x ->
        {
            if (x.getType() == Rock.Type.DIRT || x.getType() == Rock.Type.GRASS || x.getType() == Rock.Type.FARMLAND)
            {
                soils.add(new ItemStack(x, 1));
            }
        });


        for (Crop crop : Crop.values())
        {
            tfcBelljarHandler.register(ItemSeedsTFC.get(crop, 1), new ItemStack[] {new ItemStack(ItemSeedsTFC.get(crop), 1), crop.getFoodDrop(crop.getMaxStage())}, soils, BlockCropSimple.get(crop).getDefaultState());
        }
        //Hopefully this takes over from the built in handler for hemp and lets it work with Fresh_water and... stuff. Not caring enough to build it's own entire separate handler ffs
        tfcBelljarHandler.register(new ItemStack(IEContent.itemSeeds, 1), new ItemStack[] {new ItemStack(IEContent.itemMaterial, 4, 4), new ItemStack(IEContent.itemSeeds, 1)}, soils, IEContent.blockCrop.getDefaultState());
        registerFluidFertilizer(new FluidFertilizerHandler()
        {
            @Override
            public boolean isValid(@Nullable FluidStack fertilizer)
            {
                return fertilizer != null && (fertilizer.getFluid() == FluidsTFC.FRESH_WATER.get() || fertilizer.getFluid() == FluidsTFC.HOT_WATER.get());
            }

            @Override
            public float getGrowthMultiplier(FluidStack fertilizer, ItemStack itemStack, ItemStack itemStack1, TileEntity tileEntity)
            {
                if (fertilizer.getFluid() == FluidsTFC.FRESH_WATER.get())
                {
                    return 1f;
                }
                else if (fertilizer.getFluid() == FluidsTFC.HOT_WATER.get())
                {
                    return 2f;
                }
                return 0f;

            }
        });
        registerItemFertilizer(new ItemFertilizerHandler()
        {
            final ItemStack sylvite = ItemPowder.get(Powder.FERTILIZER, 1);

            @Override
            public boolean isValid(ItemStack fertilizer)
            {
                return !fertilizer.isEmpty() && OreDictionary.itemMatches(sylvite, fertilizer, true);
            }

            @Override
            public float getGrowthMultiplier(ItemStack itemStack, ItemStack itemStack1, ItemStack itemStack2, TileEntity tileEntity)
            {
                return 1.2f;
            }
        });

    }



}
