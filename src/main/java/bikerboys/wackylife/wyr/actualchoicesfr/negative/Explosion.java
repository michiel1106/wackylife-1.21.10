package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.network.*;
import net.minecraft.world.*;

import java.util.*;

public class Explosion extends Choice {

    public Explosion(String randomSprint, boolean b) {
        super(randomSprint, b);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        player.setInvulnerable(true);
        world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 6, World.ExplosionSourceType.TNT);
        player.setInvulnerable(false);
    }
}
