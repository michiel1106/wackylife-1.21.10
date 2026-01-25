package bikerboys.wackylife.entity.snail.goal;


import bikerboys.wackylife.entity.snail.*;
import net.minecraft.entity.ai.goal.*;
import org.jetbrains.annotations.*;

public class SnailFlyGoal extends Goal {

    @NotNull
    private final Snail mob;

    public SnailFlyGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @NotNull
    protected Snail getMob() {
        return this.mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (mob.isPaused()) return false;
        if (!mob.isSnailFlying() || mob.isSnailGliding()) {
            return false;
        }

        return getMob().pathfinding.canPathToPlayer(true);
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.isSnailFlying()) return false;
        return mob.serverData.shouldPathfind();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        mob.setSnailFlying(false);
        mob.pathfinding.updateNavigation();
        mob.pathfinding.updateMoveControl();
    }
}
