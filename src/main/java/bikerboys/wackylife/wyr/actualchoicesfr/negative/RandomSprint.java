package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.network.*;
import net.minecraft.world.*;

import java.util.*;

public class RandomSprint extends Choice {
    Random random = new Random();

    public RandomSprint(String randomSprint, boolean b) {
        super(randomSprint, b);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        int timer = data.getInt("timer", random.nextInt(600, 1200));
        timer -= 1;
        data.putInt("timer", timer);

        if (timer <= 0) {
            data.putInt("timer", random.nextInt(600, 1200));
            if (player instanceof ServerPlayerEntity player1) {
                ServerPlayNetworking.send(player1, new RandomSprintS2C(player.isSprinting()));
            }
        }
    }
}
