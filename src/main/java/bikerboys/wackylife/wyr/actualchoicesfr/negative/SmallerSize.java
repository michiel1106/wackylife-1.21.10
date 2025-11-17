package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class SmallerSize extends Choice {
    public SmallerSize(boolean positive) {
        super("smaller_size", positive);
    }


    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {

        EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.SCALE);
        if (attributeInstance != null) {
            double baseValue = attributeInstance.getBaseValue();
            data.putDouble("previousscale", baseValue);

            attributeInstance.setBaseValue(0.5d);
        }


    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.SCALE);
        if (attributeInstance != null) {
            double baseValue = attributeInstance.getBaseValue();
            double previousscale = data.getDouble("previousscale", baseValue);

            attributeInstance.setBaseValue(previousscale);

        }
    }
}
