package bikerboys.wackylife.mixin;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.*;

@Debug(export = true)
@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin {
    @WrapOperation(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"
            )
    )
    private Text changedeathmessage(DamageTracker instance, Operation<Text> original) {
        Text originalMessage = original.call(instance);
        Wildcard wildcardObj = WackyLife.wackyLife.getWildcardObj();

        System.out.println("[DEBUG] originalMessage: " + originalMessage.getString());
        System.out.println("[DEBUG] siblings: " + originalMessage.getSiblings());

        if (!(wildcardObj instanceof WackySkins skins)) {
            System.out.println("[DEBUG] wildcardObj not WackySkins");
            return originalMessage;
        }

        Text replaced = replaceNamesInText(originalMessage, skins.playerNameMap);
        System.out.println("[DEBUG] replacedMessage: " + replaced.getString());

        return replaced;
    }

    private Text replaceNamesInText(Text text, Map<String, String> nameMap) {
        if (!(text instanceof MutableText mutable)) {
            return text;
        }

        // Handle translatable components
        if (mutable.getContent() instanceof TranslatableTextContent translatable) {
            Object[] args = translatable.getArgs();
            Object[] replacedArgs = new Object[args.length];

            // Replace arguments
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof Text t) {
                    // Recursively replace in nested Text components (including siblings)
                    replacedArgs[i] = replaceNamesInText(t, nameMap);
                } else if (arg instanceof String s) {
                    // Check if this string should be replaced
                    replacedArgs[i] = nameMap.getOrDefault(s, s);
                } else {
                    replacedArgs[i] = arg;
                }
            }

            System.out.println("[DEBUG] Original args: " + java.util.Arrays.toString(args));
            System.out.println("[DEBUG] Replaced args: " + java.util.Arrays.toString(replacedArgs));

            // Create new translatable with replaced arguments
            MutableText result = Text.translatable(translatable.getKey(), replacedArgs);
            result.setStyle(mutable.getStyle());

            // Append siblings with replacements
            for (Text sibling : mutable.getSiblings()) {
                result.append(replaceNamesInText(sibling, nameMap));
            }

            return result;
        }

        // Check if the entire text (including siblings) matches a name to replace
        String currentText = mutable.getString();
        for (Map.Entry<String, String> entry : nameMap.entrySet()) {
            if (currentText.equals(entry.getKey())) {
                System.out.println("[DEBUG] Replacing name: " + entry.getKey() + " -> " + entry.getValue());
                MutableText replaced = Text.literal(entry.getValue());
                replaced.setStyle(mutable.getStyle());
                // Copy over interaction properties if they exist
                return replaced;
            }
        }

        // For other content types, process siblings
        MutableText result = mutable.copy();
        result.getSiblings().clear();

        for (Text sibling : mutable.getSiblings()) {
            result.append(replaceNamesInText(sibling, nameMap));
        }

        return result;
    }
}