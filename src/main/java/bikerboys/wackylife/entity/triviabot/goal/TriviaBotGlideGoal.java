package bikerboys.wackylife.entity.triviabot.goal;

import bikerboys.wackylife.entity.triviabot.*;
import net.minecraft.entity.ai.goal.*;
import org.jetbrains.annotations.*;

public final class TriviaBotGlideGoal extends Goal {

    @NotNull
    private final TriviaBot mob;
    private final int ticksToWait;
    private int ticksWaited;

    public TriviaBotGlideGoal(@NotNull TriviaBot mob) {
        this.mob = mob;
        this.ticksToWait = 2;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (mob.isBotGliding()) {
            return true;
        }

        if (mob.getVelocity().y >= 0 || mob.isOnGround()) {
            return false;
        }

        if (mob.pathfinding.getDistanceToGroundBlock() <= 1.5) {
            return false;
        }

        if (ticksWaited < ticksToWait) {
            ticksWaited++;
            return false;
        }

        return true;
    }


    @Override
    public void start() {
        ticksWaited = 0;
        mob.setGliding(true);
    }

    @Override
    public boolean shouldContinue() {
        return mob.pathfinding.getDistanceToGroundBlock() >= 1;
    }

    @Override
    public void tick() {
        mob.setVelocity(0, -0.1, 0);
    }

    @Override
    public void stop() {
        mob.setGliding(false);
        mob.pathfinding.updateNavigation();
    }
}
