package bikerboys.wackylife.mixin.client.choices;

import bikerboys.wackylife.choices.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.stream.*;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    private int packetTimer = 0;

    public boolean isOverLiquid()
    {
        boolean foundLiquid = false;
        boolean foundSolid = false;
        Box box = MinecraftClient.getInstance().player.getBoundingBox().offset(0, -0.5, 0);

        ArrayList<Block> blockCollisions = BlockUtils.getBlockCollisions(box)
                .map(bb -> BlockUtils.getBlock(BlockPos.ofFloored(bb.getCenter())))
                .collect(Collectors.toCollection(ArrayList::new));

        for(Block block : blockCollisions)
            if(block instanceof FluidBlock)
                foundLiquid = true;
            else if(!(block instanceof AirBlock))
                foundSolid = true;

        return foundLiquid && !foundSolid;
    }

    public boolean shouldBeSolid()
    {
        MinecraftClient MC = MinecraftClient.getInstance();
        return ChoiceTicker.hasChoice("jesus", true) && MC.player != null && MC.player.fallDistance <= 3
                && !MC.options.sneakKey.isPressed() && !MC.player.isTouchingWater()
                && !MC.player.isInLava();
    }

}
