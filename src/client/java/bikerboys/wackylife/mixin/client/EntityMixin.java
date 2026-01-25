package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = Entity.class, priority = 2)
public class EntityMixin {



    @Inject(method = "getAir", at = @At("RETURN"), cancellable = true)
    public void getAir(CallbackInfoReturnable<Integer> cir) {
        if (WackyLife.isLogicalSide()) return;
        if (System.currentTimeMillis() - WackyLifeClient.snailAirTimestamp > 5000) return;
        if (WackyLifeClient.snailAir >= 300) return;

        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity player && !player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
            int initialAir = cir.getReturnValue();
            if (WackyLifeClient.snailAir < initialAir) {
                cir.setReturnValue(WackyLifeClient.snailAir);
            }
        }
    }

}
