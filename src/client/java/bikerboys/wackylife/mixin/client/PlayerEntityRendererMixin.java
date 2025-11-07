package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import bikerboys.wackylife.networking.*;
import net.fabricmc.api.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @ModifyArg(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V", ordinal = 0),
            index = 3
    )
    private Text onRenderLabelIfPresent(Text text) {
        if (text == null || WackyLifeClient.playerNameMap == null) return text;

        String originalName = text.getString();

        String newName = WackyLifeClient.playerNameMap.getOrDefault(originalName, originalName);


        Style style = text.getStyle();

        if (!WackyLifeClient.wackySkinsActive) {
            return text;
        }
        return Text.literal(newName).setStyle(style);
    }


    @ModifyArg(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V", ordinal = 1),
            index = 3
    )
    private Text yayaya(Text text) {
        if (text == null || WackyLifeClient.playerNameMap == null) return text;

        String originalName = text.getString();

        String newName = WackyLifeClient.playerNameMap.getOrDefault(originalName, originalName);


        Style style = text.getStyle();

        if (!WackyLifeClient.wackySkinsActive) {
            return text;
        }
        return Text.literal(newName).setStyle(style);

    }
}
