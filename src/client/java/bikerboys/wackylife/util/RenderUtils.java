package bikerboys.wackylife.util;


import net.minecraft.client.gui.*;
import net.minecraft.text.*;

public class RenderUtils {

    public static void debugX(DrawContext context, int x) {
        context.fill(x, 0, x+1, context.getScaledWindowHeight(), TextColors.DEBUG);

        
    }

    public static void debugY(DrawContext context, int y) {
        context.fill(0, y, context.getScaledWindowWidth(), y+1, TextColors.DEBUG);
    }

    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }



    public static CustomTextRenderer text(Text text, int x, int y) {
        return new CustomTextRenderer(text, x, y);
    }

    public static CustomTextRenderer text(OrderedText text, int x, int y) {
        return new CustomTextRenderer(text, x, y);
    }

    public static CustomTextRenderer text(String text, int x, int y) {
        return new CustomTextRenderer(Text.of(text), x, y);
    }


}
