package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.server.network.*;
import net.minecraft.world.*;

import java.util.*;

public class OccasionalDrops extends Choice {
    Random random = new Random();

    public OccasionalDrops(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        int timer = data.getInt("timer", random.nextInt(3500, 6000));
        timer -= 1;
        data.putInt("timer", timer);

        if (timer <= 0) {
            data.putInt("timer", random.nextInt(3500, 6000));
            ((ServerPlayerEntity) player).dropSelectedItem(true);
        }
    }
}
