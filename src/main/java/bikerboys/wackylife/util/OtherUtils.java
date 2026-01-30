package bikerboys.wackylife.util;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class OtherUtils {
    public static final Random rnd = new Random();

    public static SoundEvent getRandomSound(String name, int from, int to) {
        if (to > from) {
            int index = rnd.nextInt(from, to + 1);
            name += index;
        }
        return SoundEvent.of(Identifier.ofVanilla(name));
    }

    public static ItemEntity spawnItemForPlayerWithVelocity(ServerWorld level, Vec3d position, ItemStack stack, PlayerEntity player, Vec3d velocity) {
        if (level == null || stack.isEmpty()) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(level, position.x, position.y, position.z, stack);
        itemEntity.setPickupDelay(20);

        itemEntity.setVelocity(velocity);
        if (player != null) itemEntity.setOwner(player.getUuid());

        level.spawnEntity(itemEntity);
        return itemEntity;
    }
}
