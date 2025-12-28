package bikerboys.wackylife.entity.triviabot;

import net.minecraft.entity.*;

public class TriviaBotClientData {
    private TriviaBot bot;
    public TriviaBotClientData(TriviaBot bot) {
        this.bot = bot;
    }

    public final AnimationState glideAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState countdownAnimationState = new AnimationState();
    public final AnimationState analyzingAnimationState = new AnimationState();
    public final AnimationState answerCorrectAnimationState = new AnimationState();
    public final AnimationState answerIncorrectAnimationState = new AnimationState();
    public final AnimationState snailTransformAnimationState = new AnimationState();

    public final AnimationState santaAnalyzingAnimation = new AnimationState();
    public final AnimationState santaAnswerCorrectAnimation = new AnimationState();
    public final AnimationState santaAnswerIncorrectAnimation = new AnimationState();
    public final AnimationState santaFlyAnimation = new AnimationState();
    public final AnimationState santaGlideAnimation = new AnimationState();
    public final AnimationState santaIdleAnimation = new AnimationState();
    public final AnimationState santaWaveAnimation = new AnimationState();

    public void tick() {
        if (!bot.getEntityWorld().isClient()) return;
        updateAnimations();
    }

    private boolean lastSubmittedAnswer = false;
    private boolean lastRanOutOfTime = false;

    public void updateAnimations() {
        // Start analyzing when answer is first submitted
        if (bot.submittedAnswer() && !lastSubmittedAnswer) {
            pauseAllAnimations("analyzing");
            analyzingAnimationState.startIfNotRunning(bot.age);
        }

        // Keep analyzing animation while analyzing time > 0
        if (bot.getAnalyzingTime() > 0) {
            pauseAllAnimations("analyzing");
        }
        // Switch to correct/incorrect when analyzing is done
        else if (bot.submittedAnswer()) {
            if (bot.getAnalyzingTime() == 0) {
                if (bot.answeredRight()) {
                    pauseAllAnimations("answer_correct");
                    answerCorrectAnimationState.startIfNotRunning(bot.age);
                }
                else {
                    pauseAllAnimations("answer_incorrect");
                    answerIncorrectAnimationState.startIfNotRunning(bot.age);
                }
            }
        }
        // Handle waiting for answer
        else if (bot.interactedWith()) {
            pauseAllAnimations("countdown");
            countdownAnimationState.startIfNotRunning(bot.age);
        }
        // Handle movement/idle
        else if (bot.isBotGliding()) {
            pauseAllAnimations("glide");
            glideAnimationState.startIfNotRunning(bot.age);
        }
        else if (bot.limbAnimator.isLimbMoving() && bot.limbAnimator.getSpeed() > 0.02) {
            pauseAllAnimations("walk");
            walkAnimationState.startIfNotRunning(bot.age);
        }
        else {
            pauseAllAnimations("idle");
            idleAnimationState.startIfNotRunning(bot.age);
        }

        lastSubmittedAnswer = bot.submittedAnswer();
        lastRanOutOfTime = bot.ranOutOfTime();
    }

    public void pauseAllAnimations(String except) {
        if (!except.equalsIgnoreCase("glide")) glideAnimationState.stop();
        if (!except.equalsIgnoreCase("walk")) walkAnimationState.stop();
        if (!except.equalsIgnoreCase("idle")) idleAnimationState.stop();
        if (!except.equalsIgnoreCase("countdown")) countdownAnimationState.stop();
        if (!except.equalsIgnoreCase("analyzing")) analyzingAnimationState.stop();
        if (!except.equalsIgnoreCase("answer_correct")) answerCorrectAnimationState.stop();
        if (!except.equalsIgnoreCase("answer_incorrect")) answerIncorrectAnimationState.stop();
        if (!except.equalsIgnoreCase("snail_transform")) snailTransformAnimationState.stop();
    }
}