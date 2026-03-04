package bikerboys.wackylife.mixin;

import bikerboys.wackylife.*;
import bikerboys.wackylife.event.*;
import bikerboys.wackylife.util.*;
import com.google.common.eventbus.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void healEvent(float amount, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (livingEntity instanceof ServerPlayerEntity player) {
            CustomPlayerEvent.HEAL.invoker().heal(player, amount);
        }
    }


    @Inject(method = "drop", at = @At("HEAD"))
    private void onDrop(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        WackyLife.wackyLife.onEntityDropItems((LivingEntity) (Object) this, damageSource, ci);
    }
}
