package bikerboys.wackylife.mixin.client;

import bikerboys.wackylife.*;
import static bikerboys.wackylife.WackyLifeClient.x;
import static bikerboys.wackylife.WackyLifeClient.y;
import static bikerboys.wackylife.WackyLifeClient.z;
import bikerboys.wackylife.attachements.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.predicate.entity.*;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;

@Debug(export = true)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        return null;
    }

    @Shadow
    @Final
    private Camera camera;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickProgress) {
        double maxRange = Math.max(blockInteractionRange, entityInteractionRange);
        double squaredMaxRange = MathHelper.square(maxRange);

        Vec3d forward = camera.getRotationVec(tickProgress);
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = forward.crossProduct(up).normalize();
        up = right.crossProduct(forward).normalize();

        float x = WackyLifeClient.x;
        float y = WackyLifeClient.y;
        float z = WackyLifeClient.z;

        switch (WackyLifeClient.currentlives) {
            case 6 -> x = 0f;
            case 5 -> x = 0.06f;
            case 4 -> x = 0.12f;
            case 3 -> x = 0.18f;
            case 2 -> x = 0.24f;
            case 1 -> x = 0.3f;
        }

        if (MinecraftClient.getInstance().player != null && ModAttachments.getChoice(MinecraftClient.getInstance().player).negativeChoiceId().equalsIgnoreCase("offsetcrosshair")) {
            x = 0.6f;
        }

        Vec3d offset = right.multiply(x).add(up.multiply(y)).add(forward.multiply(z));

        Vec3d start = camera.getCameraPosVec(tickProgress).add(offset);
        Vec3d end = start.add(forward.multiply(maxRange));

        // Block raycast

        HitResult blockHit = camera.getEntityWorld().raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                camera
        ));

        double blockDistanceSq = blockHit.getPos().squaredDistanceTo(start);
        double range = maxRange;
        if (blockHit.getType() != HitResult.Type.MISS) {
            squaredMaxRange = blockDistanceSq;
            range = Math.sqrt(blockDistanceSq);
        }

        // Entity raycast
        Box box = camera.getBoundingBox().stretch(forward.multiply(range)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHit = ProjectileUtil.raycast(camera, start, end, box, EntityPredicates.CAN_HIT, squaredMaxRange);

        return entityHit != null && entityHit.getPos().squaredDistanceTo(start) < blockDistanceSq
                ? ensureTargetInRange(entityHit, start, entityInteractionRange)
                : ensureTargetInRange(blockHit, start, blockInteractionRange);
    }
}