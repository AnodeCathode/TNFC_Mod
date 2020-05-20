package tnfcmod.objects.items;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

import static net.dries007.tfc.objects.CreativeTabsTFC.CT_MISC;

public class TNFCItems
{
    public static ItemBackpackPiece backpackpiece = new ItemBackpackPiece("backpackpiece").setCreativeTab(CT_MISC);
    public static ItemBackpackFrame backpackframe = new ItemBackpackFrame("backpackframe").setCreativeTab(CT_MISC);

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
            backpackpiece,
            backpackframe
        );

    }

    public static void registerModels()
    {
        backpackpiece.registerItemModel();
        backpackframe.registerItemModel();
    }
}
