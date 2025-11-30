package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.math.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract void dropLoot(ServerWorld world, DamageSource damageSource, boolean causedByPlayer);

    @Shadow protected int playerHitTimer;

    @Shadow public abstract boolean isClimbing();

    @Shadow protected boolean jumping;

    @Shadow protected abstract float getMovementSpeed(float slipperiness);

    @Shadow protected abstract Vec3d applyClimbingSpeed(Vec3d motion);

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

    @Inject(method = "applyMovementInput", at = @At("RETURN"), cancellable = true)
    private void applyMovementInputMixin(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("faster_ladder_climbing")) {
                if ((livingEntity.horizontalCollision || jumping) && (this.isClimbing() || livingEntity.wasInPowderSnow && PowderSnowBlock.canWalkOnPowderSnow(livingEntity))) {
                    Vec3d vec3d = livingEntity.getVelocity();
                    vec3d = new Vec3d(vec3d.x, 0.5, vec3d.z);
                    cir.setReturnValue(vec3d);
                }
            }
        }
    }



    @WrapOperation(method = "applyClimbingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D"))
    private double yeahidk(double value, double min, double max, Operation<Double> original) {
        double f = 0.15f;
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("faster_ladder_climbing")) {
                f = 0.30f;
            }
        }

        return original.call(value, -f, f);
    }

    @WrapOperation(method = "applyClimbingSpeed", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"))
    private double otherthing(double a, double b, Operation<Double> original) {
        double f = 0.15f;
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (livingEntity instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("faster_ladder_climbing")) {
                f = 0.30f;
            }
        }

        return original.call(a, -f);
    }

    /**
     * @author
     * @reason
     */
  //  @Overwrite
  //  private Vec3d applyClimbingSpeed(Vec3d motion) {
//
  //      LivingEntity livingEntity = (LivingEntity) (Object) this;
//
  //      if (livingEntity.isClimbing()) {
  //          livingEntity.onLanding();
//
  //          float f = 0.15f;
  //          if (livingEntity instanceof PlayerEntity player) {
  //              if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("faster_ladder_climbing")) {
  //                  f = 0.60F;
  //              }
  //          }
  //          double d = MathHelper.clamp(motion.x, -f, f);
  //          double e = MathHelper.clamp(motion.z, -f, f);
  //          double g = Math.max(motion.y, -f);
  //          if (g < 0.0 && !livingEntity.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && livingEntity.isHoldingOntoLadder() && livingEntity instanceof PlayerEntity) {
  //              g = 0.0;
  //          }
//
  //          motion = new Vec3d(d, g, e);
  //      }
//
  //      return motion;
  //  }
}
