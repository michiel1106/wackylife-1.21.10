package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;

public class MaxDurability extends Choice {
    public MaxDurability(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void onAddition(World world, PlayerEntity player, NbtCompound data) {
        for (ItemStack itemStack : player.getInventory()) {
            itemStack.setDamage(0);
        }
    }
}
