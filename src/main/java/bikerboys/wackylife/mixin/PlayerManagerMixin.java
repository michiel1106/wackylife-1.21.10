package bikerboys.wackylife.mixin;


import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @WrapOperation(
            method = "onPlayerConnect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;",
                    ordinal = 0
            )
    )
    private Text conenctMixin(ServerPlayerEntity player, Operation<Text> original) {
        original.call(player);

        Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();

        if (!(wildcardObj instanceof WackySkins skins)) {
            return player.getDisplayName();
        }

        String realName = player.getName().getString();
        String fakeName = skins.playerNameMap.get(realName);

        if (fakeName != null) {
            return Text.literal(fakeName);
        }

        return player.getDisplayName();
    }
}
