package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import de.maxhenkel.voicechat.voice.client.*;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(RenderEvents.class)
public class RenderEventsMixin {

    @ModifyVariable(
            method = "onRenderName",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/state/EntityRenderState;displayName:Lnet/minecraft/text/Text;"),
            argsOnly = true
    )
    private EntityRenderState modifyVoiceChatIcon(EntityRenderState value) {
        boolean wackySkinsActive = WackyLifeClient.wackySkinsActive;
        if (wackySkinsActive && value.displayName != null) {
            Pair<String, Integer> pair = WackyLifeClient.playerNameMap.get(value.displayName.getString());
            if (pair != null) {
                String replacement = pair.getLeft();
                if (replacement != null) {
                    value.displayName = Text.of(replacement);
                }
            }

        }


        return value;
    }

}
