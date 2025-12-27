package bikerboys.wackylife.entity.triviabot;


import net.minecraft.client.render.entity.*;
import net.minecraft.util.*;

public class TriviaBotRenderer extends AgeableMobRenderer<TriviaBot, TriviaBotRenderState, TriviaBotModel> {

    public TriviaBotRenderer(EntityRendererFactory.Context context) {
        super(context, new TriviaBotModel(context.getPart(TriviaBotModel.TRIVIA_BOT)), new TriviaBotModel(context.getPart(TriviaBotModel.TRIVIA_BOT)), 0.45f);
    }

    @Override
    public TriviaBotRenderState createRenderState() {
        return new TriviaBotRenderState();
    }

    @Override

    public Identifier getTextureLocation(TriviaBotRenderState state) {

        if (state.santaBot) {
            return TriviaBot.SANTABOT_TEXTURE;
        }
        return TriviaBot.DEFAULT_TEXTURE;
    }

    @Override
    public void extractRenderState(TriviaBot triviaBot, TriviaBotRenderState state, float f) {
        super.extractRenderState(triviaBot, state, f);

        state.glideAnimationState.copyFrom(triviaBot.clientData.glideAnimationState);
        state.idleAnimationState.copyFrom(triviaBot.clientData.idleAnimationState);
        state.walkAnimationState.copyFrom(triviaBot.clientData.walkAnimationState);
        state.countdownAnimationState.copyFrom(triviaBot.clientData.countdownAnimationState);
        state.analyzingAnimationState.copyFrom(triviaBot.clientData.analyzingAnimationState);
        state.answerCorrectAnimationState.copyFrom(triviaBot.clientData.answerCorrectAnimationState);
        state.answerIncorrectAnimationState.copyFrom(triviaBot.clientData.answerIncorrectAnimationState);
        state.snailTransformAnimationState.copyFrom(triviaBot.clientData.snailTransformAnimationState);

        state.santaAnalyzingAnimation.copyFrom(triviaBot.clientData.santaAnalyzingAnimation);
        state.santaAnswerCorrectAnimation.copyFrom(triviaBot.clientData.santaAnswerCorrectAnimation);
        state.santaAnswerIncorrectAnimation.copyFrom(triviaBot.clientData.santaAnswerIncorrectAnimation);
        state.santaFlyAnimation.copyFrom(triviaBot.clientData.santaFlyAnimation);
        state.santaGlideAnimation.copyFrom(triviaBot.clientData.santaGlideAnimation);
        state.santaIdleAnimation.copyFrom(triviaBot.clientData.santaIdleAnimation);
        state.santaWaveAnimation.copyFrom(triviaBot.clientData.santaWaveAnimation);

        state.santaBot = triviaBot.santaBot();
    }
}

