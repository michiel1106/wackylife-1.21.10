package bikerboys.wackylife.wyr.actualchoicesfr.choiceTemplates;

import bikerboys.wackylife.*;
import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.entry.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class AttributeChoice extends Choice {
    RegistryEntry<EntityAttribute> attribute;
    EntityAttributeModifier.Operation operation;
    double value;

    public AttributeChoice(String id, boolean positive, RegistryEntry<EntityAttribute> attribute, double value, EntityAttributeModifier.Operation operation) {
        super(id, positive);
        this.attribute = attribute;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(attribute);
        if (attributeInstance != null) {
            attributeInstance.addPersistentModifier(new EntityAttributeModifier(Identifier.of(WackyLife.MOD_ID, getId()), value, operation));
        }

    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(attribute);
        if (attributeInstance != null) {
            attributeInstance.removeModifier(Identifier.of(WackyLife.MOD_ID, getId()));
        }
    }
}
