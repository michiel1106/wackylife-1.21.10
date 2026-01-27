package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.util.*;
import com.mojang.datafixers.kinds.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.tick.*;

import java.util.*;

public class AnxietyWildcard extends Wildcard {
    private static final Random random = new Random();
    public static Map<UUID, PlayerAnxietyEntry> playerAnxietyEntries;
    private static final int maxTimer = 300;

    @Override
    public String toString() {
        return "anxiety";
    }

    @Override
    public void onActivate(MinecraftServer server) {
        anxietizePlayers(server);
    }

    public void anxietizePlayers(MinecraftServer server) {
        if (playerAnxietyEntries == null) {
            playerAnxietyEntries = new HashMap<>();
        }

        playerAnxietyEntries.clear();

        List<ServerPlayerEntity> players = new ArrayList<>(PlayerUtils.getActivePlayers(server));

        if (players.size() < 2) return;

        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i += 2) {
            ServerPlayerEntity target = players.get(i);
            ServerPlayerEntity source = (i + 1 < players.size()) ? players.get(i + 1) : players.get(0);
            playerAnxietyEntries.put(
                    target.getUuid(),
                    new PlayerAnxietyEntry(
                            source.getUuid(),
                            random.nextInt(15,25),
                            0)
            );
        }


        TaskScheduler.scheduleTask(200, () -> {

            for (ServerPlayerEntity player : players) {
                PlayerAnxietyEntry playerAnxietyEntry = playerAnxietyEntries.get(player.getUuid());

                if (playerAnxietyEntry == null) continue;
                ServerPlayerEntity player1 = server.getPlayerManager().getPlayer(playerAnxietyEntry.getTargetPlayer());
                if (player1 == null) continue;

                Text text = Text.literal("AVOID " + player1.getName().getString().toUpperCase()).formatted(Formatting.DARK_RED, Formatting.BOLD);

                PlayerUtils.sendTitle(player, text, 20, 60, 20);

            }

        });
    }


    @Override
    public void tick(MinecraftServer server) {
        if (playerAnxietyEntries.isEmpty()) return;
        if (Constants.paused) return;


        playerAnxietyEntries.forEach(((uuid, playerAnxietyEntry) -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            ServerPlayerEntity anxietyInducingPlayer = server.getPlayerManager().getPlayer(playerAnxietyEntry.getTargetPlayer());

            if (player == null || anxietyInducingPlayer == null) return;
            int warningTimer = playerAnxietyEntry.getWarningTimer();
            float distanceToTarget = player.distanceTo(anxietyInducingPlayer);

            if (distanceToTarget <= playerAnxietyEntry.getMaxdistanceuntilhurtingstops()) {
                playerAnxietyEntry.incrementWarningTimer();

                if (warningTimer == 2 * 20) {
                    Text text = Text.literal("TOO CLOSE, STAY AWAY FROM " + anxietyInducingPlayer.getName().getString().toUpperCase()).formatted(Formatting.DARK_GREEN, Formatting.BOLD);
                    player.sendMessage(text, true);
                }

                if (warningTimer == 4 * 20) {
                    Text text = Text.literal("TOO CLOSE, STAY AWAY FROM " + anxietyInducingPlayer.getName().getString().toUpperCase()).formatted(Formatting.GREEN, Formatting.BOLD);
                    player.sendMessage(text, true);
                }

                if (warningTimer == 6 * 20) {
                    Text text = Text.literal("TOO CLOSE, STAY AWAY FROM " + anxietyInducingPlayer.getName().getString().toUpperCase()).formatted(Formatting.YELLOW, Formatting.BOLD);
                    player.sendMessage(text, true);
                }

                if (warningTimer == 8 * 20) {
                    Text text = Text.literal("TOO CLOSE, STAY AWAY FROM " + anxietyInducingPlayer.getName().getString().toUpperCase()).formatted(Formatting.RED, Formatting.BOLD);
                    player.sendMessage(text, true);
                }

                if (warningTimer >= 10 * 20 && warningTimer % 20 == 1) {
                    Text text = Text.literal("TOO CLOSE, STAY AWAY FROM " + anxietyInducingPlayer.getName().getString().toUpperCase()).formatted(Formatting.DARK_RED, Formatting.BOLD);
                    player.sendMessage(text, true);
                }

                if (warningTimer >= maxTimer) {
                    playerAnxietyEntry.resetWarningTimer();
                    player.damage(player.getEntityWorld(), anxietyInducingPlayer.getDamageSources().playerAttack(anxietyInducingPlayer), 1000000f);
                }

            } else {
                playerAnxietyEntry.resetWarningTimer();

            }



        }));



    }

    public static class PlayerAnxietyEntry {
        UUID targetPlayer;
        int maxdistanceuntilhurtingstops;
        int warningTimer;


        public PlayerAnxietyEntry(UUID targetPlayer, int maxdistanceuntilhurtingstops, int warningTimer) {
            this.targetPlayer = targetPlayer;
            this.maxdistanceuntilhurtingstops = maxdistanceuntilhurtingstops;
            this.warningTimer = warningTimer;
        }


        public int getMaxdistanceuntilhurtingstops() {
            return maxdistanceuntilhurtingstops;
        }

        public int getWarningTimer() {
            return warningTimer;
        }

        public UUID getTargetPlayer() {
            return targetPlayer;
        }

        public void incrementWarningTimer() {
            warningTimer++;
        }

        public void resetWarningTimer() {
            warningTimer = 0;
        }
    }

}
