package bikerboys.wackylife.entity.triviabot.trivia;

import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.networking.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;

import java.util.*;

public class TriviaHandler extends AbstractTrivia{
    protected TriviaBot bot;
    protected int interactedAtAge = 0;
    protected int timeToComplete = 6000; // in ticks
    protected int difficulty = 0;

    public TriviaHandler(TriviaBot bot) {
        super(bot);
        this.bot = bot;

        ServerPlayNetworking.registerGlobalReceiver(SubmitAnswerC2S.ID, ((payload, context) -> {

            UUID playerUUID = context.player().getUuid();
            UUID boundUUID = bot.serverData.getBoundPlayer().getUuid();

            System.out.println("Packet received! Player UUID: " + playerUUID + ", Bound UUID: " + boundUUID);
            System.out.println("UUIDs match: " + playerUUID.equals(boundUUID));

            if (playerUUID.equals(boundUUID)) {
                System.out.println("Calling handleAnswer with index: " + payload.index());
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

        if (bot.submittedAnswer()) {
            return ActionResult.PASS;
        }

        if (bot.interactedWith() && getRemainingTicks() <= 0) {
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

    @Override
    Pair<Integer, Question> generateTrivia(ServerPlayerEntity boundPlayer) {
        return new Pair<Integer, Question>(1, Question.DEFAULT);
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
                        // Time expired without answer
                    }
                }
                bot.setRanOutOfTime(true);
            }
        }
    }

    public boolean handleAnswer(int answerIndex) {
        if (bot.getEntityWorld().isClient()) return false;
        if (bot.submittedAnswer()) return false;

        bot.setSubmittedAnswer(true);
        bot.setAnalyzingTime(42);

        System.out.println("Answer submitted! Analyzing time set to: " + bot.getAnalyzingTime());

        if (answerIndex == bot.getQuestion().rightQuestionIndex()) {
            answeredCorrect();
        } else {
            answeredIncorrect();
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

    protected Question generateQuestion(ServerPlayerEntity boundPlayer) {
        return bot.getQuestion();
    }

    protected void setTimeBasedOnDifficulty() {
        timeToComplete = 6000;
    }

    public void answeredCorrect() {
        System.out.println("succeed!");
        bot.setAnsweredRight(true);
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player == null) return;
    }

    public void answeredIncorrect() {
        System.out.println("fail :(");
        bot.setAnsweredRight(false);
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player == null) return;
    }
}