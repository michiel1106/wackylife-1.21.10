package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class MoreKnockback extends Choice {
    public MoreKnockback(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        player.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).addPersistentModifier(new EntityAttributeModifier(Identifier.of("wackylife:moreknockback"), 2, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        player.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).removeModifier(Identifier.of("wackylife:moreknockback"));
    }
}
