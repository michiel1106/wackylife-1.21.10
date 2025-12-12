package bikerboys.wackylife.mixin.client.choices;

import bikerboys.wackylife.attachements.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.hud.*;
import net.minecraft.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(InGameHud.class)
public class HudMixin {

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    private void stopthat(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
         if (MinecraftClient.getInstance().player != null) {
             if(ModAttachments.getChoice(MinecraftClient.getInstance().player).negativeChoiceId().equalsIgnoreCase("hiddenhealth")) {
                 ci.cancel();
             }
         }
    }
}
