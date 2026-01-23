package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import bikerboys.wackylife.networking.*;
import net.fabricmc.api.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.awt.*;
import java.text.*;

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

        Pair<String, Integer> newName = WackyLifeClient.playerNameMap.get(originalName);

        if (newName == null) {
            return text;
        }

        Style style = text.getStyle();

        if (!WackyLifeClient.wackySkinsActive) {
            return text;
        }

        Formatting color = Formatting.GRAY;

        Integer right = newName.getRight();

        if (right >= 4) {
            color = Formatting.DARK_GREEN;
        } else if (right == 3) {
            color = Formatting.GREEN;
        } else if (right == 2) {
            color = Formatting.YELLOW;
        } else if (right == 1) {
            color = Formatting.RED;
        }

        return Text.literal(newName.getLeft()).setStyle(style.withColor(color));
    }


    @ModifyArg(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitLabel(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Vec3d;ILnet/minecraft/text/Text;ZIDLnet/minecraft/client/render/state/CameraRenderState;)V", ordinal = 1),
            index = 3
    )
    private Text yayaya(Text text) {
        if (text == null || WackyLifeClient.playerNameMap == null) return text;

        String originalName = text.getString();

        Pair<String, Integer> newName = WackyLifeClient.playerNameMap.get(originalName);

        if (newName == null) {
            return text;
        }

        Style style = text.getStyle();

        if (!WackyLifeClient.wackySkinsActive) {
            return text;
        }
        Formatting color = Formatting.GRAY;

        Integer right = newName.getRight();

        if (right >= 4) {
            color = Formatting.DARK_GREEN;
        } else if (right == 3) {
            color = Formatting.GREEN;
        } else if (right == 2) {
            color = Formatting.YELLOW;
        } else if (right == 1) {
            color = Formatting.RED;
        }


        return Text.literal(newName.getLeft()).setStyle(style.withColor(color));

    }
}
