package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getMaxUseTime", at = @At("RETURN"), cancellable = true)
    private void maxusetime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        Integer returnValue = cir.getReturnValue() / 3;

        if (user instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("faster_eating")) {
                cir.setReturnValue(returnValue);
            }
        }
    }
}
