package bikerboys.wackylife.entity.triviabot;


import bikerboys.wackylife.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.animation.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.*;


public class TriviaBotModel extends EntityModel<TriviaBotRenderState> {


    public static final EntityModelLayer TRIVIA_BOT = new EntityModelLayer(Identifier.of(WackyLife.MOD_ID, "triviabot"), "triviabot");



    private final Animation glideAnimation;
    private final Animation idleAnimation;
    private final Animation walkAnimation;
    private final Animation countdownAnimation;
    private final Animation analyzingAnimation;
    private final Animation answerCorrectAnimation;
    private final Animation answerIncorrectAnimation;
    private final Animation snailTransformAnimation;

    private final Animation santaAnalyzingAnimation;
    private final Animation santaAnswerCorrectAnimation;
    private final Animation santaAnswerIncorrectAnimation;
    private final Animation santaFlyAnimation;
    private final Animation santaGlideAnimation;
    private final Animation santaIdleAnimation;
    private final Animation santaWaveAnimation;


    private final ModelPart triviabot;
    private final ModelPart neckpivot;
    private final ModelPart main;
    private final ModelPart shell;
    private final ModelPart expressions;
    private final ModelPart mouth;
    private final ModelPart dots;
    private final ModelPart green;
    private final ModelPart yellow;
    private final ModelPart red;
    private final ModelPart clock;
    private final ModelPart clockhand;
    private final ModelPart processing;
    private final ModelPart one;
    private final ModelPart two;
    private final ModelPart three;
    private final ModelPart angry;
    private final ModelPart happy;
    private final ModelPart snail;
    private final ModelPart beard;
    private final ModelPart hat;
    private final ModelPart ball;
    private final ModelPart body;
    private final ModelPart righthand;
    private final ModelPart actualhand;
    private final ModelPart microphone;
    private final ModelPart umbrella;
    private final ModelPart top;
    private final ModelPart lefthand;
    private final ModelPart bag;
    private final ModelPart torso;
    private final ModelPart bottom;
    private final ModelPart bottomlarge;
    private final ModelPart legs;

