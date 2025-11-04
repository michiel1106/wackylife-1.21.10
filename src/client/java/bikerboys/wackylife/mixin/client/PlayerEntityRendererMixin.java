package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import net.fabricmc.api.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.command.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.client.render.state.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(PlayerEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class PlayerEntityRendererMixin {

    @Inject(
            method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderLabelIfPresent(
            PlayerEntityRenderState renderState,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            CameraRenderState camera,
            CallbackInfo ci
    ) {
        matrices.push();

        int yOffset = renderState.extraEars ? -10 : 0;

        // Always check displayName first, fallback to playerName mapped
        Text nameText = null;

        if (renderState.playerName != null) {
            String realName = renderState.playerName.getString();
            String mapped = WackyLifeClient.playerNameMap.get(realName);
            if (mapped != null) {
                nameText = Text.of(mapped);
            } else {
                nameText = renderState.playerName;
            }
        }

        if (nameText != null) {
            queue.submitLabel(
                    matrices,
                    renderState.nameLabelPos,
                    yOffset,
                    nameText,
                    !renderState.sneaking,
                    renderState.light,
                    renderState.squaredDistanceToCamera,
                    camera
            );
            matrices.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
        }

        // Render displayName as usual
        if (renderState.displayName != null) {
            queue.submitLabel(
                    matrices,
                    renderState.nameLabelPos,
                    yOffset,
                    renderState.displayName,
                    !renderState.sneaking,
                    renderState.light,
                    renderState.squaredDistanceToCamera,
                    camera
            );
        }

        matrices.pop();

        ci.cancel(); // prevent vanilla rendering
    }
}
