package bikerboys.wackylife.wyr.actualchoicesfr.choiceTemplates;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import org.apache.commons.lang3.function.*;

public class OnAddChoice extends Choice {
    TriConsumer<PlayerEntity, World, NbtCompound> triConsumer;

    public OnAddChoice(String id, boolean positive, TriConsumer<PlayerEntity, World, NbtCompound> triConsumer) {
        super(id, positive);
        this.triConsumer = triConsumer;
    }


    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        triConsumer.accept(player, world, data);
    }
}
