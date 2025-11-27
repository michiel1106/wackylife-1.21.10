package bikerboys.wackylife.mixin.client.choices;

import bikerboys.wackylife.attachements.*;
import com.mojang.datafixers.kinds.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.option.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(GameOptions.class)
public class GameOptionsMixin {


    @Shadow private int serverViewDistance;

    @Shadow @Final private SimpleOption<Integer> viewDistance;

    @Inject(method = "getClampedViewDistance", at = @At("HEAD"), cancellable = true)
    private void changethat(CallbackInfoReturnable<Integer> cir) {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            if (ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase("thick_fog")) {
                cir.setReturnValue(3);
            }
        }

    }


}
