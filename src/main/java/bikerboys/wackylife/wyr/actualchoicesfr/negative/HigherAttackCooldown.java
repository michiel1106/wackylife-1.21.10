package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.*;

public class HigherAttackCooldown extends Choice {
    public HigherAttackCooldown(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        player.getAttributeInstance(EntityAttributes.ATTACK_SPEED).addPersistentModifier(new EntityAttributeModifier(Identifier.of("wackylife:higherattackcooldown"), -0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        player.getAttributeInstance(EntityAttributes.ATTACK_SPEED).removeModifier(Identifier.of("wackylife:higherattackcooldown"));
    }
}
