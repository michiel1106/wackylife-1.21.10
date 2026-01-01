package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.util.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.math.*;
import net.minecraft.world.tick.*;

import java.util.*;

public class SwapWildcard extends Wildcard {

    // --- Configuration ---
    public static final int MIN_SWAP_INTERVAL_TICKS = 4000;   // 200 seconds
    public static final int MAX_SWAP_INTERVAL_TICKS = 8000;   // 400 seconds
    private static final Random rnd = new Random();

    // --- State ---
    private int swapTimer = 6000;
    private int swapInterval = 0;

    @Override
    public void onActivate(MinecraftServer server) {
        swapTimer = 6000;
        swapInterval = getRandomSwapInterval();
    }

    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        swapTimer--;

        if (swapTimer <= 0) {
            swapRandomPlayers(server);
            swapTimer = getRandomSwapInterval();
        }

        if (swapTimer == 260) {
            PlayerUtils.applyToAll(server, (player -> {
                PlayerUtils.playSoundToPlayer(player, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
                PlayerUtils.sendTitle(player, Text.literal("§r§6§lAre you ready for..."), 10, 180, 10);
            }));

            TaskScheduler.scheduleTask(220, () -> {
                PlayerUtils.applyToAll(server, (player -> {
                    PlayerUtils.playSoundToPlayer(player, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
                    PlayerUtils.sendTitle(player, Text.literal("§a§lA §e§lS§c§lW§a§lA§e§lP"), 0, 60, 10);
                }));
            });

            TaskScheduler.scheduleTask(260, () -> {
                PlayerUtils.applyToAll(server, (player -> {
                    PlayerUtils.playSoundToPlayers(server.getPlayerManager().getPlayerList(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
                }));
            });
        }

    }

    @Override
    public void deactivate(MinecraftServer server) {
        swapTimer = 0;
        swapInterval = 0;
    }

    private void swapRandomPlayers(MinecraftServer server) {
        List<ServerPlayerEntity> players = new ArrayList<>(server.getPlayerManager().getPlayerList()).stream().filter((player -> !ScoreboardManager.INSTANCE.isDead(player))).toList();
        // Need at least 2 players to swap
        if (players.size() < 2) {
            return;
        }
        // Store all positions and rotations before swapping
        List<Vec3d> positions = new ArrayList<>();
        List<Float> yaws = new ArrayList<>();
        List<Float> pitches = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            positions.add(player.getEntityPos());
            yaws.add(player.getYaw());
            pitches.add(player.getPitch());
        }
        // Shuffle until no player gets their original position
        List<Vec3d> shuffledPositions = new ArrayList<>(positions);
        boolean valid = false;
        while (!valid) {
            Collections.shuffle(shuffledPositions);
            valid = true;
            for (int i = 0; i < shuffledPositions.size(); i++) {
                if (shuffledPositions.get(i).equals(positions.get(i))) {
                    valid = false;
                    break;
                }
            }
        }
        // Teleport each player to a random position
        for (int i = 0; i < players.size(); i++) {
            ServerPlayerEntity player = players.get(i);
            Vec3d newPos = shuffledPositions.get(i);
            player.teleport(player.getEntityWorld(), newPos.x, newPos.y, newPos.z, EnumSet.noneOf(PositionFlag.class), yaws.get(i), pitches.get(i), false);
        }
    }

    private static int getRandomSwapInterval() {
        return MIN_SWAP_INTERVAL_TICKS + rnd.nextInt(MAX_SWAP_INTERVAL_TICKS - MIN_SWAP_INTERVAL_TICKS + 1);
    }

    public int getSwapTimer() {
        return swapTimer;
    }

    public void setSwapTimer(int amount) {
        swapTimer = amount;
    }

    @Override
    public String toString() {
        return "Swap";
    }
}