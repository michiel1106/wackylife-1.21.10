package bikerboys.wackylife.entity.snail.server;


import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.entity.snail.*;
import bikerboys.wackylife.entity.snail.goal.*;
import bikerboys.wackylife.util.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

@SuppressWarnings("resource")
public class SnailPathfinding {
    private final Snail snail;

    private MobNavigation groundNavigation;
    private BirdNavigation flyingNavigation;
    private MiningNavigation miningNavigation;

    private NavigationMode currentMode = NavigationMode.WALKING;
    private NavigationMode lastMode = NavigationMode.WALKING;

    private int pathfindingCacheTicks = 0;
    private static final int PATHFINDING_CACHE_DURATION = 20;
    private boolean cachedCanPathOnGround = true;
    private boolean cachedCanPathFlying = true;

    public boolean navigationInit = false;
    private double lastSpeedMultiplier = 0.99;

    public SnailPathfinding(Snail snail) {
        this.snail = snail;
        initializeNavigations();
    }

    private enum NavigationMode {
        WALKING,
        FLYING,
        MINING
    }

    private void initializeNavigations() {
        groundNavigation = new MobNavigation(snail, snail.getEntityWorld());
        flyingNavigation = new BirdNavigation(snail, snail.getEntityWorld());
        miningNavigation = new MiningNavigation(snail, snail.getEntityWorld());

        groundNavigation.setCanSwim(false);
        groundNavigation.setCanOpenDoors(false);
        groundNavigation.setCanWalkOverFences(false);

        flyingNavigation.setCanSwim(false);
        miningNavigation.setCanSwim(false);

    }

    public void tick() {
        if (snail.isPaused()) {
            snail.getNavigation().stop();
            return;
        }

        if (snail.age % 100 == 0 || !navigationInit) {
            navigationInit = true;
            updateMoveControl();
            updateNavigation();
        }
        else if (snail.age % 21 == 0) {
            updateMovementSpeed();
        }
        else if (snail.age % 5 == 0) {
            updateNavigationTarget();
        }

        if (pathfindingCacheTicks <= 0) {
            updatePathfindingCache();
            pathfindingCacheTicks = PATHFINDING_CACHE_DURATION;
        }
        else {
            pathfindingCacheTicks--;
        }

        if (snail.age % 10 == 0) {
            checkPathStaleness();
        }
    }

    private void checkPathStaleness() {
        if (!snail.serverData.shouldPathfind()) return;

        Path currentPath = snail.getNavigation().getCurrentPath();
        if (currentPath != null && (currentPath.isFinished() || currentPath.getLength() <= 1)) {
            updateNavigationTarget();
        }
    }

    private void updatePathfindingCache() {
        if (!snail.serverData.shouldPathfind() || snail.getEntityWorld().isClient()) {
            return;
        }

        LivingEntity target = snail.serverData.getBoundEntity();
        if (target == null) {
            cachedCanPathOnGround = false;
            cachedCanPathFlying = false;
            return;
        }

        Vec3d targetPos = target.getEntityPos();

        BlockPos groundPos = getGroundBlock();

        if (groundPos != null) {
            Vec3d originalPos = snail.getEntityPos();
            double groundY = groundPos.getY() + 1.0;

            boolean wasOnGround = snail.isOnGround();
            if (!wasOnGround) {
                snail.setOnGround(true);
                snail.setPos(snail.getX(), groundY, snail.getZ());
            }
            Path groundPath = groundNavigation.findPathTo(target, 0);
            if (!wasOnGround) {
                snail.setPos(originalPos.x, originalPos.y, originalPos.z);
                snail.setOnGround(wasOnGround);
            }

            cachedCanPathOnGround =  groundPath != null && groundPath.getEnd() != null && groundPath.getEnd().getBlockPos().toCenterPos().distanceTo(targetPos) < 2;
        }
        else {
            cachedCanPathOnGround = false;
        }

        if (!cachedCanPathOnGround) {
            Path flyingPath = flyingNavigation.findPathTo(target, 0);
            cachedCanPathFlying =  flyingPath != null && flyingPath.getEnd() != null && flyingPath.getEnd().getBlockPos().toCenterPos().distanceTo(targetPos) < 2;
        }
        else {
            cachedCanPathFlying = true;
        }
    }

