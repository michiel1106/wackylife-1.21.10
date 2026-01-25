package bikerboys.wackylife.util;

import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class LevelUtils {

    public static int findTopSafeY(World level, Vec3d pos) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos.x, level.getHeight(), pos.z);
        // Check upwards or downwards for the first safe position

        int minBuildHeight = level.getBottomY();

        while (mutablePos.getY() >= minBuildHeight) {
            if (isSafeSpot(level, mutablePos)) {
                return mutablePos.getY(); // Found a safe spot
            }
            mutablePos.move(0, -1, 0);
        }
        // Fallback to original position if no safe spot found
        return (int) pos.y;
    }

    public static boolean isSafeSpot(World level, BlockPos.Mutable pos) {
        // Check if the block below is solid
        boolean isSolidBlockBelow = level.getBlockState(pos.down()).hasSolidTopSurface(level, pos.down(), new ZombieEntity(level));

        // Check if the current position and one above are non-collision blocks (air, water, etc.)
        boolean isNonCollisionAbove = level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.up()).getCollisionShape(level, pos.up()).isEmpty();

        return isSolidBlockBelow && isNonCollisionAbove;
    }

    public static void summonHarmlessLightning(ServerPlayerEntity player) {
        summonHarmlessLightning(player.getEntityWorld(), player.getEntityPos());
    }

    public static void summonHarmlessLightning(ServerWorld level, Vec3d pos) {
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, level);
        lightning.setPos(pos.x, pos.y, pos.z);
        lightning.setCosmetic(true);
        level.spawnEntity(lightning);
    }

    public static BlockPos getCloseBlockPos(World level, BlockPos targetPos, double distanceFromTarget, int height, boolean bottomSupport) {
        for (int attempts = 0; attempts < 20; attempts++) {
            Vec3d offset = new Vec3d(
                    level.random.nextDouble() * 2 - 1,
                    0,
                    level.random.nextDouble() * 2 - 1
            ).normalize().multiply(distanceFromTarget);

            BlockPos pos = targetPos.add((int) offset.x, 0, (int) offset.z);

            BlockPos validPos = findNearestAirBlock(pos, level, height, bottomSupport);
            if (validPos != null) {
                return validPos;
            }
        }

        return targetPos;
    }

    private static BlockPos findNearestAirBlock(BlockPos pos, World level, int height, boolean bottomSupport) {
        for (int yOffset = 5; yOffset >= -5; yOffset--) {
            BlockPos newPos = pos.up(yOffset);
            if (bottomSupport) {
                BlockPos bottomPos = newPos.down();
                if (!level.getBlockState(bottomPos).isSideSolidFullSquare(level, bottomPos, Direction.UP)) {
                    continue;
                }
            }
            boolean allAir = true;
            for (int i = 0; i < height; i++) {
                BlockPos airTest = newPos.up(i);
                if (!level.getBlockState(airTest).isAir()) {
                    allAir = false;
                }
            }
            if (allAir) {
                return newPos;
            }

        }
        return null;
    }

    public static <T extends Entity> T spawnEntity(EntityType<T> entityType, ServerWorld level, BlockPos pos) {
        return entityType.spawn(level, pos, SpawnReason.COMMAND);
    }

    public static void teleport(Entity entity, ServerWorld level, double destX, double destY, double destZ) {
        teleport(entity, level, destX, destY, destZ, entity.getYaw(), entity.getPitch());
    }

    public static void teleport(Entity entity, ServerWorld level, BlockPos pos) {
        teleport(entity, level, Vec3d.ofBottomCenter(pos));
    }

    public static void teleport(Entity entity, ServerWorld level, Vec3d pos) {
        teleport(entity, level, pos.x, pos.y, pos.z, entity.getYaw(), entity.getPitch());
    }

    public static void teleport(Entity entity, ServerWorld level, Vec3d pos, float yaw, float pitch) {
        teleport(entity, level, pos.x, pos.y, pos.z, yaw, pitch);
    }

    public static void teleport(Entity entity, ServerWorld level, double destX, double destY, double destZ, float yaw, float pitch) {
        entity.teleport(level, destX, destY, destZ, EnumSet.noneOf(PositionFlag.class), yaw, pitch, false);
    }

    public static ChunkPos chunkPosFromBlockPos(BlockPos pos) {
        return new ChunkPos(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
    }
}
