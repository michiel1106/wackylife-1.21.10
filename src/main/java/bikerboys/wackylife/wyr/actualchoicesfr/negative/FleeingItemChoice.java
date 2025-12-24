package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class FleeingItemChoice extends Choice {

    public FleeingItemChoice(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        Box box = new Box(player.getBlockPos()).expand(5);

        List<ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, box, item -> item.distanceTo(player) <= 4.5f);


        for (ItemEntity itemEntity : itemEntities) {
            double dx = itemEntity.getX() - player.getX();
            double dy = itemEntity.getY() - player.getY();
            double dz = itemEntity.getZ() - player.getZ();

            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length == 0) continue;

            // Normalize
            dx /= length;
            dy /= length;
            dz /= length;

            double strength = 0.1 * (4.5f - itemEntity.distanceTo(player));

            itemEntity.addVelocity(
                    dx * strength,
                    0, // optional small upward bias
                    dz * strength
            );

            itemEntity.velocityModified = true;

        }


    }
}
