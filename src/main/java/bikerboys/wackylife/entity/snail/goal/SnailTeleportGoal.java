package bikerboys.wackylife.entity.snail.goal;

import bikerboys.wackylife.entity.snail.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("resource")
public final class SnailTeleportGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int ticksSinceLastPositionChange;
    private final int maxTicksSinceLastPositionChange;
    private int teleportCooldown = 0;
    @NotNull
    private BlockPos lastPosition;

    private int lowSpeedTicks = 0;
    private static final double MIN_SPEED_THRESHOLD = 0.01;

    public SnailTeleportGoal(@NotNull Snail mob) {
        this.mob = mob;
        this.maxTicksSinceLastPositionChange = Snail.STATIONARY_TP_COOLDOWN;
        this.lastPosition = mob.getBlockPos();
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (mob.isPaused()) return false;

        if (teleportCooldown > 0) {
            teleportCooldown--;
            return false;
        }

        if (!mob.serverData.shouldPathfind()) {
            return false;
        }

        Entity boundEntity = mob.serverData.getBoundEntity();
        if (boundEntity == null) return false;

        boolean dimensionsAreSame = mob.getEntityWorld().getDimension().equals(boundEntity.getEntityWorld().getDimension());
        if (!dimensionsAreSame) {
            return true;
        }

        float distFromPlayer = mob.distanceTo(boundEntity);
        if (distFromPlayer > Snail.MAX_DISTANCE) {
            return true;
        }

        BlockPos currentPos = mob.getBlockPos();
        if (!currentPos.equals(this.lastPosition)) {
            this.ticksSinceLastPositionChange = 0;
            this.lastPosition = currentPos;
            this.lowSpeedTicks = 0;
        } else {
            this.ticksSinceLastPositionChange++;
        }

        double currentSpeed = mob.getVelocity().horizontalLength();
        if (currentSpeed < (MIN_SPEED_THRESHOLD*Snail.GLOBAL_SPEED_MULTIPLIER) && mob.getNavigation().getCurrentPath() != null) {
            lowSpeedTicks++;
        } else {
            lowSpeedTicks = 0;
        }

        boolean stuckByPosition = this.ticksSinceLastPositionChange > this.maxTicksSinceLastPositionChange;
        boolean stuckBySpeed = this.lowSpeedTicks > (this.maxTicksSinceLastPositionChange / 2);

        return stuckByPosition || stuckBySpeed;
    }

    @Override
    public void start() {
        teleportCooldown = 20;

        mob.pathfinding.fakeTeleportNearPlayer(Snail.TP_MIN_RANGE);

        this.ticksSinceLastPositionChange = 0;
        this.lowSpeedTicks = 0;
        this.lastPosition = mob.getBlockPos();
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}
