package bikerboys.wackylife.wyr.actualchoicesfr.negative;

import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.server.world.*;
import net.minecraft.world.*;

import java.util.*;

public class NoArmorAboveDiamond extends Choice {

    public NoArmorAboveDiamond(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {
        ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = player.getEquippedStack(EquipmentSlot.FEET);

        if (world instanceof ServerWorld serverWorld) {
            if (head.isOf(Items.DIAMOND_HELMET) || head.isOf(Items.NETHERITE_HELMET)) {
                player.dropStack(serverWorld, head.copy());
                head.decrement(1);
            }

            if (chest.isOf(Items.DIAMOND_CHESTPLATE) || chest.isOf(Items.NETHERITE_CHESTPLATE)) {
                player.dropStack(serverWorld, chest.copy());
                chest.decrement(1);
            }

            if (legs.isOf(Items.DIAMOND_LEGGINGS) || legs.isOf(Items.NETHERITE_LEGGINGS)) {
                player.dropStack(serverWorld, legs.copy());
                legs.decrement(1);
            }

            if (feet.isOf(Items.DIAMOND_BOOTS) || feet.isOf(Items.NETHERITE_BOOTS)) {
                player.dropStack(serverWorld, feet.copy());
                feet.decrement(1);
            }


        }

    }
}
