package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import com.mojang.authlib.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.text.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class PlayerListRedirectMixin extends PlayerEntity {

    private static final Map<String, PlayerListEntry> ENTRY_CACHE = new HashMap<>();

    public PlayerListRedirectMixin(World world, GameProfile profile) {
        super(world, profile);
    }


    @Inject(method = "getPlayerListEntry", at = @At("HEAD"), cancellable = true)
    private void redirectPlayerListEntry(CallbackInfoReturnable<PlayerListEntry> cir) {
        var client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return;

        var map = WackyLifeClient.playerNameMap;
        var realName = this.getGameProfile().name();
        var mappedName = map.get(realName);
        if (mappedName == null) return;

        var cached = ENTRY_CACHE.get(realName);
        if (cached != null) {
            cir.setReturnValue(cached);
            return;
        }

        for (var entry : client.getNetworkHandler().getPlayerList()) {
            if (entry.getProfile().name().equalsIgnoreCase(mappedName)) {
                ENTRY_CACHE.put(realName, entry);
                cir.setReturnValue(entry);
                return;
            }
        }
    }

    @Inject(method = "getMannequinName", at = @At("HEAD"), cancellable = true)
    private void getMannequinNameMixin(CallbackInfoReturnable<Text> cir) {

    }
}
