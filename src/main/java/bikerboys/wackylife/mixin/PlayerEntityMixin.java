package bikerboys.wackylife.mixin;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
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


        if (!(wildcardObj instanceof WackySkins skins)) {
            return originalMessage;
        }

        Text replaced = replaceNamesInText(originalMessage, skins.playerNameMap);

        return replaced;
    }

    private Text replaceNamesInText(Text text, Map<String, Pair<String, Integer>> nameMap) {
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
                    Pair<String, Integer> stringIntegerPair = nameMap.get(s);
                    if (stringIntegerPair != null) {
                        replacedArgs[i] = stringIntegerPair.getLeft();
                    } else {
                        replacedArgs[i] = s;
                    }
                } else {
                    replacedArgs[i] = arg;
                }
            }

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
        for (Map.Entry<String, Pair<String, Integer>> entry : nameMap.entrySet()) {
            if (currentText.equals(entry.getKey())) {
                MutableText replaced = Text.literal(entry.getValue().getLeft());
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