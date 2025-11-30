package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

   @Inject(method = "getCookTime", at = @At("RETURN"), cancellable = true)
   private static void getCookTimeChange(ServerWorld world, AbstractFurnaceBlockEntity furnace, CallbackInfoReturnable<Integer> cir) {
       BlockPos pos = furnace.getPos();
       Box box = new Box(pos).expand(5);

       List<PlayerEntity> playerEntities = world.getEntitiesByClass(PlayerEntity.class, box, (player -> ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("faster_furnaces")));

       if (!playerEntities.isEmpty()) {
           int originalCookTime = cir.getReturnValue();
           int increasedFuelTime = originalCookTime / 3;
           cir.setReturnValue(increasedFuelTime);
       }
   }


}


