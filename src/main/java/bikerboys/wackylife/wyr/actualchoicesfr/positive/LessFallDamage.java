package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class LessFallDamage extends Choice {
    public LessFallDamage(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance falldamage = player.getAttributeInstance(EntityAttributes.FALL_DAMAGE_MULTIPLIER);
        data.putFloat("falldamage", (float) falldamage.getValue());
        falldamage.setBaseValue(0.64d);
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        float falldamage = data.getFloat("falldamage", 20);
        player.getAttributeInstance(EntityAttributes.FALL_DAMAGE_MULTIPLIER).setBaseValue(falldamage);
    }
}
