package bikerboys.wackylife.entity.snail.goal;


import bikerboys.wackylife.entity.snail.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;

public final class SnailLandGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int noTargetTicks;

    public SnailLandGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (!mob.isSnailFlying() || mob.isSnailGliding()) {
            return false;
        }

        Vec3d targetPos = mob.serverData.getPlayerPos();
        if (targetPos == null) {
            noTargetTicks++;
        } else {
            noTargetTicks = 0;
        }

        if (noTargetTicks >= 40) {
            return true;
        }

        if (targetPos == null) {
            return false;
        }

        boolean isMobAboveTarget = mob.getY() - targetPos.y > 0.0D;

        if (!isMobAboveTarget) {
            return false;
        }

        if (!mob.pathfinding.isValidGroundPosition(mob.pathfinding.getGroundBlock())) {
            return false;
        }

        return mob.pathfinding.canPathToPlayerFromGround(false);
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.pathfinding.isValidGroundPosition(mob.pathfinding.getGroundBlock())) {
            return false;
        }
        return mob.pathfinding.getDistanceToGroundBlock() > 1.5D;
    }

    @Override
    public void tick() {
        land();
    }

    @Override
    public void start() {
        mob.setSnailLanding(true);
        mob.setSnailFlying(false);
        mob.setSnailGliding(false);
    }

    @Override
    public void stop() {
        mob.setSnailLanding(false);
        mob.setSnailFlying(false);
        mob.setSnailGliding(false);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }

    private void land() {
        mob.setVelocity(0.0D, -0.15D, 0.0D);
    }
}
