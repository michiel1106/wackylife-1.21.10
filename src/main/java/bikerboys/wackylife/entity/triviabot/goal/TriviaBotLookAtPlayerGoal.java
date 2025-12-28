package bikerboys.wackylife.entity.triviabot.goal;

import bikerboys.wackylife.entity.triviabot.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.util.math.*;

import java.util.*;

public class TriviaBotLookAtPlayerGoal extends Goal {
    protected final TriviaBot mob;
    protected static final double RANGE_SQUARED = 400;
    private int lookTime;

    public TriviaBotLookAtPlayerGoal(TriviaBot mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        if (!mob.interactedWith() || mob.santaBot()) return false;

        Vec3d targetPos = mob.serverData.getPlayerPos();
        if (targetPos == null) return false;

        return this.mob.squaredDistanceTo(targetPos) <= RANGE_SQUARED;
    }

    @Override
    public boolean shouldContinue() {
        Vec3d targetPos = mob.serverData.getPlayerPos();
        if (targetPos == null) return false;

        if (this.mob.squaredDistanceTo(targetPos) > RANGE_SQUARED) return false;
        return this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = this.getTickCount(40 + this.mob.getRandom().nextInt(40));
    }

    @Override
    public void tick() {
        Vec3d targetPos = mob.serverData.getPlayerPos();
        if (targetPos != null) {
            double d = this.mob.getEyeY();
            this.mob.getLookControl().lookAt(targetPos.x, d, targetPos.z);
            --this.lookTime;
        }
    }
}
