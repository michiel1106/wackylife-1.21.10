package bikerboys.wackylife.mixin.choice;

import bikerboys.wackylife.attachements.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemUsage;exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack onUseWrap(ItemStack inputStack, PlayerEntity player, ItemStack outputStack, Operation<ItemStack> original) {
        if (ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase("infinite_water_bucket")) {
            if (inputStack.isOf(Items.WATER_BUCKET)) {
                if (!player.isSneaking()) {
                    return inputStack;
                }
            }
        }


        return original.call(inputStack, player, outputStack);
    }

}