    public boolean isValidGroundPosition(BlockPos groundPos) {
        if (groundPos == null) return false;
        BlockState block = snail.getEntityWorld().getBlockState(groundPos);
        if (block.isOf(Blocks.LAVA)) return false;
        if (block.isOf(Blocks.WATER)) return false;
        if (block.isOf(Blocks.POWDER_SNOW)) return false;
        return true;
    }

    public boolean canPathToPlayer(boolean requireFlying) {
        if (!snail.serverData.shouldPathfind()) return false;

        if (requireFlying) {
            return cachedCanPathFlying;
        }
        return cachedCanPathOnGround;
    }

    public boolean canPathToPlayerFromGround(boolean flying) {
        BlockPos groundPos = getGroundBlock();
        if (!isValidGroundPosition(groundPos)) {
            return false;
        }
        return canPathToPlayer(flying);
    }

    @Nullable
    public BlockPos getGroundBlock() {
        Vec3d startPos = snail.getEntityPos();

        int minY = snail.getEntityWorld().getBottomY();

        Vec3d endPos = new Vec3d(startPos.x, minY, startPos.z);

        BlockHitResult result = snail.getEntityWorld().raycast(
                new RaycastContext(
                        startPos,
                        endPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        snail
                )
        );
        if (result.getType() == HitResult.Type.MISS) return null;
        return result.getBlockPos();
    }

    public double getDistanceToGroundBlock() {
        BlockPos belowBlock = getGroundBlock();
        if (belowBlock == null) return Double.NEGATIVE_INFINITY;
        return snail.getY() - belowBlock.getY() - 1;
    }

    public void fakeTeleportNearPlayer(double minDistanceFromPlayer) {
        if (snail.getEntityWorld() instanceof ServerWorld level) {
            Entity boundEntity = snail.serverData.getBoundEntity();
            ServerPlayerEntity boundPlayer = snail.serverData.getPlayer();
            if (boundEntity == null || boundPlayer == null) return;
            if (boundEntity.getEntityWorld() instanceof ServerWorld entityWorld) {
                if (!snail.serverData.shouldPathfind()) return;
                BlockPos tpTo = getBlockPosNearTarget(boundEntity, minDistanceFromPlayer);
                if (tpTo == null) return;

                level.playSound(null, snail.getX(), snail.getY(), snail.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, snail.getSoundCategory(), snail.soundVolume(), snail.getPitch());
                entityWorld.playSound(null, tpTo.getX(), tpTo.getY(), tpTo.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT, snail.getSoundCategory(), snail.soundVolume(), snail.getPitch());

                AnimationUtils.spawnTeleportParticles(level, snail.getEntityPos());
                AnimationUtils.spawnTeleportParticles(entityWorld, tpTo.toCenterPos());
                snail.serverData.despawn();
                SnailsWildCard.spawnSnailFor(boundPlayer, tpTo);
            }
        }
    }

    public static BlockPos getBlockPosNearPlayer(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        BlockPos targetPos = target.getBlockPos();
        return LevelUtils.getCloseBlockPos(target.getEntityWorld(), targetPos, distanceFromTarget, 1, false);
    }

    public BlockPos getBlockPosNearTarget(Entity target, double distanceFromTarget) {
        if (target == null) return null;
        Vec3d targetPos = snail.serverData.getPlayerPos();
        if (targetPos == null) return null;
        BlockPos targetBlockPos = BlockPos.ofFloored(targetPos.x, targetPos.y, targetPos.z);
        return LevelUtils.getCloseBlockPos(target.getEntityWorld(), targetBlockPos, distanceFromTarget, 1, false);
    }

    public void updateNavigation() {
        NavigationMode desiredMode;

        if (snail.isSnailMining()) {
            desiredMode = NavigationMode.MINING;
        }
        else if (snail.isSnailFlying()) {
            desiredMode = NavigationMode.FLYING;
        }
        else {
            desiredMode = NavigationMode.WALKING;
        }

        if (desiredMode != currentMode) {
            switchNavigationMode(desiredMode);
            currentMode = desiredMode;
        }
    }

