package bikerboys.wackylife.entity.triviabot.goal;

import bikerboys.wackylife.entity.triviabot.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;



@SuppressWarnings("resource")
public final class TriviaBotTeleportGoal extends Goal {


    @NotNull
    private final TriviaBot mob;
    private int teleportCooldown = 0;
    @NotNull
    private BlockPos lastPosition;
    private int ticksSinceLastPositionChange;
    private final int maxTicksSinceLastPositionChange;

    public TriviaBotTeleportGoal(@NotNull TriviaBot mob) {
        this.mob = mob;
        this.maxTicksSinceLastPositionChange = TriviaBot.STATIONARY_TP_COOLDOWN;
        this.lastPosition = BlockPos.ORIGIN;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (mob.interactedWith() || mob.pathfinding.noPathfinding || mob.santaBot()) {
            return false;
        }
        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }

        if (!mob.serverData.shouldPathfind()) {
            return false;
        }

        LivingEntity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) return false;

        float distFromPlayer = mob.distanceTo(boundEntity);
        if (distFromPlayer > TriviaBot.MAX_DISTANCE) return true;


        if (!mob.getBlockPos().equals(this.lastPosition) || distFromPlayer < 4) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = mob.getBlockPos();
        }

        this.ticksSinceLastPositionChange++;
        if (this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange) return true;


        boolean dimensionsAreSame = mob.getEntityWorld().getDimension().equals(boundEntity.getEntityWorld().getDimension());
        return !dimensionsAreSame;
    }

    @Override
    public void start() {
        teleportCooldown = 20;
        mob.pathfinding.fakeTeleportToPlayer();
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

}
