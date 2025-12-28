package bikerboys.wackylife.entity.triviabot.trivia;

import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.networking.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;

import java.util.*;

public class TriviaHandler {
    private TriviaBot bot;
    private int interactedAtAge = 0;
    private int timeToComplete = 6000; // in ticks

    public TriviaHandler(TriviaBot bot) {
        this.bot = bot;

        ServerPlayNetworking.registerGlobalReceiver(SubmitAnswerC2S.ID, ((payload, context) -> {
            UUID uuid = context.player().getUuid();
            if (uuid.equals(bot.serverData.getBoundPlayerUUID())) {
                handleAnswer(payload.index());
            }
        }));
    }

    public ActionResult onInteract(PlayerEntity player, Hand hand) {
        if (player.getEntityWorld().isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayerEntity playerEntity)) {
            return ActionResult.PASS;
        }

        UUID uuid = bot.serverData.getBoundPlayerUUID();
        if (!player.getUuid().equals(uuid)) {
            return ActionResult.PASS;
        }

        if (!bot.canSumbitAnswer()) {
            return ActionResult.PASS;
        }

        startTrivia(playerEntity);
        return ActionResult.SUCCESS;
    }

    public void startTrivia(ServerPlayerEntity boundPlayer) {
        if (!bot.interactedWith() || bot.getQuestion() == null) {
            interactedAtAge = bot.getTickCount();
            bot.setQuestion(generateQuestion(boundPlayer));
            setTimeBasedOnDifficulty();
        }

        sendTimeUpdatePacket();
        ServerPlayNetworking.send(boundPlayer, new S2COpenTriviaScreen(bot.getQuestion(), bot.getUuid()));
        bot.setInteractedWith(true);
    }

    public void tick() {
        // Decrement analyzing time every 2 ticks
        if (bot.age % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime() - 1);
        }

        if (bot.submittedAnswer()) {
            // Handle animation for answered questions
            if (bot.answeredRight()) {
                // Correct answer animation - fly up after analyzing
                if (bot.getAnalyzingTime() < -80) {
                    bot.setLeaving(true);
                    if (bot.hasVehicle()) bot.dismountVehicle();
                    bot.noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime() + 80) / (20.0f)));
                    bot.setVelocity(0, velocity, 0);
                    if (bot.getAnalyzingTime() < -200) {
                        bot.serverData.despawn();
                    }
                }
            } else {
                // Incorrect answer animation - fly up after analyzing
                if (bot.getAnalyzingTime() < -100) {
                    if (bot.hasVehicle()) bot.dismountVehicle();
                    bot.noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime() + 100) / (20.0f)));
                    bot.setVelocity(0, velocity, 0);
                    if (bot.getAnalyzingTime() < -200) {
                        bot.serverData.despawn();
                    }
                }
            }
        } else {
            // Handle normal movement and timer
            bot.serverData.handleHighVelocity();

            // Check if time ran out
            if (bot.interactedWith() && getRemainingTicks() <= 0) {
                if (!bot.ranOutOfTime()) {
                    ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
                    if (boundPlayer != null) {
                        // Notify player time is up - they didn't answer
                    }
                }
                bot.setRanOutOfTime(true);
            }
        }
    }

    public boolean handleAnswer(int answerIndex) {
        if (bot.getEntityWorld().isClient()) return false;
        if (!bot.canSumbitAnswer()) return false;

        bot.setSubmittedAnswer(true);
        bot.setAnalyzingTime(42); // Start animation countdown

        if (answerIndex == bot.getQuestion().rightQuestionIndex()) {
            onSucceed();
            bot.setAnsweredRight(true);
        } else {
            onFail();
            bot.setAnsweredRight(false);
        }

        return true;
    }

    public void sendTimeUpdatePacket() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player != null) {
            int ticksSinceStart = bot.age - interactedAtAge;
            ServerPlayNetworking.send(player, new QuestionTimeLeftUpdateS2C(timeToComplete - ticksSinceStart));
        }
    }

    public int getRemainingTicks() {
        int ticksSinceStart = bot.getTickCount() - interactedAtAge;
        return timeToComplete - ticksSinceStart;
    }

    private Question generateQuestion(ServerPlayerEntity boundPlayer) {
        return bot.getQuestion();
    }

    private void setTimeBasedOnDifficulty() {
        timeToComplete = 6000;
    }

    private void onSucceed() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player == null) return;
    }

    private void onFail() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player == null) return;
    }
}