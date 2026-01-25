package bikerboys.wackylife.entity.snail;


import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.*;
import net.minecraft.client.render.entity.model.*;

public class SnailModel extends EntityModel<SnailRenderState> {



    private final Animation flyAnimation;
    private final Animation glideAnimation;
    private final Animation walkAnimation;
    private final Animation idleAnimation;
    private final Animation startFlyAnimation;
    private final Animation stopFlyAnimation;


    public static final EntityModelLayer SNAIL = new EntityModelLayer(Snail.ID, "main");

    private final ModelPart main;
    private final ModelPart head;
    private final ModelPart trivia;
    private final ModelPart body;
    private final ModelPart shell;
    private final ModelPart back;
    private final ModelPart mid;
    private final ModelPart midfront;
    private final ModelPart midback;
    private final ModelPart propeller;
    private final ModelPart top;
    private final ModelPart parachute;
    private final ModelPart strings;
    public SnailModel(ModelPart root) {

        super(root);

        this.main = root.getChild("main");
        this.head = this.main.getChild("head");
        this.trivia = this.head.getChild("trivia");
        this.body = this.main.getChild("body");
        this.shell = this.body.getChild("shell");
        this.back = this.body.getChild("back");
        this.mid = this.body.getChild("mid");
        this.midfront = this.mid.getChild("midfront");
        this.midback = this.mid.getChild("midback");
        this.propeller = this.body.getChild("propeller");
        this.top = this.propeller.getChild("top");
        this.parachute = this.body.getChild("parachute");
        this.strings = this.parachute.getChild("strings");


        flyAnimation = SnailAnimations.fly.createAnimation(root);
        glideAnimation = SnailAnimations.glide.createAnimation(root);
        walkAnimation = SnailAnimations.walk.createAnimation(root);
        idleAnimation = SnailAnimations.idle.createAnimation(root);
        startFlyAnimation = SnailAnimations.startFly.createAnimation(root);
        stopFlyAnimation = SnailAnimations.stopFly.createAnimation(root);

    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData main = modelPartData.addChild("main", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 24.0F, 0.0F));

        ModelPartData head = main.addChild("head", ModelPartBuilder.create().uv(28, 57).cuboid(-2.0F, -8.0F, 4.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
                .uv(48, 53).cuboid(-2.0F, -5.0F, 3.0F, 4.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 57).cuboid(-2.0F, -2.0F, 1.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(12, 58).cuboid(1.0F, -8.0F, 4.0F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -3.0F, 0.0F, 3.1416F, 0.0F));

        ModelPartData trivia = head.addChild("trivia", ModelPartBuilder.create().uv(0, 51).cuboid(-3.0F, -8.0F, 3.0F, 6.0F, 5.0F, 2.0F, new Dilation(0.01F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = trivia.addChild("cube_r1", ModelPartBuilder.create().uv(34, 58).cuboid(-1.0F, -6.0F, -0.025F, 1.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 4.025F, 0.0F, 0.0F, -0.3927F));

        ModelPartData cube_r2 = trivia.addChild("cube_r2", ModelPartBuilder.create().uv(32, 58).cuboid(0.0F, -6.0F, -0.025F, 1.0F, 6.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -8.0F, 4.025F, 0.0F, 0.0F, 0.3927F));

        ModelPartData body = main.addChild("body", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData shell = body.addChild("shell", ModelPartBuilder.create().uv(34, 17).cuboid(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData back = body.addChild("back", ModelPartBuilder.create().uv(0, 58).cuboid(-2.0F, -2.0F, 4.0F, 4.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData mid = body.addChild("mid", ModelPartBuilder.create(), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData midfront = mid.addChild("midfront", ModelPartBuilder.create().uv(34, 37).cuboid(-1.99F, -1.99F, -4.5F, 3.98F, 1.98F, 8.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData midback = mid.addChild("midback", ModelPartBuilder.create().uv(16, 51).cuboid(-2.0F, -2.0F, 0.0F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData propeller = body.addChild("propeller", ModelPartBuilder.create().uv(32, 53).cuboid(-2.0F, -10.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
                .uv(36, 58).cuboid(-0.5F, -12.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData top = propeller.addChild("top", ModelPartBuilder.create().uv(34, 47).cuboid(-3.0F, -12.01F, -3.0F, 6.0F, 0.0F, 6.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));

        ModelPartData parachute = body.addChild("parachute", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -13.6F, -4.0F, 16.0F, 0.6F, 16.0F, new Dilation(0.0F))
                .uv(34, 33).cuboid(-7.99F, -13.0F, -3.99F, 15.98F, 0.5F, 0.5F, new Dilation(0.0F))
                .uv(34, 35).cuboid(-7.99F, -13.0F, 11.49F, 15.98F, 0.5F, 0.5F, new Dilation(0.0F))
                .uv(0, 17).cuboid(7.5F, -13.0F, -4.0F, 0.5F, 0.5F, 16.0F, new Dilation(0.0F))
                .uv(0, 34).cuboid(-8.0F, -13.0F, -4.0F, 0.5F, 0.5F, 16.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, -4.0F));

        ModelPartData strings = parachute.addChild("strings", ModelPartBuilder.create().uv(44, 58).cuboid(1.0F, -13.0F, 7.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(58, 37).cuboid(3.0F, -13.0F, 5.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(40, 58).cuboid(3.0F, -13.0F, 2.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(58, 41).cuboid(-3.0F, -13.0F, 5.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(42, 58).cuboid(-3.0F, -13.0F, 2.0F, 0.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(46, 58).cuboid(-2.0F, -13.0F, 7.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(60, 37).cuboid(1.0F, -13.0F, 1.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(58, 47).cuboid(-2.0F, -13.0F, 1.0F, 1.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.origin(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(SnailRenderState state) {
        super.setAngles(state);


        this.flyAnimation.apply(state.flyAnimationState, state.age);
        this.glideAnimation.apply(state.glideAnimationState, state.age);
        this.walkAnimation.apply(state.walkAnimationState, state.age);
        this.idleAnimation.apply(state.idleAnimationState, state.age);
        this.startFlyAnimation.apply(state.startFlyAnimationState, state.age);
        this.stopFlyAnimation.apply(state.stopFlyAnimationState, state.age);


        boolean parachuteHidden = !state.glideAnimationState.isRunning();
        boolean propellerHidden = !state.flyAnimationState.isRunning() && !state.startFlyAnimationState.isRunning();
        boolean triviaHidden = !state.fromTrivia;

        this.parachute.traverse().forEach(part -> part.hidden = parachuteHidden);
        this.propeller.traverse().forEach(part -> part.hidden = propellerHidden);
        //this.trivia.traverse().forEach(part -> part.hidden = triviaHidden);
    }

}