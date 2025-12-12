package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class LowerJumpHeight extends Choice {
    public LowerJumpHeight(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance jump = player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH);
        data.putDouble("originaljumpheight", jump.getBaseValue());
        jump.setBaseValue(0.35);
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        double originalJump = data.getDouble("originaljumpheight").get();
        player.getAttributeInstance(EntityAttributes.JUMP_STRENGTH).setBaseValue(originalJump);
    }
}
