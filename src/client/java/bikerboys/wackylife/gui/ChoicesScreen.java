package bikerboys.wackylife.gui;


import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChoicesScreen extends Screen {
    public static final Identifier BACKGROUND = Identifier.of("wackylife:choices/choices");
    private final ChoicePair firstChoicePair;
    private final ChoicePair secondChoicePair;
    private ButtonWidget confirmButton;
    private boolean selectedChoice = false;
    private boolean selectedLeft;

    public ChoicesScreen(ChoicePair firstChoicePair, ChoicePair secondChoicePair) {
        super(Text.translatable("gui.choices.title"));
        this.firstChoicePair = firstChoicePair;
        this.secondChoicePair = secondChoicePair;
    }

    public void onDisplayed() {
        super.onDisplayed();
    }

    protected void init() {
        super.init();
        this.addChoicePairWidget(this.width / 2 - 120, this.height / 4 + 14, true);
        this.addChoicePairWidget(this.width / 2, this.height / 4 + 14, false);
        this.confirmButton = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 40, this.height / 4 + 192, 80, 20, Text.translatable("gui.confirm"), (button) -> {
            this.onConfirm();
        }, (textSupplier) -> {
            return Text.translatable("gui.confirm");
        }) {
            public void playDownSound(SoundManager soundManager) {
                soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        });
        this.confirmButton.active = false;
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 2.0F));
    }

    protected void addChoicePairWidget(int x, int y, boolean left) {
        ChoicePair pair = left ? this.firstChoicePair : this.secondChoicePair;
        Choice posChoice = ChoiceRegistry.get(pair.positiveChoice());
        Choice negChoice = ChoiceRegistry.get(pair.negativeChoice());

        MutableText positiveText = Text.translatable(posChoice.getTranslationKey());
        MutableText negativeText = Text.translatable(negChoice.getTranslationKey());

        this.addDrawableChild(new ChoicePairWidget(x, y, positiveText, negativeText, (button) -> {
            this.onChoicePress(left);
        }, this.textRenderer, left, false));
    }

    public boolean shouldPause() {
        return false;
    }

    public boolean shouldCloseOnEsc() {
        return this.client != null && this.client.player != null && this.client.player.getAbilities().creativeMode;
    }

    protected void onChoicePress(boolean left) {
        this.selectedChoice = true;
        this.selectedLeft = left;
        this.confirmButton.active = true;
    }

    protected void onConfirm() {
        if (this.selectedChoice) {
            ChoicePair selectedPair = this.selectedLeft ? this.firstChoicePair : this.secondChoicePair;
            ClientPlayNetworking.send(new SendChoices(selectedPair));
            this.close();
        }

    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, this.width / 2 - 128, this.height / 4, 255, 256);
    }
}
