package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatEntityMixin {

    @Shadow public abstract Vec3d updatePassengerForDismount(LivingEntity passenger);

    @WrapOperation(method = "getNearbySlipperiness", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getSlipperiness()F"))
    private float changeSlipperiness(Block instance, Operation<Float> original) {
        AbstractBoatEntity boatEntity = (AbstractBoatEntity)(Object)this;
        LivingEntity controllingPassenger = boatEntity.getControllingPassenger();

        if (controllingPassenger instanceof PlayerEntity player) {
            if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("ice_physics")) {
                return 0.989F;
            }
        }
        return original.call(instance);
    }

}
