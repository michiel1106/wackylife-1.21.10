package bikerboys.wackylife.entity.snail.goal;


import bikerboys.wackylife.entity.snail.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.nbt.*;
import net.minecraft.storage.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("resource")
public final class SnailPushProjectilesGoal extends Goal {

    @NotNull
    private final Snail mob;
    @NotNull
    private List<ProjectileEntity> projectiles  = new ArrayList<>();

    public SnailPushProjectilesGoal(@NotNull Snail mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (mob.getEntityWorld().isClient()) return false;

        World level = mob.getEntityWorld();
        this.projectiles = level.getEntitiesByClass(
                ProjectileEntity.class,
                mob.getBoundingBox().expand(5.0, 5.0, 5.0),
                projectile -> projectile.distanceTo(mob) < 16
        );

        return !this.projectiles.isEmpty();
    }

    @Override
    public void start() {
        boolean playSound = false;
        for (ProjectileEntity projectile : projectiles) {

            NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY);
            projectile.writeData(writeView);
            NbtCompound nbt = writeView.getNbt();


            Optional<Boolean> bool = nbt.getBoolean("inGround");
            if (nbt.contains("inGround") && bool.isPresent()) {
                if (bool.get()) continue;
            }


            if (projectile instanceof PotionEntity && Snail.ALLOW_POTION_EFFECTS) {
                continue;
            }


            Entity sender = projectile.getOwner();
            if (sender instanceof LivingEntity target) {
                if (target instanceof Snail) {
                    continue;
                }

                double dx = target.getX() - projectile.getX();
                double dz = target.getZ() - projectile.getZ();
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

                double dy = target.getEyeY() - projectile.getY();

                float speed = 1.6F;

                double time = horizontalDistance / speed;
                double velocityY = dy / time + 0.5 * 0.05 * time;

                double velocityX = (dx / horizontalDistance) * speed;
                double velocityZ = (dz / horizontalDistance) * speed;

                projectile.setVelocity(velocityX, velocityY, velocityZ, 1.6F, 0.0F);
                if (!(projectile instanceof TridentEntity)) projectile.setOwner(mob);

                playSound = true;
            }
        }
        if (playSound) {
            mob.sounds.playThrowSound();
        }
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void stop() {
        this.projectiles.clear();
    }
}
