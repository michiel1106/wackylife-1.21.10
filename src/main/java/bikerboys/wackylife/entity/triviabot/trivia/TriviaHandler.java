package bikerboys.wackylife.entity.triviabot.trivia;

import bikerboys.wackylife.attachements.*;
import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.impl.discovery.*;
import net.jpountz.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class TriviaHandler extends AbstractTrivia{
    public TriviaHandler(TriviaBot bot) {
        super(bot);
        initializeItemSpawner();
    }
    public static ItemSpawner itemSpawner;

    public int snailTransformation = 0;

    public static int EASY_TIME = 180;
    public static int NORMAL_TIME = 240;
    public static int HARD_TIME = 300;

    public void tick() {
        super.tick();
        if (bot.ranOutOfTime()) {
            snailTransformation++;
        }

        if (bot.age % 2 == 0 && bot.submittedAnswer()) {
            bot.setAnalyzingTime(bot.getAnalyzingTime()-1);
        }

        if (bot.submittedAnswer()) {
            if (bot.answeredRight()) {
                if (bot.getAnalyzingTime() < -80) {
                    bot.setLeaving(true);
                    if (bot.hasVehicle()) bot.dismountVehicle();
                    bot.noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+80) / (20.0f)));
                    bot.setVelocity(0,velocity,0);
                    if (bot.getAnalyzingTime() < -200) bot.serverData.despawn();
                }
            }
            else {
                if (bot.getAnalyzingTime() < -100) {
                    if (bot.hasVehicle()) bot.dismountVehicle();
                    bot.noClip = true;
                    float velocity = Math.min(0.5f, 0.25f * Math.abs((bot.getAnalyzingTime()+100) / (20.0f)));
                    bot.setVelocity(0,velocity,0);
                    if (bot.getAnalyzingTime() < -200) bot.serverData.despawn();
                }
            }
        }
        else {
            bot.serverData.handleHighVelocity();
            if (bot.interactedWith() && getRemainingTicks() <= 0) {
                if (!bot.ranOutOfTime()) {
                    ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
                    if (boundPlayer != null) {
                        // cancel trivia here
                    }
                }
                bot.setRanOutOfTime(true);
            }
            if (snailTransformation > 66) {
                failSnail();
            }
        }
    }

    private void failSnail() {

    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (bot.getEntityWorld().isClient()) return ActionResult.SUCCESS;
        ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
        if (boundPlayer == null) return ActionResult.PASS;
        if (boundPlayer.getUuid() != player.getUuid()) return ActionResult.PASS;
        if (bot.submittedAnswer()) return ActionResult.PASS;
        if (bot.interactedWith() && getRemainingTicks() <= 0) return ActionResult.PASS;

        startTrivia(boundPlayer);

        return ActionResult.SUCCESS;
    }
    public Pair<Integer, Question> generateTrivia(ServerPlayerEntity boundPlayer) {
        return new Pair<>(2, bot.question);
        //return TriviaWildcard.getTriviaQuestion(boundPlayer);
    }

    public void setTimeBasedOnDifficulty(int difficulty) {
        timeToComplete = difficulty * 60 + 120;
        if (difficulty == 1) timeToComplete = EASY_TIME;
        if (difficulty == 2) timeToComplete = NORMAL_TIME;
        if (difficulty == 3) timeToComplete = HARD_TIME;
    }


    public boolean handleAnswer(int answer) {
        if (super.handleAnswer(answer)) {
            bot.setAnalyzingTime(42);
            PlayerUtils.playSoundWithSourceToPlayers(
                    bot.getEntityWorld().getServer().getPlayerManager().getPlayerList(), bot,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_analyzing")),
                    SoundCategory.NEUTRAL, 1f, 1);
            return true;
        }
        return false;
    }

    public void answeredCorrect() {
        super.answeredCorrect();
        TaskScheduler.scheduleTask(213, this::blessPlayer);
        TaskScheduler.scheduleTask(72, () -> {
            PlayerUtils.playSoundWithSourceToPlayers(
                    bot.getEntityWorld().getServer().getPlayerManager().getPlayerList(), bot,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_correct")),
                    SoundCategory.NEUTRAL, 1f, 1);
        });
    }

    private void blessPlayer() {
        ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
        Vec3d pos = bot.getEntityPos().add(0,1,0);
        Vec3d vector = Vec3d.ZERO;

        if (boundPlayer != null) {
            ChoiceAttachment choice = ModAttachments.getChoice(boundPlayer);
            if (choice != null) {
                Choice positiveChoice = ChoiceRegistry.get(ChoiceRegistry.getRandomChoicePairs().getFirst().positiveChoice());
                ModAttachments.setChoice(boundPlayer, positiveChoice, ChoiceRegistry.get("empty_neg"));
            }
            ItemStack randomItem = itemSpawner.getRandomItem();
            OtherUtils.spawnItemForPlayerWithVelocity((ServerWorld) bot.getEntityWorld(), pos, randomItem, bot.serverData.getBoundPlayer(), vector);


        }
    }

    public static void initializeItemSpawner() {
        itemSpawner = new ItemSpawner();
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_APPLE, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.ENDER_PEARL, 2), 20);
        itemSpawner.addItem(new ItemStack(Items.TRIDENT), 10);
        itemSpawner.addItem(new ItemStack(Items.POWERED_RAIL, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.DIAMOND, 4), 20);
        itemSpawner.addItem(new ItemStack(Items.CREEPER_SPAWN_EGG), 10);
        itemSpawner.addItem(new ItemStack(Items.GOLDEN_CARROT, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.WIND_CHARGE, 16), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SHRIEKER, 2), 10);
        itemSpawner.addItem(new ItemStack(Items.SCULK_SENSOR, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.TNT, 8), 20);
        itemSpawner.addItem(new ItemStack(Items.COBWEB, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.OBSIDIAN, 8), 10);
        itemSpawner.addItem(new ItemStack(Items.PUFFERFISH_BUCKET), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_CHESTPLATE), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_LEGGINGS), 10);
        itemSpawner.addItem(new ItemStack(Items.NETHERITE_BOOTS), 10);
        itemSpawner.addItem(new ItemStack(Items.ARROW, 64), 10);
        itemSpawner.addItem(new ItemStack(Items.IRON_BLOCK, 2), 10);

        ItemStack mace = new ItemStack(Items.MACE);
        mace.setDamage(mace.getMaxDamage()-1);
        itemSpawner.addItem(mace, 5);

        ItemStack endCrystal = new ItemStack(Items.END_CRYSTAL);
        itemSpawner.addItem(endCrystal, 10);

    }

    public void answeredIncorrect() {
        super.answeredIncorrect();
        TaskScheduler.scheduleTask(210, this::cursePlayer);
        TaskScheduler.scheduleTask(72, () -> {
            PlayerUtils.playSoundWithSourceToPlayers(
                    bot.getEntityWorld().getServer().getPlayerManager().getPlayerList(), bot,
                    SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_incorrect")),
                    SoundCategory.NEUTRAL, 1f, 1);
        });
    }

    private void cursePlayer() {
        ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();

        if (boundPlayer != null) {
            ChoiceAttachment choice = ModAttachments.getChoice(boundPlayer);
            if (choice != null) {
                Choice negativeChoice = ChoiceRegistry.get(ChoiceRegistry.getRandomChoicePairs().getFirst().negativeChoice());
                ModAttachments.setChoice(boundPlayer, ChoiceRegistry.get("empty_pos"), negativeChoice);
            }
        }
    }


}