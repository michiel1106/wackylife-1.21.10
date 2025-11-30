package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class InstantBreeding extends Choice {
    public InstantBreeding(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        Box box = new Box(player.getBlockPos()).expand(6);

        List<PassiveEntity> entitiesByClass = world.getEntitiesByClass(PassiveEntity.class, box, (d) -> true);

        for (PassiveEntity entity : entitiesByClass) {
            if (!entity.isBaby()) {
                entity.setBreedingAge(0);
            }
        }

    }
}
