package tnfcmod.mixin;

import net.dries007.tfc.objects.items.ItemQuiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import org.spongepowered.asm.mixin.Mixin;
import cofh.core.proxy.EventHandler;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EventHandler.class)
public class ArrowHandlerMixin {

    @Redirect(method = "Lcofh/core/proxy/EventHandler;handleArrowNockEvent(Lnet/minecraftforge/event/entity/player/ArrowNockEvent;)V",
            at = @At(value="INVOKE", target = "Lnet/minecraftforge/event/entity/player/ArrowNockEvent;setAction(Lnet/minecraft/util/ActionResult;)V",
                    ordinal = 1),
            remap = false)
    public void checkTFCQuiver(ArrowNockEvent event, ActionResult<ItemStack> action, ArrowNockEvent dummy)
    {
        final EntityPlayer player = event.getEntityPlayer();
        if (player != null && !player.capabilities.isCreativeMode && ItemQuiver.replenishArrow(player))
        {
            event.setAction(new ActionResult<>(EnumActionResult.SUCCESS, event.getBow()));
        }
        else
        {
            event.setAction(new ActionResult<>(EnumActionResult.PASS, event.getBow()));
        }
    }
}