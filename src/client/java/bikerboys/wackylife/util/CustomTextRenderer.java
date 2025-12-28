package bikerboys.wackylife.util;

import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.text.*;

import java.util.*;

public class CustomTextRenderer {
    private final Text text;
    private OrderedText orderedText;
    private final double x;
    private final double y;
    private float scaleX = 1;
    private float scaleY = 1;
    private boolean shadow = false;
    private int textColor = TextColors.DEFAULT;
    private Anchor anchor = Anchor.LEFT;
    private boolean wrapLines = false;
    private int wrapMaxWidth = 100;
    private int wrapGapY = 7;

    public CustomTextRenderer(Text text, double x, double y) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.orderedText = null;
    }

    public CustomTextRenderer(OrderedText orderedText, double x, double y) {
        this.x = x;
        this.y = y;
        this.orderedText = orderedText;
        this.text = null;
    }

    public CustomTextRenderer anchorCenter() {
        anchor = Anchor.CENTER;
        return this;
    }

    public CustomTextRenderer anchorRight() {
        anchor = Anchor.RIGHT;
        return this;
    }

    public CustomTextRenderer withShadow() {
        shadow = true;
        return this;
    }

    public CustomTextRenderer wrapLines(int maxWidth, int gapY) {
        wrapLines = true;
        wrapMaxWidth = maxWidth;
        wrapGapY = gapY;
        if (orderedText == null && text != null) {
            orderedText = text.asOrderedText();
        }
        return this;
    }

    public CustomTextRenderer scaled(float newScaleX, float newScaleY) {
        scaleX = newScaleX;
        scaleY = newScaleY;
        return this;
    }

    public CustomTextRenderer colored(int color) {
        textColor = color;
        return this;
    }

    private boolean isScaled() {
        return scaleX != 1 || scaleY != 1;
    }

    public int render(DrawContext context, TextRenderer textRenderer) {
        if (isScaled()) {
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(scaleX, scaleY);

        }

        int renderedTextHeight = textRenderer.fontHeight;
        double offsetX = 0;
        double textWidth = 0;
        if (this.text != null) textWidth = textRenderer.getWidth(text);
        if (this.orderedText != null) textWidth = textRenderer.getWidth(orderedText);

        if (anchor == Anchor.CENTER) offsetX -= textWidth/2.0;
        if (anchor == Anchor.RIGHT) offsetX -= textWidth;

        if (this.orderedText != null) {
            if (wrapLines) {
                List<OrderedText> wrappedText = textRenderer.wrapLines(text, wrapMaxWidth);
                int offsetY = 0;
                for (OrderedText line : wrappedText) {
                    context.drawText(textRenderer, line, (int) (x / scaleX + offsetX), (int) (y / scaleY + offsetY), textColor, shadow);
                    offsetY += textRenderer.fontHeight + wrapGapY;
                }
                renderedTextHeight = offsetY;
            }
            else {
                context.drawText(textRenderer, orderedText, (int) (x / scaleX + offsetX), (int) (y / scaleY), textColor, shadow);
            }
        }
        else if (this.text != null) {
            context.drawText(textRenderer, text, (int)(x / scaleX + offsetX), (int)(y / scaleY), textColor, shadow);
        }

        if (isScaled()) {
            context.getMatrices().popMatrix();
        }
        return renderedTextHeight;
    }

    public enum Anchor {
        LEFT,
        CENTER,
        RIGHT;
    }
}
