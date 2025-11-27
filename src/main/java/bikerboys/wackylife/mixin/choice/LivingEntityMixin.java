package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract void dropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer);

    @Shadow protected int playerHitTimer;

    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropLoot(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;Z)V"))
    private void dropExtraLoot(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        Entity attacker = damageSource.getAttacker();
        boolean bl = playerHitTimer > 0;
        if (attacker instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("double_mob_drops")) {
                dropLoot(world, damageSource, bl);
            }
        }
    }
}
