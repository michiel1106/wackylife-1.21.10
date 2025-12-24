package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.item.*;
import net.minecraft.server.network.*;
import net.minecraft.util.hit.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin {

    @Shadow
    public abstract @Nullable Entity getOwner();

    @Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;onLanding()V"))
    private void onCollisionMixin(HitResult hitResult, CallbackInfo ci) {
        Entity owner = getOwner();
        if (getOwner() instanceof ServerPlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("infinite_pearls")) {
                player.giveOrDropStack(new ItemStack(Items.ENDER_PEARL));
            }
        }
    }
}
