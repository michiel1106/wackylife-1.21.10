package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase("pacifist")) {
            ci.cancel();
        }
    }

}
