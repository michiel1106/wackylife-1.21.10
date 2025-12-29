package bikerboys.wackylife.entity.triviabot.server;

import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.entity.triviabot.trivia.*;
import bikerboys.wackylife.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

public class TriviaBotPathfinding {
    private TriviaBot bot;
    public TriviaBotPathfinding(TriviaBot bot) {
        this.bot = bot;
    }
    public boolean navigationInit = false;
    public boolean noPathfinding = false;

    public void tick() {
        if (bot.age % 5 == 0) {
            updateNavigationTarget();
        }
        if (bot.age % 100 == 0 || !navigationInit) {
            navigationInit = true;
            updateNavigation();
        }
    }

    public void fakeTeleportToPlayer() {
        if (bot.getEntityWorld().isClient()) return;
        ServerPlayerEntity boundPlayer = bot.serverData.getBoundPlayer();
        Entity boundEntity = bot.serverData.getBoundEntity();
        Question question = bot.question;
        if (boundEntity == null) return;
        if (bot.getEntityWorld() instanceof ServerWorld level) {
            if (boundEntity.getEntityWorld() instanceof ServerWorld entityWorld) {
                BlockPos tpTo = getBlockPosNearTarget(boundEntity,5);

                level.playSound(null, bot.getX(), bot.getY(), bot.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, bot.getSoundCategory(), bot.soundVolume(), bot.getSoundPitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, bot.getSoundCategory(), bot.soundVolume(), bot.getSoundPitch());

                AnimationUtils.spawnTeleportParticles(level, bot.getEntityPos());
                AnimationUtils.spawnTeleportParticles(level, tpTo.toCenterPos());
                bot.serverData.despawn();

                TriviaWildcard.spawnBotFor(boundPlayer, tpTo, question);
            }
        }
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, BlockPos targetPos, double distanceFromTarget) {
        if (target == null) return targetPos;
        return LevelUtils.getCloseBlockPos(target.getEntityWorld(), targetPos, distanceFromTarget, 2, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3d targetPos = bot.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.ofFloored(targetPos.x, targetPos.y, targetPos.z);
        return LevelUtils.getCloseBlockPos(target.getEntityWorld(), targetBlockPos, distanceFromTarget, 2, false);
    }


    public void updateNavigation() {
        bot.setMoveControl(new MoveControl(bot));
        bot.setNavigation(new MobNavigation(bot, bot.getEntityWorld()));
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        Vec3d targetPos = bot.serverData.getPlayerPos();
        if (noPathfinding || bot.interactedWith() ||!bot.serverData.shouldPathfind() || targetPos == null ||
                bot.squaredDistanceTo(targetPos) > (TriviaBot.MAX_DISTANCE*TriviaBot.MAX_DISTANCE)) {
            bot.getNavigation().stop();
            return;
        }

        bot.getNavigation().setSpeed(TriviaBot.MOVEMENT_SPEED);
        Path path = bot.getNavigation().findPathTo(targetPos.x, targetPos.y, targetPos.z, 3);
        if (path != null) bot.getNavigation().startMovingAlong(path, TriviaBot.MOVEMENT_SPEED);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = bot.getEntityPos();

        int minY = bot.getEntityWorld().getBottomY();

        Vec3d endPos = startPos.add(0, minY, 0);

        BlockHitResult result = bot.getEntityWorld().raycast(
                new RaycastContext(
                        startPos,
                        endPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        bot
                )
        );

        if (result.getType() == HitResult.Type.MISS) return null;
        return result.getBlockPos();
    }

    public double getDistanceToGroundBlock() {
        BlockPos belowBlock = getGroundBlock();
        if (belowBlock == null) return Double.NEGATIVE_INFINITY;
        return bot.getY() - belowBlock.getY() - 1;
    }
}