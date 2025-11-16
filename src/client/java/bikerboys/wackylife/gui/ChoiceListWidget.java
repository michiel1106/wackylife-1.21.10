package bikerboys.wackylife.gui;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.navigation.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;


public class ChoiceListWidget implements Drawable, Element, Widget {
    public static final Identifier POSITIVE_TEXTURE = Identifier.of("wackylife:choices/choice_background_positive");
    public static final Identifier NEGATIVE_TEXTURE = Identifier.of("wackylife:choices/choice_background_negative");
    private int x;
    private int y;
    private final int height;
    private final TextRenderer textRenderer;
    private final List<Entry> entries;
    private int prevMouseIndex = -1;
    private int prevTick;

    public ChoiceListWidget(int x, int y, int height, TextRenderer textRenderer, Collection<Choice> choiceInstances) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.textRenderer = textRenderer;
        this.entries = choiceInstances.stream().map((choiceInstance) -> {
            return new Entry(MultilineText.create(this.textRenderer, Text.translatable(choiceInstance.getTranslationKey()), 105), 1, choiceInstance.isPositive(), this.textRenderer);
        }).toList();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.entries.isEmpty()) {
            this.prevTick = ModClient.getTick();
            this.prevMouseIndex = -1;
        } else {
            boolean hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
            int tick = ModClient.getTick();
            boolean newTick = tick != this.prevTick;
            float tickDelta = MinecraftClient.getInstance().method_1488();
            float time = tickDelta + (float)tick;
            int size = this.entries.size();
            int totalHeight = Math.min(size * 24, this.height / 2);
            float spacing = (float)totalHeight / (float)size;
            int minY = this.height / 2 - totalHeight / 2 - 29;
            int mouseIndex = MathHelper.clamp((int)((float)(mouseY - minY) / spacing), 0, size - 1);

            for(int i = 0; i < size; ++i) {
                Entry entry = (Entry)this.entries.get(i);
                if (newTick && size > 1) {
                    entry.update(time, mouseIndex == i && hovered ? ChoiceListWidget.EntryState.HOVER : ChoiceListWidget.EntryState.NONE);
                }

                context.getMatrices().pushMatrix();
                context.getMatrices().translate((float)this.x, (float)minY + (float)i * spacing, (float)i);
                entry.render(context, time);
                context.getMatrices().popMatrix();
            }

            if (hovered && mouseIndex != this.prevMouseIndex) {
                MinecraftClient.getInstance().getSoundManager().method_4873(PositionedSoundInstance.master(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 2.0F));
            }

            this.prevMouseIndex = hovered ? mouseIndex : -1;
            this.prevTick = tick;
        }
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return 128;
    }

    public int getHeight() {
        return this.height;
    }

    public void forEachChild(Consumer<ClickableWidget> consumer) {
    }

    public static class Entry {
        private final MultilineText text;
        private final @Nullable MutableText countText;
        private final boolean positive;
        private float prevX = 0.0F;
        private float prevY = 0.0F;
        private EntryState state;
        private int tick;
        private final TextRenderer textRenderer;

        public Entry(MultilineText text, int count, boolean positive, TextRenderer textRenderer) {
            this.state = ChoiceListWidget.EntryState.NONE;
            this.text = text;
            this.positive = positive;
            this.countText = count > 1 ? Text.literal("" + count + "x").styled((style) -> {
                return style.withColor(16762959);
            }) : null;
            this.textRenderer = textRenderer;
        }

        public void update(float time, EntryState state) {
            if (this.state != state) {
                this.tick = MathHelper.floor(time);
                this.prevX = this.lerpX(time);
                this.prevY = this.lerpY(time);
            }

            this.state = state;
        }

        public float lerpX(float time) {
            return MathHelper.lerp(Math.min(1.0F, 0.4F * (time - (float)this.tick)), this.prevX, (float)this.state.offsetX);
        }

        public float lerpY(float time) {
            return MathHelper.lerp(Math.min(1.0F, 0.4F * (time - (float)this.tick)), this.prevY, (float)this.state.offsetY);
        }

        public void render(DrawContext context, float time) {
            context.getMatrices().translate(this.lerpX(time), this.lerpY(time), 0.0F);
            context.method_52708(this.positive ? ChoiceListWidget.POSITIVE_TEXTURE : ChoiceListWidget.NEGATIVE_TEXTURE, 128, 128, 0, 0, 0, 0, 109, 57);
            this.text.method_30888(context, 55, 3);
            if (this.countText != null) {
                this.textRenderer.getWidth(this.countText);
                context.method_51439(this.textRenderer, this.countText, 107 - this.textRenderer.getWidth(this.countText), 48, 16777215, true);
            }

        }
    }

    public static enum EntryState {
        NONE(0, 0),
        HOVER(108, 0);

        public final int offsetX;
        public final int offsetY;

        private EntryState(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }
}
