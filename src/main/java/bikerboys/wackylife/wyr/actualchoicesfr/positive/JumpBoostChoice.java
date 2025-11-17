package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.text.*;
import net.minecraft.world.*;

public class JumpBoostChoice extends Choice {
    public JumpBoostChoice(boolean positive) {
        super("jump_boost", positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 10, 3));
    }


}
