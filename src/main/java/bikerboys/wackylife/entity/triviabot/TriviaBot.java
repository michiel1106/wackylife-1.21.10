package bikerboys.wackylife.entity.triviabot;

import bikerboys.wackylife.*;
import bikerboys.wackylife.entity.triviabot.goal.*;
import bikerboys.wackylife.entity.triviabot.server.*;
import bikerboys.wackylife.entity.triviabot.trivia.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.data.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.*;
import net.minecraft.registry.tag.*;
import net.minecraft.sound.*;
import net.minecraft.storage.*;
import net.minecraft.util.*;
import net.minecraft.util.dynamic.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

import java.util.*;

public class TriviaBot extends AmbientEntity {

    public static final Identifier DEFAULT_TEXTURE = Identifier.of(WackyLife.MOD_ID, "textures/entity/triviabot/triviabot.png");
    public static final Identifier SANTABOT_TEXTURE = Identifier.of(WackyLife.MOD_ID, "textures/entity/triviabot/santabot.png");
    public static final Identifier ID = Identifier.of(WackyLife.MOD_ID,"triviabot");
    public static final TrackedDataHandler<Question> BOOLEAN = TrackedDataHandler.create(Question.CODEC);

    public static final int STATIONARY_TP_COOLDOWN = 400; // No movement for 20 seconds teleports the bot
    public static final float MOVEMENT_SPEED = 0.45f;
    public static final int MAX_DISTANCE = 100;
    public static boolean CAN_START_RIDING = true;
    public Question question = Question.DEFAULT;

    public TriviaHandler triviaHandler = new TriviaHandler(this);
    public TriviaBotClientData clientData = new TriviaBotClientData(this);
    public TriviaBotServerData serverData = new TriviaBotServerData(this);
    public TriviaBotSounds sounds = new TriviaBotSounds(this);
    public TriviaBotPathfinding pathfinding = new TriviaBotPathfinding(this);
    private boolean isSantaBot = false;

    private static final TrackedData<Boolean> submittedAnswer = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ranOutOfTime = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> answeredRight = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> interactedWith = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> gliding = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> analyzing = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> santaBot = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> waving = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> leaving = DataTracker.registerData(TriviaBot.class, TrackedDataHandlerRegistry.BOOLEAN);

    public TriviaBot(EntityType<? extends AmbientEntity> entityType, World level) {
        super(entityType, level);
        setInvulnerable(true);
        setPersistent();
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 10000)
                .add(EntityAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.FLYING_SPEED, MOVEMENT_SPEED)
                .add(EntityAttributes.STEP_HEIGHT, 1)
                .add(EntityAttributes.SAFE_FALL_DISTANCE, 100)
                .add(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.FOLLOW_RANGE, 100)
                .add(EntityAttributes.ATTACK_DAMAGE, 0);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        return triviaHandler.onInteract(player, hand);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new TriviaBotTeleportGoal(this));
        goalSelector.add(1, new TriviaBotGlideGoal(this));
        goalSelector.add(2, new TriviaBotLookAtPlayerGoal(this));
    }



    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);

        if (this.question == null) return;

        view.putInt("index", this.question.rightQuestionIndex());
        view.putString("question", this.question.question());

        if (!this.question.answers().isEmpty()) {
            WriteView.ListAppender<String> list = view.getListAppender("answers", Codecs.NON_EMPTY_STRING);

            for (String answer : this.question.answers()) {
                list.add(answer);
            }
        }
    }


    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);

        int index = view.getInt("index", 1);
        String questionText = view.getString("question", "whoopssomethingwentwrong");

        List<String> answers = new ArrayList<>();

        ReadView.TypedListReadView<String> list = view.getTypedListView("answers", Codecs.NON_EMPTY_STRING);

        if (list != null) {
            // TypedListReadView implies Iterable<T>, so 'element' is directly a String
            for (String element : list) {
                answers.add(element);
            }
        }

        this.question = new Question(questionText, answers, index);
    }

    @Override
    public void tick() {
        super.tick();
        if (!getEntityWorld().isClient()) {
            serverData.tick(this.getEntityWorld().getServer());
        }
        clientData.tick();
        triviaHandler.tick();
    }

    /*
        Override vanilla things
     */

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) {
        return motion;
    }

    public boolean isAffectedByFluids() {
        return false;
    }

    @Override
    public boolean isSubmergedInWater() {
        return false;
    }



    @Override
    public void setSwimming(boolean swimming) {
        this.setFlag(4, false);
    }

    @Override
    public boolean updateMovementInFluid(TagKey<Fluid> tag, double speed) {
        return false;
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
        if (this.noClip) return false;
        return CAN_START_RIDING;
    }


    @Override
    public void slowMovement(BlockState state, Vec3d multiplier) {
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    public boolean isImmuneToExplosion(Explosion explosion) {
        return true;
    }

    public float soundVolume() {
        return getSoundVolume();
    }

    public void setNavigation(EntityNavigation newNavigation) {
        this.navigation = newNavigation;
    }
    public void setMoveControl(MoveControl newMoveControl) {
        this.moveControl = newMoveControl;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ranOutOfTime, false);
        builder.add(submittedAnswer, false);
        builder.add(answeredRight, false);
        builder.add(interactedWith, false);
        builder.add(gliding, false);
        builder.add(analyzing, -1);
        builder.add(santaBot, isSantaBot);
    }

    public void setRanOutOfTime(boolean value) {
        this.dataTracker.set(ranOutOfTime, value);
    }
    public void setSubmittedAnswer(boolean value) {
        this.dataTracker.set(submittedAnswer, value);
    }
    public void setAnsweredRight(boolean value) {
        this.dataTracker.set(answeredRight, value);
    }
    public void setInteractedWith(boolean value) {
        this.dataTracker.set(interactedWith, value);
    }
    public void setGliding(boolean value) {
        this.dataTracker.set(gliding, value);
    }
    public void setAnalyzingTime(int value) {
        this.dataTracker.set(analyzing, value);
    }
    public void setSantaBot(boolean value) {
        this.dataTracker.set(santaBot, value);
    }

    public void setWaving(int value) {
        this.dataTracker.set(waving, value);
    }
    public void setLeaving(boolean value) {
        this.dataTracker.set(leaving, value);
    }


    public boolean canSumbitAnswer() {
        return !ranOutOfTime() && !submittedAnswer();
    }

    public boolean ranOutOfTime() {
        return this.dataTracker.get(ranOutOfTime);
    }
    public boolean submittedAnswer() {
        return this.dataTracker.get(submittedAnswer);
    }
    public boolean answeredRight() {
        return this.dataTracker.get(answeredRight);
    }
    public boolean interactedWith() {
        return this.dataTracker.get(interactedWith);
    }
    public boolean isBotGliding() {
        return this.dataTracker.get(gliding);
    }
    public int getAnalyzingTime() {
        return this.dataTracker.get(analyzing);
    }
    public boolean santaBot() {
        return this.dataTracker.get(santaBot);
    }

    public int waving() {
        return this.dataTracker.get(waving);
    }
    public boolean leaving() {
        return this.dataTracker.get(leaving);
    }

    public int getTickCount() {
        return age;
    }
}
