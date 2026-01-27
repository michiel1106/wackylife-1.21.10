package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import com.mojang.authlib.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerListRedirectMixin extends PlayerEntity {



    public PlayerListRedirectMixin(World world, GameProfile profile) {
        super(world, profile);
    }


    /*
    @Inject(method = "getPlayerListEntry", at = @At("HEAD"), cancellable = true)
    private void redirectPlayerListEntry(CallbackInfoReturnable<PlayerListEntry> cir) {
        if (WackyLifeClient.wackySkinsActive) {
            var client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() == null) return;

            var map = WackyLifeClient.playerNameMap;
            var realName = this.getGameProfile().name();
            Pair<String, Integer> stringIntegerPair = map.get(realName);
            if (stringIntegerPair == null) return;
            var mappedName = stringIntegerPair.getLeft();


            for (var entry : client.getNetworkHandler().getPlayerList()) {
                if (entry.getProfile().name().equalsIgnoreCase(mappedName)) {
                    cir.setReturnValue(entry);
                    return;
                }
            }
        }
    }

     */
}
