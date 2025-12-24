package bikerboys.wackylife.wyr.actualchoicesfr.positive;

import bikerboys.wackylife.wyr.choice.Choice;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JesusChoice extends Choice {

    public JesusChoice(String id, boolean positive) {
        super(id, positive);
    }

    @Override
    public void tick(World world, PlayerEntity player, NbtCompound data) {

    }


}