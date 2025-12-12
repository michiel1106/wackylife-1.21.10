package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.event.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity implements Shearable {


    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At(value = "HEAD"), cancellable = true)
    private void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("infinite_shearing")) {
            SheepEntity sheepEntity = (SheepEntity)(Object)this;
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.SHEARS)) {
                if (sheepEntity.getEntityWorld() instanceof ServerWorld serverWorld && sheepEntity.isShearable()) {
                    customsheared(serverWorld, SoundCategory.PLAYERS, itemStack);
                    sheepEntity.emitGameEvent(GameEvent.SHEAR, player);
                    itemStack.damage(1, player, hand.getEquipmentSlot());
                    cir.setReturnValue(ActionResult.SUCCESS_SERVER);
                } else {
                    cir.setReturnValue(ActionResult.CONSUME);
                }
            } else {
                cir.setReturnValue(super.interactMob(player, hand));
            }


            cir.cancel();

        }
    }


    public void customsheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
        SheepEntity sheepEntity = (SheepEntity)(Object)this;


        world.playSoundFromEntity(null, sheepEntity, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);


        forEachShearedItem(
                world,
                LootTables.SHEEP_SHEARING,
                shears,
                (worldx, stack) -> {
                    for (int i = 0; i < stack.getCount(); i++) {
                        ItemEntity itemEntity = dropStack(worldx, stack.copyWithCount(1), 1.0F);
                        if (itemEntity != null) {
                            itemEntity.setVelocity(
                                    itemEntity.getVelocity()
                                            .add(
                                                    (random.nextFloat() - random.nextFloat()) * 0.1F,
                                                    random.nextFloat() * 0.05F,
                                                    (random.nextFloat() - random.nextFloat()) * 0.1F
                                            )
                            );
                        }
                    }
                }
        );

    }

}
