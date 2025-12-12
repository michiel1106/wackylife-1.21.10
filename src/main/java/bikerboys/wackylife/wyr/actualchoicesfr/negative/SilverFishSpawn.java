package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.network.*;
import net.minecraft.world.*;

import java.util.*;

public class SilverFishSpawn extends Choice {
    Random random = new Random();

    public SilverFishSpawn(String randomSprint, boolean b) {
        super(randomSprint, b);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        int timer = data.getInt("timer", random.nextInt(300, 800));
        timer -= 1;
        data.putInt("timer", timer);

        if (timer <= 0) {
            data.putInt("timer", random.nextInt(300, 800));
            if (player instanceof ServerPlayerEntity player1) {

                int rand = random.nextInt(1, 4);

                for (int i = 0; i < rand; i++) {
                    SilverfishEntity silverfishEntity = EntityType.SILVERFISH.create(world, SpawnReason.COMMAND);
                    silverfishEntity.setPos(player.getX(), player.getY(), player.getZ());
                    world.spawnEntity(silverfishEntity);




                }


            }
        }
    }
}