    private void switchNavigationMode(NavigationMode mode) {
        snail.getNavigation().stop();

        if (mode == NavigationMode.MINING) {
            setNavigationMining();
        }
        else if (mode == NavigationMode.FLYING) {
            setNavigationFlying();
        }
        else if (mode == NavigationMode.WALKING) {
            setNavigationWalking();
        }

        lastMode = mode;
    }

    public void updateMoveControl() {
        if (snail.isSnailFlying() || snail.isSnailMining()) {
            setMoveControlFlight();
        }
        else {
            setMoveControlWalking();
        }
    }

    public void setNavigationFlying() {

        snail.setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setPathfindingPenalty(PathNodeType.FENCE, -1);
        snail.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0);
        snail.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE, 0);
        snail.setPathfindingPenalty(PathNodeType.OPEN, 0);

        snail.setNavigation(flyingNavigation);
        updateNavigationTarget();
    }

    public void setNavigationWalking() {
        snail.setPathfindingPenalty(PathNodeType.BLOCKED, -1);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, -1);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, -1);
        snail.setPathfindingPenalty(PathNodeType.WATER, 8);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);

        snail.setPathfindingPenalty(PathNodeType.FENCE, 0);
        snail.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0);
        snail.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE, 0);
        snail.setPathfindingPenalty(PathNodeType.OPEN, 0);


        snail.setNavigation(groundNavigation);
        updateNavigationTarget();
    }

    public void setNavigationMining() {

        snail.setPathfindingPenalty(PathNodeType.BLOCKED, 4.0f);
        snail.setPathfindingPenalty(PathNodeType.TRAPDOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.DANGER_TRAPDOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE_DOOR, 0);
        snail.setPathfindingPenalty(PathNodeType.DOOR_OPEN, 0);
        snail.setPathfindingPenalty(PathNodeType.UNPASSABLE_RAIL, 0);
        snail.setPathfindingPenalty(PathNodeType.FENCE, 0);
        snail.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0);
        snail.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0);
        snail.setPathfindingPenalty(PathNodeType.WALKABLE, 0);
        snail.setPathfindingPenalty(PathNodeType.OPEN, 0);

        snail.setNavigation(miningNavigation);
        updateNavigationTarget();
    }

    public void updateNavigationTarget() {
        if (snail.serverData.shouldPathfind() && snail.serverData.getPlayerPos() != null) {
            snail.setTarget(snail.serverData.getBoundEntity());
        }
        else {
            snail.setTarget(null);
        }

        EntityNavigation nav = snail.getNavigation();
        if (nav instanceof BirdNavigation) {
            nav.setSpeed(1);
        }
        else {
            nav.setSpeed(Snail.MOVEMENT_SPEED);
        }
    }

    public void updateMovementSpeed() {
        Path path = snail.getNavigation().getCurrentPath();
        if (path != null) {
            double length = path.getLength();
            double speedMultiplier = 1;
            if (length > 10) {
                speedMultiplier += length / 100.0;
            }
            if (speedMultiplier != lastSpeedMultiplier) {
                lastSpeedMultiplier = speedMultiplier;
                double movementSpeed = Snail.MOVEMENT_SPEED * speedMultiplier * Snail.GLOBAL_SPEED_MULTIPLIER;
                double flyingSpeed = Snail.FLYING_SPEED * speedMultiplier * Snail.GLOBAL_SPEED_MULTIPLIER;
                if (snail.serverData.isNerfed()) {
                    movementSpeed *= 0.8;
                    flyingSpeed *= 0.8;
                }
                if (movementSpeed < 0.01) movementSpeed = 0.01;
                if (flyingSpeed < 0.01) flyingSpeed = 0.01;


                Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)).setBaseValue(movementSpeed);
                Objects.requireNonNull(snail.getAttributeInstance(EntityAttributes.FLYING_SPEED)).setBaseValue(flyingSpeed);

            }
        }
    }

    public void setMoveControlFlight() {
        snail.setNoGravity(true);
        snail.setMoveControl(new FlightMoveControl(snail, 20, true));
    }

    public void setMoveControlWalking() {
        snail.setNoGravity(false);
        snail.setMoveControl(new MoveControl(snail));
    }

    public void cleanup() {
        if (groundNavigation != null) groundNavigation.stop();
        if (flyingNavigation != null) flyingNavigation.stop();
        if (miningNavigation != null) miningNavigation.stop();
    }
}
