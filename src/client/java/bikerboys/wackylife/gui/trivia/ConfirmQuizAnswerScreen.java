package bikerboys.wackylife.gui.trivia;

import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.*;

import java.awt.*;

public class ConfirmQuizAnswerScreen extends DefaultScreen {
    private final QuizScreen parent;
    private final int answerIndex;

    public ConfirmQuizAnswerScreen(QuizScreen parent, int answerIndex) {
        super(Text.literal("Confirm Answer"), 150, 60);
        this.parent = parent;
        this.answerIndex = answerIndex;
    }

    @Override
    public boolean allowCloseButton() {
        return false;
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Confirm"), btn -> {
                            ClientPlayNetworking.send(new SubmitAnswerC2S(answerIndex));
                            this.close();
                        })
                        .position(startX + 8, endY - 28)
                        .size(60, 20)
                        .build()
        );

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Cancel"), btn -> {
                            if (MinecraftClient.getInstance() != null) MinecraftClient.getInstance().setScreen(parent);
                        })
                        .position(endX  - 68, endY - 28)
                        .size(60, 20)
                        .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        RenderUtils.text("Submit answer?", centerX, startY + 10).anchorCenter().render(context, this.textRenderer);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }

}
