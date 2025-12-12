package bikerboys.wackylife.wyr.actualchoicesfr.choiceTemplates;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import org.apache.logging.log4j.util.*;

public class SimpleTickChoice extends Choice {
    TriConsumer<PlayerEntity, World, NbtCompound> triConsumer;

    public SimpleTickChoice(String id, boolean positive, TriConsumer<PlayerEntity, World, NbtCompound> triConsumer) {
        super(id, positive);
        this.triConsumer = triConsumer;
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        triConsumer.accept(player, world, data);
    }
}
