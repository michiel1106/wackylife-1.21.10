package bikerboys.wackylife.gui.trivia;

import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.util.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;
import net.minecraft.text.*;
import net.minecraft.util.*;

import java.awt.*;
import java.util.List;

import bikerboys.wackylife.entity.triviabot.trivia.Question;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.util.ArrayList;
import java.util.UUID;

public class QuizScreen extends DefaultScreen {
    Question question;
    UUID uuid;
    MinecraftClient minecraft = MinecraftClient.getInstance();

    private static final int[] ANSWER_COLORS = {
            TextColors.PASTEL_BLUE, TextColors.PASTEL_ORANGE, TextColors.PASTEL_LIME, TextColors.PASTEL_YELLOW, TextColors.PASTEL_RED
    };

    private final List<List<OrderedText>> answers = new ArrayList<>();
    private String difficulty = "Difficulty: null";
    private int timerSeconds = Trivia.getTicksLeft() / 20;
    private final List<Rectangle> answerRects = new ArrayList<>();

    public QuizScreen(Question question, UUID uuid) {
        super(Text.literal("Quiz Screen"));
        this.question = question;
        this.uuid = uuid;
    }

    @Override
    protected void init() {
        super.init();
        timerSeconds = Trivia.getTicksLeft() / 20;

        int fifth3 = startX + (BG_WIDTH / 5) * 3;
        int answersStartX = fifth3 + 15;
        int answersStopX = endX - 15;

        int maxWidth = answersStopX - answersStartX;

        int currentYPos = startY + 30;
        int gap = 8;
        answers.clear();
        answerRects.clear();
        for (int i = 0; i < question.answers().size(); i++) {
            char answerIndex = (char) (i+65);
            MutableText label = TextUtils.format("{}: ", answerIndex).formatted(Formatting.BOLD);
            MutableText answerText = Text.literal(question.answers().get(i));
            answerText.setStyle(answerText.getStyle().withBold(false));
            Text text = label.append(answerText);
            List<OrderedText> answer = this.textRenderer.wrapLines(text, maxWidth);
            answers.add(answer);
            int answerBoxHeight = this.textRenderer.fontHeight * answer.size()+2;
            int answerBoxWidth = 0;
            for (OrderedText line : answer) {
                int lineWidth = this.textRenderer.getWidth(line);
                if (lineWidth > answerBoxWidth) answerBoxWidth = lineWidth;
            }
            answerBoxWidth += 2;

            Rectangle rect = new Rectangle(answersStartX, currentYPos, answerBoxWidth, answerBoxHeight);
            answerRects.add(rect);
            currentYPos += answerBoxHeight + gap;
        }

        difficulty = "Difficulty: Medium";
    }

    @Override
    public void tick() {
        super.tick();
        timerSeconds = Trivia.getTicksLeft() / 20;
        if (timerSeconds <= 0) {
            this.closeButtonClicked();
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) {
            for (int i = 0; i < answerRects.size(); i++) {
                if (answerRects.get(i).contains(mouseX, mouseY)) {
                   //     if (this.minecraft != null) this.minecraft.setScreen(new ConfirmQuizAnswerScreen(this, i));
                    return true;
                }
            }
        }


        return super.mouseClicked(click, doubled);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        // X
        int fifth1 = startX + (BG_WIDTH / 5);
        int fifth2 = startX + (BG_WIDTH / 5) * 2;
        int fifth4 = startX + (BG_WIDTH / 5) * 4;
        int questionX = startX + 10;
        int questionWidth = (fifth2-10) - questionX;

        // Y
        int minY = startY + 9;
        int maxY = endY - 23;
        int questionY = startY + 30;

        // Timer
        long minutes = timerSeconds / 60;
        long seconds = timerSeconds - minutes * 60;
        String secondsStr = String.valueOf(seconds);
        String minutesStr = String.valueOf(minutes);
        while (secondsStr.length() < 2) secondsStr = "0" + secondsStr;
        while (minutesStr.length() < 2) minutesStr = "0" + minutesStr;

        Text timerText = TextUtils.format("{}:{}", minutesStr, secondsStr);
        if (timerSeconds <= 5) {
            RenderUtils.text(timerText, centerX, minY).anchorCenter().colored(TextColors.RED).render(context, this.textRenderer);
        }
        else if (timerSeconds <= 30) {
            RenderUtils.text(timerText, centerX, minY).anchorCenter().colored(TextColors.ORANGE).render(context, this.textRenderer);
        }
        else {
            RenderUtils.text(timerText, centerX, minY).anchorCenter().render(context, this.textRenderer);
        }

        // Difficulty
        RenderUtils.text(difficulty, centerX, maxY).anchorCenter().render(context, this.textRenderer);

        // Questions
        RenderUtils.text(Text.literal("Question").formatted(Formatting.UNDERLINE), fifth1, minY).anchorCenter().render(context, this.textRenderer);
        List<OrderedText> wrappedQuestion = this.textRenderer.wrapLines(Text.literal(question.question()), questionWidth);
        for (int i = 0; i < wrappedQuestion.size(); i++) {
            RenderUtils.text(wrappedQuestion.get(i), questionX, questionY + i * this.textRenderer.fontHeight).render(context, this.textRenderer);
        }

        // Answers
        RenderUtils.text(Text.literal("Answers").formatted(Formatting.UNDERLINE), fifth4, minY).anchorCenter().render(context, this.textRenderer);
        for (int i = 0; i < question.answers().size(); i++) {
            Rectangle rect = answerRects.get(i);
            int borderColor = ANSWER_COLORS[i % ANSWER_COLORS.length];
            context.fill(rect.x - 1, rect.y - 1, rect.x + rect.width + 1, rect.y, borderColor); // top border
            context.fill(rect.x - 1, rect.y + rect.height, rect.x + rect.width + 2, rect.y + rect.height + 2, borderColor); // bottom
            context.fill(rect.x - 1, rect.y, rect.x, rect.y + rect.height, borderColor); // left
            context.fill(rect.x + rect.width, rect.y-1, rect.x + rect.width + 2, rect.y + rect.height, borderColor); // right

            // Check if the mouse is hovering over this answer
            boolean hovered = rect.contains(mouseX, mouseY);
            int textColor = hovered ? TextColors.WHITE : DEFAULT_TEXT_COLOR;

            // Draw each line
            int lineY = rect.y + 2;
            for (OrderedText line : answers.get(i)) {
                RenderUtils.text(line, rect.x+1, lineY).colored(textColor).render(context, this.textRenderer);
                lineY += this.textRenderer.fontHeight;
            }
        }

        // Entity in the middle
        context.fill(centerX-33, centerY-55, centerX+33, centerY+55, TextColors.BLACK);
        drawBot(context, startX, startY, mouseX, mouseY, centerX, centerY, 40);
    }

    private void drawBot(DrawContext context, int i, int j, int mouseX, int mouseY, int x, int y, int size) {
        if (minecraft == null) return;
        if (minecraft.world == null) return;
        if (minecraft.player == null) return;
        TriviaBot bot = null;
        for (TriviaBot entity : minecraft.world.getEntitiesByClass(TriviaBot.class, minecraft.player.getBoundingBox().expand(10), entity->true)) {
            if (bot == null || minecraft.player.distanceTo(entity) < minecraft.player.distanceTo(bot)) {
                bot = entity;
            }
        }
        if (bot != null) {

            InventoryScreen.drawEntity(context, x-30, y-70, x+30, y+70, size, 0.0625F, centerX, centerY+10, bot);

        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

}
