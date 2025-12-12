package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class Shorter extends Choice {
    public Shorter(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance value = player.getAttributeInstance(EntityAttributes.SCALE);
        data.putDouble("value", value.getBaseValue());
        value.setBaseValue(0.75d);
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        double originalhearts = data.getDouble("value", 1);
        player.getAttributeInstance(EntityAttributes.SCALE).setBaseValue(originalhearts);
    }
}
