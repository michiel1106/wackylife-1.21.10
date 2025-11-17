package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class HigherStepSize extends Choice {
    public HigherStepSize(boolean positive) {
        super("higher_step_size", positive);
    }


    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {

        EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attributeInstance != null) {
            double baseValue = attributeInstance.getBaseValue();
            data.putDouble("previousstepheight", baseValue);

            attributeInstance.setBaseValue(1d);
        }


    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attributeInstance != null) {
            double baseValue = attributeInstance.getBaseValue();
            double previousscale = data.getDouble("previousstepheight", baseValue);

            attributeInstance.setBaseValue(previousscale);

        }
    }
}
