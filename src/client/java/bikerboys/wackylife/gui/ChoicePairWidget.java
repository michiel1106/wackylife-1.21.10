package bikerboys.wackylife.gui;


import com.fasterxml.jackson.annotation.*;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ChoicePairWidget extends ButtonWidget {
    public static final Identifier LEFT_CHOICE = Identifier.of("wackylife:choices/left");
    public static final Identifier LEFT_CHOICE_HIGHLIGHTED = Identifier.of("wackylife:choices/left_highlighted");
    public static final Identifier LEFT_CHOICE_SELECTED = Identifier.of("wackylife:choices/left_selected");
    public static final Identifier RIGHT_CHOICE = Identifier.of("wackylife:choices/right");
    public static final Identifier RIGHT_CHOICE_HIGHLIGHTED = Identifier.of("wackylife:choices/right_highlighted");
    public static final Identifier RIGHT_CHOICE_SELECTED = Identifier.of("wackylife:choices/right_selected");
    public static final Identifier SCRIBBLE = Identifier.of("wackylife:choices/scribble");
    private final boolean left;
    private final boolean negativeOnly;
    private final TextRenderer textRenderer;
    private final MutableText positiveText;
    private final MutableText negativeText;
    private @Nullable MultilineText positiveMultilineText;
    private @Nullable MultilineText negativeMultilineText;

    public ChoicePairWidget(int x, int y, MutableText positiveText, MutableText negativeText, ButtonWidget.PressAction pressAction, TextRenderer textRenderer, boolean left, boolean negativeOnly) {
        super(x, y, 119, 151, Text.empty(), pressAction, (textSupplier) -> {
            return positiveText.append(" but ").append(negativeText);
        });
        this.textRenderer = textRenderer;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.left = left;
        this.negativeOnly = negativeOnly;
    }

    protected MultilineText toMultilineText(MutableText text) {
        return MultilineText.create(this.textRenderer, 105, 6, text);
    }

    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier texture = this.left ? (this.isFocused() ? LEFT_CHOICE_SELECTED : (this.isHovered() ? LEFT_CHOICE_HIGHLIGHTED : LEFT_CHOICE)) : (this.isFocused() ? RIGHT_CHOICE_SELECTED : (this.isHovered() ? RIGHT_CHOICE_HIGHLIGHTED : RIGHT_CHOICE));
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, 256, 256, 0, 0, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        if (this.positiveMultilineText == null) {
            this.positiveMultilineText = this.toMultilineText(this.positiveText);
        }

        if (this.negativeMultilineText == null) {
            this.negativeMultilineText = this.toMultilineText(this.negativeText);
        }

        if (this.positiveMultilineText != null) {
            // FIX 1: 'draw' needs 7 arguments: (context, alignment, x, y, lineHeight, shadow, color)
            this.positiveMultilineText.draw(
                    context,
                    MultilineText.Alignment.CENTER,
                    this.getX() + 60,
                    this.getY() + 12,
                    this.textRenderer.fontHeight,
                    false,
                    0xFFFFFFFF
            );
        }

        if (this.negativeMultilineText != null) {
            // FIX 2: 'method_30888' is gone. Replaced with the 7-argument 'draw' call.
            this.negativeMultilineText.draw(
                    context,
                    MultilineText.Alignment.CENTER,
                    this.getX() + 60,
                    this.getY() + 88,
                    this.textRenderer.fontHeight,
                    false,
                    0xFFFFFFFF
            );
        }

        if (this.negativeOnly) {
            // FIX 3: 'method_52709' is gone. Replaced with the 10-argument 'drawTexture'
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCRIBBLE, 256, 256, 0, 0, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1);
        }
    }
}

