package bikerboys.wackylife.entity.triviabot;

import net.minecraft.client.render.entity.state.*;
import net.minecraft.entity.*;

public class TriviaBotRenderState extends LivingEntityRenderState {
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

    public boolean santaBot = false;
}
