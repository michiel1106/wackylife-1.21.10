package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class FasterSwimming extends Choice {
    public FasterSwimming(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 20, 0));
    }
}