    public TriviaBotModel(ModelPart root) {
        super(root);

        this.triviabot = root.getChild("triviabot");
        this.neckpivot = root.getChild("neckpivot");
        this.main = root.getChild("main");
        this.shell = root.getChild("shell");
        this.expressions = root.getChild("expressions");
        this.mouth = root.getChild("mouth");
        this.dots = root.getChild("dots");
        this.green = root.getChild("green");
        this.yellow = root.getChild("yellow");
        this.red = root.getChild("red");
        this.clock = root.getChild("clock");
        this.clockhand = root.getChild("clockhand");
        this.processing = root.getChild("processing");
        this.one = root.getChild("one");
        this.two = root.getChild("two");
        this.three = root.getChild("three");
        this.angry = root.getChild("angry");
        this.happy = root.getChild("happy");
        this.snail = root.getChild("snail");
        this.beard = root.getChild("beard");
        this.hat = root.getChild("hat");
        this.ball = root.getChild("ball");
        this.body = root.getChild("body");
        this.righthand = root.getChild("righthand");
        this.actualhand = root.getChild("actualhand");
        this.microphone = root.getChild("microphone");
        this.umbrella = root.getChild("umbrella");
        this.top = root.getChild("top");
        this.lefthand = root.getChild("lefthand");
        this.bag = root.getChild("bag");
        this.torso = root.getChild("torso");
        this.bottom = root.getChild("bottom");
        this.bottomlarge = root.getChild("bottomlarge");
        this.legs = root.getChild("legs");


        glideAnimation = TriviaBotAnimations.glide.createAnimation(root);
        idleAnimation = TriviaBotAnimations.idle.createAnimation(root);
        walkAnimation = TriviaBotAnimations.walk.createAnimation(root);
        countdownAnimation = TriviaBotAnimations.countdown.createAnimation(root);
        analyzingAnimation = TriviaBotAnimations.analyzing.createAnimation(root);
        answerCorrectAnimation = TriviaBotAnimations.answer_correct.createAnimation(root);
        answerIncorrectAnimation = TriviaBotAnimations.answer_incorrect.createAnimation(root);
        snailTransformAnimation = TriviaBotAnimations.snail_transform.createAnimation(root);

        santaAnalyzingAnimation = TriviaBotAnimations.santa_analyzing.createAnimation(root);
        santaAnswerCorrectAnimation = TriviaBotAnimations.santa_answer_correct.createAnimation(root);
        santaAnswerIncorrectAnimation = TriviaBotAnimations.santa_answer_incorrect.createAnimation(root);
        santaFlyAnimation = TriviaBotAnimations.santa_fly.createAnimation(root);
        santaGlideAnimation = TriviaBotAnimations.santa_glide.createAnimation(root);
        santaIdleAnimation = TriviaBotAnimations.santa_idle.createAnimation(root);
        santaWaveAnimation = TriviaBotAnimations.santa_wave.createAnimation(root);

    }


    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData triviabot = modelPartData.addChild("triviabot", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.9F, -0.7F));

        ModelPartData neckpivot = triviabot.addChild("neckpivot", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 5.1F, 0.2F));

        ModelPartData main = neckpivot.addChild("main", ModelPartBuilder.create().uv(40, 36).cuboid(-5.0F, -4.5F, -2.6F, 10.0F, 9.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, -5.1F, -0.2F));

        ModelPartData shell = main.addChild("shell", ModelPartBuilder.create().uv(0, 41).cuboid(-6.0F, -10.6F, 4.5F, 12.0F, 11.0F, 0.0F, new Dilation(0.0F))
                .uv(56, 69).cuboid(-6.0F, -0.6F, -3.5F, 12.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(36, 70).cuboid(-6.0F, -10.6F, -3.5F, 12.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 52).cuboid(5.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 64).cuboid(4.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 66).cuboid(4.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(36, 69).cuboid(-5.0F, -9.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(38, 65).cuboid(-5.0F, -1.6F, -3.5F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 70).cuboid(-6.0F, -9.6F, -3.5F, 1.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 25).cuboid(-6.0F, -10.6F, -3.5F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(0, 33).cuboid(-6.02F, 0.4F, -3.48F, 12.0F, 0.0F, 8.0F, new Dilation(0.0F))
                .uv(24, 41).cuboid(-6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new Dilation(0.0F))
                .uv(40, 51).cuboid(6.0F, -10.6F, -3.5F, 0.0F, 11.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 5.1F, 0.2F));

        ModelPartData expressions = main.addChild("expressions", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, -3.005F));

        ModelPartData mouth = expressions.addChild("mouth", ModelPartBuilder.create().uv(28, 67).cuboid(-3.0F, 1.53F, -2.61F, 6.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 3.005F));

        ModelPartData dots = expressions.addChild("dots", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -0.5F, 0.0F));

        ModelPartData green = dots.addChild("green", ModelPartBuilder.create().uv(56, 71).cuboid(-4.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData yellow = dots.addChild("yellow", ModelPartBuilder.create().uv(78, 78).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData red = dots.addChild("red", ModelPartBuilder.create().uv(44, 79).cuboid(2.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData clock = expressions.addChild("clock", ModelPartBuilder.create().uv(48, 79).cuboid(-1.2228F, -1.4755F, 1.7083F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.2228F, -0.0245F, -0.0033F));

        ModelPartData green_r1 = clock.addChild("green_r1", ModelPartBuilder.create().uv(38, 60).cuboid(0.0F, -3.5F, 0.69F, 1.0F, 3.5F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.6228F, -0.5755F, 1.0083F, 0.0F, 0.0F, 0.3927F));

        ModelPartData clockhand = clock.addChild("clockhand", ModelPartBuilder.create().uv(78, 77).cuboid(0.0F, -0.4F, 1.98F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(-0.2228F, -0.4755F, -0.2917F));

        ModelPartData processing = expressions.addChild("processing", ModelPartBuilder.create(), ModelTransform.origin(0.0F, -0.5F, 0.0F));

        ModelPartData one = processing.addChild("one", ModelPartBuilder.create().uv(52, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(-3.0F, 0.0F, 0.0F));

        ModelPartData two = processing.addChild("two", ModelPartBuilder.create().uv(56, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData three = processing.addChild("three", ModelPartBuilder.create().uv(66, 79).cuboid(-1.0F, -1.0F, 1.705F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(3.0F, 0.0F, 0.0F));

        ModelPartData angry = expressions.addChild("angry", ModelPartBuilder.create().uv(56, 51).cuboid(-5.0F, -9.6F, -1.5F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 5.1F, 3.205F));

        ModelPartData happy = expressions.addChild("happy", ModelPartBuilder.create().uv(8, 60).cuboid(-5.0F, -4.5F, 1.4F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.305F));

        ModelPartData snail = expressions.addChild("snail", ModelPartBuilder.create().uv(56, 60).cuboid(-5.0F, -4.5F, -1.3F, 10.0F, 9.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 3.005F));

        ModelPartData beard = main.addChild("beard", ModelPartBuilder.create().uv(52, 82).cuboid(-3.0F, 0.5F, -4.62F, 6.0F, 8.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData hat = main.addChild("hat", ModelPartBuilder.create().uv(60, 84).cuboid(-4.3F, -1.5F, -3.5F, 8.0F, 3.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(3.3F, -6.1F, 3.3F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r1 = hat.addChild("cube_r1", ModelPartBuilder.create().uv(39, 93).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(-0.3F, -3.1F, 1.1F, -0.3927F, 0.0F, 0.0F));

        ModelPartData ball = hat.addChild("ball", ModelPartBuilder.create(), ModelTransform.origin(2.1929F, -3.5F, 4.0071F));

        ModelPartData cube_r2 = ball.addChild("cube_r2", ModelPartBuilder.create().uv(63, 95).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.8F, -0.2F, 2.8F, -0.7854F, 0.7854F, 0.0F));

        ModelPartData body = triviabot.addChild("body", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 23.1F, 0.7F));

        ModelPartData righthand = body.addChild("righthand", ModelPartBuilder.create(), ModelTransform.origin(-7.7F, -16.9F, 0.0F));

        ModelPartData actualhand = righthand.addChild("actualhand", ModelPartBuilder.create().uv(22, 69).cuboid(-1.5F, -2.0F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData microphone = righthand.addChild("microphone", ModelPartBuilder.create().uv(36, 73).cuboid(-1.05F, -0.25F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(8, 52).cuboid(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.5953F, 6.4269F, -5.5088F, 1.309F, 0.1309F, 0.0F));

        ModelPartData umbrella = righthand.addChild("umbrella", ModelPartBuilder.create().uv(0, 52).cuboid(-1.05F, -18.25F, -1.0F, 2.0F, 23.0F, 2.0F, new Dilation(0.0F))
                .uv(60, 70).cuboid(-1.95F, -4.25F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(2.0953F, 5.2269F, -3.9088F, 1.6581F, 0.2182F, -0.1309F));

        ModelPartData top = umbrella.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-11.3333F, -1.45F, -12.3333F, 24.0F, 1.0F, 24.0F, new Dilation(0.0F))
                .uv(76, 61).cuboid(12.6667F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 74).cuboid(9.6667F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 75).cuboid(-2.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(78, 76).cuboid(-2.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(66, 78).cuboid(-11.3333F, -0.45F, -12.3333F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(60, 78).cuboid(12.6667F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 70).cuboid(9.6667F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 65).cuboid(-11.3333F, -0.45F, 8.6667F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(0, 77).cuboid(-11.3333F, -0.45F, -0.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 56).cuboid(12.6667F, -0.45F, -3.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 52).cuboid(-11.3333F, -0.45F, -12.3333F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F))
                .uv(78, 60).cuboid(-11.3333F, -0.45F, 11.6667F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-0.6667F, -17.35F, 0.3333F, 0.0F, -0.7854F, 0.0F));

        ModelPartData lefthand = body.addChild("lefthand", ModelPartBuilder.create().uv(8, 69).cuboid(-1.9F, -1.5F, -2.0F, 3.0F, 10.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(8.1F, -17.4F, 0.0F));

        ModelPartData bag = lefthand.addChild("bag", ModelPartBuilder.create().uv(12, 83).cuboid(-0.3474F, -3.2093F, -7.6446F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                .uv(36, 84).cuboid(1.6526F, -6.2093F, -5.6446F, 4.0F, 3.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(-0.25F, 8.05F, 2.45F, 1.3809F, 0.0242F, 1.0667F));

        ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(40, 25).cuboid(-5.998F, -5.3F, -3.002F, 12.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(-0.002F, -12.1F, 0.002F));

        ModelPartData bottom = torso.addChild("bottom", ModelPartBuilder.create().uv(36, 71).cuboid(-4.998F, -0.3F, -2.502F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 36).cuboid(-4.998F, -0.3F, 2.498F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(28, 60).cuboid(-4.998F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F))
                .uv(72, 42).cuboid(5.002F, -0.3F, -2.502F, 0.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData bottomlarge = torso.addChild("bottomlarge", ModelPartBuilder.create().uv(0, 83).cuboid(-4.998F, -0.3F, -3.002F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 85).cuboid(-4.998F, -0.3F, 2.998F, 10.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 81).cuboid(-4.998F, -0.3F, -3.002F, 0.0F, 2.0F, 6.0F, new Dilation(0.0F))
                .uv(0, 83).cuboid(5.002F, -0.3F, -3.002F, 0.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData legs = body.addChild("legs", ModelPartBuilder.create().uv(72, 38).cuboid(-3.9975F, 0.6F, -2.0025F, 8.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 40).cuboid(-3.9975F, 0.6F, 1.9975F, 8.0F, 2.0F, 0.0F, new Dilation(0.0F))
                .uv(44, 73).cuboid(-3.9975F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(52, 73).cuboid(4.0025F, 0.6F, -2.0025F, 0.0F, 2.0F, 4.0F, new Dilation(0.0F))
                .uv(72, 49).cuboid(-3.9975F, 3.6F, -2.0025F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 50).cuboid(-3.9975F, 3.6F, 1.9975F, 8.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 25).cuboid(-3.9975F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(76, 30).cuboid(4.0025F, 3.6F, -2.0025F, 0.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(78, 71).cuboid(4.0025F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(76, 35).cuboid(-2.9975F, 5.6F, -2.0025F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(76, 51).cuboid(-2.9975F, 5.6F, 1.9975F, 6.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(72, 78).cuboid(-3.9975F, 5.6F, -1.0025F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(-0.0025F, -10.5F, 0.0025F));
        return TexturedModelData.of(modelData, 128, 128);
    }





    /*
    @Override
    public void renderToBuffer(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        triviabot.render(matrices, vertexConsumer, light, overlay, color);
    }



    @Override
    public ModelPart root() {
        return triviabot;
    }

     */




    @Override
    public void setAngles(TriviaBotRenderState state) {
        super.setAngles(state);

        this.glideAnimation.apply(state.glideAnimationState, state.age);
        this.idleAnimation.apply(state.idleAnimationState, state.age);
        this.walkAnimation.apply(state.walkAnimationState, state.age);
        this.countdownAnimation.apply(state.countdownAnimationState, state.age);
        this.analyzingAnimation.apply(state.analyzingAnimationState, state.age);
        this.answerCorrectAnimation.apply(state.answerCorrectAnimationState, state.age);
        this.answerIncorrectAnimation.apply(state.answerIncorrectAnimationState, state.age);
        this.snailTransformAnimation.apply(state.snailTransformAnimationState, state.age);

        this.santaAnalyzingAnimation.apply(state.santaAnalyzingAnimation, state.age);
        this.santaAnswerCorrectAnimation.apply(state.santaAnswerCorrectAnimation, state.age);
        this.santaAnswerIncorrectAnimation.apply(state.santaAnswerIncorrectAnimation, state.age);
        this.santaFlyAnimation.apply(state.santaFlyAnimation, state.age);
        this.santaGlideAnimation.apply(state.santaGlideAnimation, state.age);
        this.santaIdleAnimation.apply(state.santaIdleAnimation, state.age);
        this.santaWaveAnimation.apply(state.santaWaveAnimation, state.age);

    }

}