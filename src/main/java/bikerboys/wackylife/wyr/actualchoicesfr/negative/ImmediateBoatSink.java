package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

import java.util.*;

public class ImmediateBoatSink extends Choice {
    public ImmediateBoatSink(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        Entity vehicle = player.getVehicle();

        if (vehicle instanceof AbstractBoatEntity entity) {
            if (!entity.isSubmergedInWater()) {
                entity.setVelocity(entity.getVelocity().add(0, -0.2, 0));
            }
        }

    }
}
