package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class AggressiveEnderman extends Choice {
    public AggressiveEnderman(String id, boolean positive) {
        super(id, positive);
    }


    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        Box box = new Box(player.getBlockPos());
        box.expand(5);
        List<EndermanEntity> entitiesByType = world.getEntitiesByType(EntityType.ENDERMAN, box, (endermanEntity -> true));
        for (EndermanEntity endermanEntity : entitiesByType) {
            endermanEntity.setAngryAt(player.getUuid());
        }
    }
}
