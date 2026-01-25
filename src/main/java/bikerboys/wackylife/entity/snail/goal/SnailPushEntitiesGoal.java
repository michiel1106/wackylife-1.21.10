package bikerboys.wackylife.entity.snail.goal;


import bikerboys.wackylife.entity.snail.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("resource")
public final class SnailPushEntitiesGoal extends Goal {

    @NotNull
    private final Snail mob;
    private int lastPushTime = 20;
    private List<Entity> pushAway = new ArrayList<>();

    public SnailPushEntitiesGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;
        World level = mob.getEntityWorld();

        lastPushTime++;
        int pushDelay = 20;
        if (lastPushTime < pushDelay) {
            return false;
        }
        lastPushTime = 0;

        pushAway = new ArrayList<>();
        pushAway.addAll(level.getEntitiesByClass(TntEntity.class, mob.getBoundingBox().expand(8.0), entity -> mob.distanceTo(entity) < 64.0));
        pushAway.addAll(level.getEntitiesByClass(TntMinecartEntity.class, mob.getBoundingBox().expand(8.0), entity -> mob.distanceTo(entity) < 64.0));

        pushAway.addAll(level.getEntitiesByClass(PotionEntity.class, mob.getBoundingBox().expand(8.0), entity -> mob.distanceTo(entity) < 64.0));



        return !pushAway.isEmpty();
    }

    @Override
    public void start() {
        if (pushAway != null) {
            mob.sounds.playThrowSound();
            for (Entity entity : pushAway) {
                pushAway(entity);
            }
        }
    }

    @Override
    public void stop() {
        pushAway = new ArrayList<>();
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    private void pushAway(Entity entity) {
        Vec3d direction = entity.getEntityPos()
                .add(0.0, 0.5, 0.0)
                .subtract(mob.getEntityPos())
                .normalize()
                .multiply(0.4);

        entity.setVelocity(entity.getVelocity().add(direction));
    }
}
