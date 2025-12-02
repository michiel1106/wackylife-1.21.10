package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.*;
import net.minecraft.registry.tag.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "updateMovementInFluid", at = @At("HEAD"), cancellable = true)
    private void updateMovementMixin(TagKey<Fluid> tag, double speed, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase("no_water_collision")) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isTouchingWater", at = @At("HEAD"), cancellable = true)
    private void updateMixin(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase("no_water_collision")) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    private void updateMovementMixin(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase("no_water_collision")) {
                cir.setReturnValue(false);
            }
        }
    }
}
