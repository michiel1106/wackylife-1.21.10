package bikerboys.wackylife.choices;

import bikerboys.wackylife.attachements.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class ChoiceTicker {
    private static int tickTimer = 10;


    public static void tick(ClientWorld world) {
        if (MinecraftClient.getInstance().player == null) return;

        jesusTick(MinecraftClient.getInstance().player, world);
    }


    public static void jesusTick(PlayerEntity player, ClientWorld world) {
        if (!hasChoice("jesus", true)) return;
        MinecraftClient MC = MinecraftClient.getInstance();

        if(MC.options.sneakKey.isPressed())
            return;


        // move up in liquid
        if(player.isTouchingWater() || player.isInLava())
        {
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x, 0.11, velocity.z);
            tickTimer = 0;
            return;
        }

        // simulate jumping out of water
        Vec3d velocity = player.getVelocity();
        if(tickTimer == 0)
            player.setVelocity(velocity.x, 0.30, velocity.z);
        else if(tickTimer == 1)
            player.setVelocity(velocity.x, 0, velocity.z);

        // update timer
        tickTimer++;

    }





    public static boolean hasChoice(String choice, boolean positive) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (positive) {
                return ModAttachments.getChoice(player).positiveChoiceId().equalsIgnoreCase(choice);
            } else {
                return ModAttachments.getChoice(player).negativeChoiceId().equalsIgnoreCase(choice);
            }
        }

        return false;
    }
}
