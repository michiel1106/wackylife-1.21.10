package bikerboys.wackylife.mixin;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.*;

@Debug(export = true)
@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @WrapOperation(
            method = "sendChatMessage",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/network/packet/s2c/play/ChatMessageS2CPacket;"
            )
    )
    private ChatMessageS2CPacket replaceChatPacket(int globalIndex, java.util.UUID sender, int index, MessageSignatureData signature, MessageBody.Serialized body, Text unsignedContent, FilterMask filterMask, MessageType.Parameters params, Operation<ChatMessageS2CPacket> original) {

        Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();

        if (wildcardObj instanceof WackySkins skins) {
            Text originalName = params.name();
            Text replacedName = replaceNamesInText(originalName, skins.playerNameMap);

            if (!replacedName.getString().equals(originalName.getString())) {

                MessageType.Parameters modifiedParams = new MessageType.Parameters(
                        params.type(),
                        replacedName,
                        params.targetName()
                );

                return original.call(globalIndex, sender, index, signature, body, unsignedContent, filterMask, modifiedParams);
            }
        }

        return original.call(globalIndex, sender, index, signature, body, unsignedContent, filterMask, params);
    }

    @WrapOperation(
            method = "cleanUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"
            )
    )
    private Text cleanupMixin(ServerPlayerEntity instance, Operation<Text> original) {
        // 1. Get the original display name component (this might contain custom styles/hover text)
        Text originalDisplayName = original.call(instance);

        // 2. Get your name map
        Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();

        if (!(wildcardObj instanceof WackySkins skins)) {
            // If WackySkins isn't active, return the original display name
            return originalDisplayName;
        }

        // 3. Process the display name using the recursive replacement method
        // The displayName is the first (and only) argument in the "multiplayer.player.left" translation.
        Text replacedDisplayName = replaceNamesInText(originalDisplayName, skins.playerNameMap);

        return replacedDisplayName;
    }


    private Text replaceNamesInText(Text text, Map<String, Pair<String, Integer>> nameMap) {
        if (!(text instanceof MutableText mutable)) {
            return text;
        }

        String currentText = mutable.getString();
        for (Map.Entry<String, Pair<String, Integer>> entry : nameMap.entrySet()) {
            if (currentText.equals(entry.getKey())) {
                String replacement = entry.getValue().getLeft();
                int right = entry.getValue().getRight();

                Formatting color;
                if (right >= 4) {
                    color = Formatting.DARK_GREEN;
                } else if (right == 3) {
                    color = Formatting.GREEN;
                } else if (right == 2) {
                    color = Formatting.YELLOW;
                } else if (right == 1) {
                    color = Formatting.RED;
                } else {
                    color = null;
                }

                MutableText replaced = Text.literal(replacement);
                if (color != null) {
                    replaced.setStyle(mutable.getStyle().withFormatting(color));
                } else {
                    replaced.setStyle(mutable.getStyle());
                }

                return replaced;
            }
        }

        MutableText result = mutable.copy();
        result.getSiblings().clear();

        for (Text sibling : mutable.getSiblings()) {
            result.append(replaceNamesInText(sibling, nameMap));
        }

        return result;
    }

}