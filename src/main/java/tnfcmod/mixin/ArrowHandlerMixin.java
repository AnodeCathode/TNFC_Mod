package tnfcmod.mixin;

import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import org.spongepowered.asm.mixin.Mixin;
import cofh.core.proxy.EventHandler;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EventHandler.class)
public class ArrowHandlerMixin {

    @Redirect(method="handleArrowNockEvent", at = @At(value="RETURN", target = "Lcofh/core/proxy/EventHandler;handleArrowNockEvent(Lnet/minecraftforge/event/entity/player/ArrowNockEvent;)V"), remap = false)
    public void returnPass(ArrowNockEvent event, CallbackInfo ci)
    {
        if (event.getAction().getType().equals(EnumActionResult.FAIL))
        {
            event.setAction(new ActionResult<>(EnumActionResult.PASS,event.getBow()));
        }
    }
}
