package bikerboys.wackylife.util;

import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

import java.net.*;
import java.util.*;
import java.util.regex.*;

public class TextUtils {
    private static HashMap<List<String>, List<String>> emotes = new HashMap<List<String>, List<String>>();

    public static void setEmotes() {
        emotes.put(List.of("skull"),List.of("☠"));
        emotes.put(List.of("smile"),List.of("☺"));
        emotes.put(List.of("frown"),List.of("☹"));
        emotes.put(List.of("heart"),List.of("❤"));
        emotes.put(List.of("copyright"),List.of("©"));
        emotes.put(List.of("trademark","tm"),List.of("™"));
    }

    public static String replaceEmotes(String input) {
        for (Map.Entry<List<String>, List<String>> entry : emotes.entrySet()) {
            if (entry.getValue().size()==0) continue;
            String emoteValue = entry.getValue().get(0);
            for (String emote : entry.getKey()) {
                String emoteCode = ":" + emote + ":";
                input = replaceCaseInsensitive(input, emoteCode, emoteValue);
            }
            if (!input.contains(":")) return input;
        }
        return input;
    }

    public static String replaceCaseInsensitive(String input, String replaceWhat, String replaceWith) {
        Pattern pattern = Pattern.compile(replaceWhat, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        String result = matcher.replaceAll(replaceWith);
        return result;
    }

    public static String toRomanNumeral(int num) {
        String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return (num > 0 && num <= romanNumerals.length) ? romanNumerals[num - 1] : String.valueOf(num);
    }



    public static String removeFormattingCodes(String input) {
        return input.replaceAll("§[0-9a-fk-or]", "");
    }

    public static ClickEvent openURLClickEvent(String url) {
        return new ClickEvent.OpenUrl(URI.create(url));
    }

    public static ClickEvent runCommandClickEvent(String command) {
        return new ClickEvent.RunCommand(command);
    }

    public static ClickEvent copyClipboardClickEvent(String copy) {
        return new ClickEvent.CopyToClipboard(copy);

    }

    public static HoverEvent showTextHoverEvent(Text text) {
        return new HoverEvent.ShowText(text);
    }

    public static Text withHover(Text text, Text hover) {
        return text.copy().styled(style -> style.withHoverEvent(showTextHoverEvent(hover)));
    }
    public static Text withHover(String text, String hover) {
        return Text.literal(text).styled(style -> style.withHoverEvent(showTextHoverEvent(Text.literal(hover))));
    }

    public static Text selfMessageText(String message) {
        return runCommandText("/selfmsg " + message);
    }

    public static Text runCommandText(String command) {
        return hereText(runCommandClickEvent(command));
    }

    public static Text openURLText(String url) {
        return hereText(openURLClickEvent(url));
    }

    public static Text copyClipboardText(String copy) {
        return hereText(copyClipboardClickEvent(copy));
    }

    public static Text hereText(ClickEvent event) {
        return clickableText("here", event);
    }

    public static Text clickableText(String label, ClickEvent event) {
        return Text.literal(label)
                .styled(style -> style
                        .withColor(Formatting.BLUE)
                        .withClickEvent(event)
                        .withUnderline(true)
                );
    }


    public static MutableText formatPlain(String template, Object... args) {
        return Text.literal(formatString(template, args));
    }

    public static String formatString(String template, Object... args) {
        return format(template, args).getString();
    }

    public static MutableText format(String template, Object... args) {
        return formatStyled(false, template, args);
    }

    public static MutableText formatLoosely(String template, Object... args) {
        return formatStyled(true, template, args);
    }

    private static MutableText formatStyled(boolean looselyStyled, String template, Object... args) {
        MutableText result = Text.empty();
        StringBuilder resultLooselyStyled = new StringBuilder();

        int argIndex = 0;
        int lastIndex = 0;
        int placeholderIndex = template.indexOf("{}");


        while (placeholderIndex != -1 && argIndex < args.length) {
            if (placeholderIndex > lastIndex) {
                String textBefore = template.substring(lastIndex, placeholderIndex);
                result.append(Text.literal(textBefore));
                resultLooselyStyled.append(textBefore);
            }

            Object arg = args[argIndex];
            Text argText = getTextForArgument(arg);
            result.append(argText);
            resultLooselyStyled.append(argText.getString());

            argIndex++;
            lastIndex = placeholderIndex + 2;
            placeholderIndex = template.indexOf("{}", lastIndex);
        }

        if (lastIndex < template.length()) {
            String remainingText = template.substring(lastIndex);
            result.append(Text.literal(remainingText));
            resultLooselyStyled.append(remainingText);
        }

        if (looselyStyled) {
            return Text.literal(resultLooselyStyled.toString());
        }

        return result;
    }

    private static Text getTextForArgument(Object arg) {
        if (arg == null) {
            return Text.empty();
        }
        if (arg instanceof Text text) {
            return text;
        }
        if (arg instanceof ServerPlayerEntity player) {
            Text name = player.getDisplayName();
            if (name == null) return Text.empty();
            return name;
        }
        if (arg instanceof List<?> list) {
            MutableText text = Text.empty();
            int index = 0;
            for (Object obj : list) {
                if (index != 0) {
                    text.append(Text.of(", "));
                }
                text.append(getTextForArgument(obj));
                index++;
            }
            return text;
        }
        return Text.of(arg.toString());
    }

    public static String pluralize(String text, Integer amount) {
        return pluralize(text, text+"s", amount);
    }

    public static String pluralize(String textSingular, String textPlural, Integer amount) {
        if (amount == null || Math.abs(amount) == 1) {
            return textSingular;
        }
        return textPlural;
    }

    public static String pluralize(String text, Double amount) {
        return pluralize(text, text+"s", amount);
    }

    public static String pluralize(String textSingular, String textPlural, Double amount) {
        if (amount == null || Math.abs(amount) == 1) {
            return textSingular;
        }
        return textPlural;
    }
}
