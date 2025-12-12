package bikerboys.wackylife.wyr.actualchoicesfr.choiceTemplates;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.entry.*;
import net.minecraft.world.*;

public class SetAttributeChoice extends Choice {
    RegistryEntry<EntityAttribute> attribute;
    double value;

    public SetAttributeChoice(String id, boolean positive, RegistryEntry<EntityAttribute> attribute, double value) {
        super(id, positive);
        this.attribute = attribute;
        this.value = value;
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        EntityAttributeInstance attributeInstance = player.getAttributeInstance(attribute);
        data.putDouble("value", attributeInstance.getBaseValue());
        attributeInstance.setBaseValue(value);
    }

    @Override
    public void onRemoval(World world, PlayerEntity player, NbtCompound data) {
        double originalhearts = data.getDouble("value").get();
        player.getAttributeInstance(attribute).setBaseValue(originalhearts);
    }
}
