package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class SlownessChoice extends Choice {
    public SlownessChoice(boolean positive) {
        super("slowness", positive);
    }



    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 4));
    }

}
