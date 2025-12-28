package bikerboys.wackylife.util;

import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import org.joml.*;

import java.awt.*;
import java.lang.Math;

public class AnimationUtils {
    private static int spiralDuration = 175;



    public static void createSpiral(ServerPlayerEntity player, int duration) {
        spiralDuration = duration;
        TaskScheduler.scheduleTask(1, () -> startSpiral(player));
    }

    private static void startSpiral(ServerPlayerEntity player) {
        TaskScheduler.scheduleTask(1, () -> runSpiralStep(player, 0));
    }

    private static void runSpiralStep(ServerPlayerEntity player, int step) {
        if (player == null) return;

        processSpiral(player, step);
        processSpiral(player, step+1);
        processSpiral(player, step+2);
        processSpiral(player, step+3);

        if (step <= spiralDuration) {
            TaskScheduler.scheduleTask(1, () -> runSpiralStep(player, step + 4));
        }
    }

    private static void processSpiral(ServerPlayerEntity player, int step) {
        ServerWorld level = player.getEntityWorld();
        double x = player.getX();
        double z = player.getZ();
        double yStart = player.getY();
        double height = 1.0;
        double radius = 0.8;
        int pointsPerCircle = 40;

        double angle = 2 * Math.PI * (step % pointsPerCircle) / pointsPerCircle + step / 4.0;
        double y = yStart + height * Math.sin(Math.PI * (step - 20) / 20) + 1;

        double offsetX = radius * Math.cos((float) angle);
        double offsetZ = radius * Math.sin((float) angle);

        level.spawnParticles(
                ParticleTypes.HAPPY_VILLAGER,
                x + offsetX, y, z + offsetZ,
                1, 0, 0, 0, 0
        );
    }

    public static void createGlyphAnimation(ServerWorld level, Vec3d target, int duration) {
        if (level == null || target == null || duration <= 0) return;

        double radius = 7.5; // Radius of the glyph starting positions

        for (int step = 0; step < duration; step++) {
            TaskScheduler.scheduleTask(step, () -> spawnGlyphParticles(level, target, radius));
        }
    }

    private static void spawnGlyphParticles(ServerWorld level, Vec3d target, double radius) {
        int particlesPerTick = 50; // Number of glyphs spawned per tick
        Random random = level.getRandom();

        for (int i = 0; i < particlesPerTick; i++) {
            // Randomize starting position around the target block
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = radius * (random.nextDouble() * 0.5); // Random distance within radius

            double startX = target.x + distance * Math.cos(angle);
            double startY = target.y + random.nextDouble()*2+1; // Random height variation
            double startZ = target.z + distance * Math.sin(angle);

            // Compute the velocity vector toward the target
            double targetX = target.x;
            double targetY = target.y;
            double targetZ = target.z;

            double dx = targetX - startX;
            double dy = targetY - startY;
            double dz = targetZ - startZ;

            // Normalize velocity to control particle speed
            double velocityScale = -50; // Adjust speed (lower values for slower movement)
            double vx = dx * velocityScale;
            double vy = dy * velocityScale;
            double vz = dz * velocityScale;

            // Spawn the particle with velocity
            level.spawnParticles(
                    ParticleTypes.ENCHANT, // Glyph particle
                    startX, startY, startZ, // Starting position
                    0, // Number of particles to display as a burst (keep 0 for velocity to work)
                    vx, vy, vz, // Velocity components
                    0.2 // Spread (keep non-zero to activate velocity)
            );
        }
    }

    public static void spawnFireworkBall(ServerWorld level, Vec3d position, int duration, double radius, Vector3f color) {
        if (level == null || position == null || duration <= 0 || radius <= 0) return;

        Random random = level.getRandom();

        for (int step = 0; step < duration; step++) {
            TaskScheduler.scheduleTask(step, () -> {
                // Spawn particles in a spherical pattern for the current step
                for (int i = 0; i < 50; i++) { // 50 particles per tick
                    double theta = random.nextDouble() * 2 * Math.PI; // Angle in the XY plane
                    double phi = random.nextDouble() * Math.PI; // Angle from the Z-axis
                    double r = radius * (0.8 + 0.2 * random.nextDouble()); // Slight variation in radius

                    // Spherical coordinates to Cartesian
                    double x = r * Math.sin(phi) * Math.cos(theta);
                    double y = r * Math.sin(phi) * Math.sin(theta);
                    double z = r * Math.cos(phi);


                    DustParticleEffect particleEffect = new DustParticleEffect(new Color(color.x, color.y, color.z).getRGB(), 1.0f);

                    // Spawn particle with random offset
                    level.spawnParticles(
                            particleEffect, // Colored particle effect
                            position.x + x,
                            position.y + y,
                            position.z + z,
                            1, // Particle count
                            0, 0, 0, // No velocity
                            0 // No spread
                    );
                }
            });
        }
    }

    public static void spawnTeleportParticles(ServerWorld level, Vec3d pos) {
        level.spawnParticles(
                ParticleTypes.PORTAL,
                pos.x, pos.y, pos.z,
                30,
                0, 0, 0,
                0.35
        );
    }
}
