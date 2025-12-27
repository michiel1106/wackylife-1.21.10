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
        if (bot.submittedAnswer() && !lastSubmittedAnswer) {
            pauseAllAnimations("analyzing");
            analyzingAnimationState.start(bot.age);
        }

        if (bot.ranOutOfTime() && !bot.santaBot()) {
            pauseAllAnimations("snail_transform");
            if (!lastRanOutOfTime) {
                snailTransformAnimationState.start(bot.age);
            }
        }
        else if (bot.getAnalyzingTime() > 0) {
            pauseAllAnimations("analyzing");
        }
        else if (bot.submittedAnswer()) {
            if (bot.getAnalyzingTime() == 0) {
                if (bot.answeredRight()) {
                    pauseAllAnimations("answer_correct");
                    answerCorrectAnimationState.start(bot.age);
                }
                else {
                    pauseAllAnimations("answer_incorrect");
                    answerIncorrectAnimationState.start(bot.age);
                }
            }
        }
        else if (bot.interactedWith()) {
            pauseAllAnimations("countdown");
            countdownAnimationState.start(bot.age);
        }
        else if (bot.isBotGliding()) {
            pauseAllAnimations("glide");
            glideAnimationState.start(bot.age);
        }
        else if (bot.limbAnimator.isLimbMoving() && bot.limbAnimator.getSpeed() > 0.02) {
            pauseAllAnimations("walk");
            walkAnimationState.start(bot.age);
        }
        else {
            pauseAllAnimations("idle");
            idleAnimationState.start(bot.age);
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
