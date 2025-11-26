package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class ExtraHeart extends Choice {
    public ExtraHeart(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance hearts = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        data.putInt("originalhearts", (int) hearts.getBaseValue());
        int baseValue = (int) hearts.getBaseValue();
        baseValue += 2;
        hearts.setBaseValue(baseValue);
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        int originalhearts = data.getInt("originalhearts", 20);
        player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(originalhearts);
    }
}
