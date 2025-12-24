package bikerboys.wackylife.wyr.actualchoicesfr.choiceTemplates;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.entry.*;
import net.minecraft.world.*;

import java.util.function.*;

public class PotionEffectChoice extends Choice {
    RegistryEntry<StatusEffect> statusEffect;
    int amplifier;
    Predicate<PlayerEntity> predicate;

    public PotionEffectChoice(String id, boolean positive, RegistryEntry<StatusEffect> statusEffect, Predicate<PlayerEntity> predicate) {
        this(id, positive, statusEffect, predicate, 0);
    }

    public PotionEffectChoice(String id, boolean positive, RegistryEntry<StatusEffect> statusEffect, Predicate<PlayerEntity> predicate, int amplifier) {
        super(id, positive);
        this.statusEffect = statusEffect;
        this.amplifier = amplifier;
        this.predicate = predicate;
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        if (predicate.test(player)) {
            player.addStatusEffect(new StatusEffectInstance(statusEffect, 30, amplifier, false, false));
        }
    }
}
