package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import com.mojang.authlib.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.texture.*;
import net.minecraft.component.type.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements ClientPlayerLikeEntity{

    public AbstractClientPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
    private void changeSkinTexture(CallbackInfoReturnable<SkinTextures> cir) {
        String name = getGameProfile().name();

        Pair<String, Integer> stringIntegerPair = WackyLifeClient.playerNameMap.get(name);

        if (WackyLifeClient.wackySkinsActive && stringIntegerPair != null) {
            PlayerSkinCache cache = MinecraftClient.getInstance().getPlayerSkinCache();
            ProfileComponent profile = ProfileComponent.ofDynamic(stringIntegerPair.getLeft());

            cache.getFuture(profile).thenAccept(optional -> {
                optional.ifPresent(entry -> {
                    cir.setReturnValue(entry.getTextures());
                });
            });
        }
    }
}
