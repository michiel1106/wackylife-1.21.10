package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.hud.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowHeight()I"))
    private int changeheight(DrawContext context, Operation<Integer> original) {
        return (int) (original.call(context) + WackyLifeClient.crosshairy);
    }


    @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    private int changewidth(DrawContext context, Operation<Integer> original) {
        return (int) (original.call(context) + WackyLifeClient.crosshairx);
    }

}
