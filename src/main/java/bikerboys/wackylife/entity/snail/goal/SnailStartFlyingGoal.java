package bikerboys.wackylife.entity.snail.goal;


import bikerboys.wackylife.entity.snail.*;
import net.minecraft.entity.ai.goal.*;
import org.jetbrains.annotations.NotNull;

public final class SnailStartFlyingGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int startFlyingCounter;
    private final int startFlyingDelay = 70;

    public SnailStartFlyingGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (mob.isPaused()) return false;
        if (!mob.serverData.shouldPathfind()) {
            return false;
        }

        if (mob.isSnailFlying()) {
            return false;
        }

        if (mob.getNavigation().getCurrentPath() == null) {
            startFlyingCounter = 0;
            return false;
        }

        // Use cached pathfinding results
        boolean canWalk = mob.pathfinding.canPathToPlayer(false);
        boolean canFly = mob.pathfinding.canPathToPlayer(true);

        if (canWalk) {
            startFlyingCounter = 0;
            return false;
        }
        else if (canFly) {
            startFlyingCounter++;
        }
        else {
            startFlyingCounter = 0;
            return false;
        }

        return startFlyingCounter >= startFlyingDelay;
    }

    @Override
    public void start() {
        mob.setSnailFlying(true);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }

    @Override
    public void stop() {
        startFlyingCounter = 0;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }
}
