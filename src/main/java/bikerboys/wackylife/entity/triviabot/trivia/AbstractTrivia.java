package bikerboys.wackylife.entity.triviabot.trivia;

import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.networking.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;

public abstract class AbstractTrivia {
    protected TriviaBot bot;
    public int countdownTimer = 0;
    public int timeToComplete = 0;
    public Question question;
    public int interactedAtAge = 0;  // ADD THIS
    public AbstractTrivia(TriviaBot bot) {
        this.bot = bot;
    }

    public void tick() {
        if (bot.interactedWith() && countdownTimer > 0) {
            countdownTimer--;
            bot.triviaHandler.sendTimeUpdatePacket();
        }
        if (countdownTimer == 1) {
            bot.triviaHandler.handleAnswer(6);
        }
    }


    public void startTrivia(ServerPlayerEntity boundPlayer) {
        if (!bot.interactedWith() || question == null) {
            interactedAtAge = bot.age;
            Pair<Integer, Question> triviaQuestion = generateTrivia(boundPlayer);
            question = triviaQuestion.getRight();
            setTimeBasedOnDifficulty(triviaQuestion.getLeft());
            countdownTimer = timeToComplete * 20;
        }
        sendTimeUpdatePacket();
        ServerPlayNetworking.send(boundPlayer, new S2COpenTriviaScreen(question, bot.getUuid()));
        bot.setInteractedWith(true);
    }

    abstract Pair<Integer, Question> generateTrivia(ServerPlayerEntity boundPlayer);
    abstract void setTimeBasedOnDifficulty(int difficulty);

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.SUCCESS;
    }

    public void sendTimeUpdatePacket() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player != null) {
            int ticksElapsed = countdownTimer;
            ServerPlayNetworking.send(player, new QuestionTimeLeftUpdateS2C(ticksElapsed));
        }
    }

    public int getRemainingTicks() {
        // CHANGE THIS - use absolute calculation instead of relying on countdownTimer
        int ticksSinceStart = bot.age - interactedAtAge;
        return (timeToComplete * 20) - ticksSinceStart;
    }

    public boolean handleAnswer(int answer) {
        if (bot.getEntityWorld().isClient()) return false;
        if (bot.submittedAnswer()) return false;
        bot.setSubmittedAnswer(true);
        if (answer == question.rightQuestionIndex()) {
            answeredCorrect();
        }
        else {
            answeredIncorrect();
        }
        return true;
    }

    public void answeredCorrect() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        bot.setAnsweredRight(true);
    }

    public void answeredIncorrect() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        bot.setAnsweredRight(false);
    }
}
