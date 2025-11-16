package bikerboys.wackylife.wyr.actualchoicesfr;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.text.*;
import net.minecraft.world.*;

public class JumpBoostChoice extends Choice {
    public JumpBoostChoice() {
        super("jump_boost", true);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        data.putInt("data", 0);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        int counter = data.getInt("data").get();

        counter += 1;

        data.putInt("data", counter);


        player.sendMessage(Text.literal("jumpboostactive " + data.getInt("data").get()), true);
    }
}
