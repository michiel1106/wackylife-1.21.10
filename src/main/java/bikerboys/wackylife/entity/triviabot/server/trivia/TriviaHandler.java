package bikerboys.wackylife.entity.triviabot.server.trivia;

import bikerboys.wackylife.entity.triviabot.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;

import java.util.*;

public abstract class TriviaHandler {
    protected TriviaBot bot;
    public int difficulty = 0;
    public int interactedAtAge = 0;
    public int timeToComplete = 0;
    public TriviaQuestion question;

    public TriviaHandler(TriviaBot bot) {
        this.bot = bot;
    }

    public void tick() {
        if (bot.interactedWith()) {
            bot.triviaHandler.sendTimeUpdatePacket();
        }
    }

    public void startTrivia(ServerPlayerEntity boundPlayer) {
        DatapackIntegration.EVENT_TRIVIA_BOT_OPEN.trigger(List.of(
                new DatapackIntegration.Events.MacroEntry("Player", boundPlayer.getScoreboardName()),
                new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
        ));

        if (!bot.interactedWith() || question == null) {
            interactedAtAge = bot.tickCount;
            Tuple<Integer, TriviaQuestion> triviaQuestion = generateTrivia(boundPlayer);
            difficulty = triviaQuestion.x;
            question = triviaQuestion.y;
            setTimeBasedOnDifficulty(difficulty);
        }
        sendTimeUpdatePacket();
        NetworkHandlerServer.sendTriviaPacket(boundPlayer, question.getQuestion(), difficulty, System.currentTimeMillis(), timeToComplete, question.getAnswers());
        bot.setInteractedWith(true);
    }

    abstract Tuple<Integer, TriviaQuestion> generateTrivia(ServerPlayerEntity boundPlayer);
    abstract void setTimeBasedOnDifficulty(int difficulty);

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.SUCCESS;
    }

    public void sendTimeUpdatePacket() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player != null) {
            int ticksSinceStart = bot.tickCount - interactedAtAge;
            NetworkHandlerServer.sendNumberPacket(player, PacketNames.TRIVIA_TIMER, ticksSinceStart);
        }
    }

    public int getRemainingTicks() {
        int ticksSinceStart = bot.tickCount - interactedAtAge;
        return (timeToComplete*20) - ticksSinceStart;
    }

    public boolean handleAnswer(int answer) {
        if (bot.getEntityWorld().isClient()) return false;
        if (bot.submittedAnswer()) return false;
        bot.setSubmittedAnswer(true);
        if (answer == question.getCorrectAnswerIndex()) {
            answeredCorrect();
        }
        else {
            answeredIncorrect();
        }
        return true;
    }

    public void answeredCorrect() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player != null) {
            DatapackIntegration.EVENT_TRIVIA_SUCCEED.trigger(List.of(
                    new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                    new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
            ));
        }
        bot.setAnsweredRight(true);
    }

    public void answeredIncorrect() {
        ServerPlayerEntity player = bot.serverData.getBoundPlayer();
        if (player != null) {
            DatapackIntegration.EVENT_TRIVIA_FAIL.trigger(List.of(
                    new DatapackIntegration.Events.MacroEntry("Player", player.getScoreboardName()),
                    new DatapackIntegration.Events.MacroEntry("TriviaBot", bot.getStringUUID())
            ));
        }
        bot.setAnsweredRight(false);
    }
}
