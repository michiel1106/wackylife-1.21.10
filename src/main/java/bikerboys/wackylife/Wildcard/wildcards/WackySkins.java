package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;


import java.util.*;
import java.util.stream.*;

public class WackySkins extends Wildcard {
    // actualname, changedname,integer
    public Map<String, Pair<String, Integer>> playerNameMap = new HashMap<>();
    public boolean afterFirstSwap = false;
    public int tickDelay = 24000;

    @Override
    public void tick(MinecraftServer server) {
        if (Constants.paused) return;
        if (tickDelay > 0) {
            tickDelay--;
        }

        if (tickDelay == 0) {
            tickDelay = 24000;
            swapPlayerList(server);
        }

    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        WackyPlayerMap wackyPlayerMap = new WackyPlayerMap(playerNameMap);
        ServerPlayNetworking.send(player, wackyPlayerMap);
    }

    @Override
    public void onPlayerLeave(ServerPlayerEntity entity) {
        MinecraftServer server = entity.getEntityWorld().getServer();
        Map<String, Pair<String, Integer>> tempMap = new HashMap<>();

        List<String> playerNames = new ArrayList<>();
        server.getPlayerManager().getPlayerList().forEach(player -> playerNames.add(player.getName().getString()));

        for (String playerName : playerNames) {
            ScoreAccess scoreboard = ScoreboardManager.INSTANCE.getScoreboard(playerName, server, Constants.LivesScoreboard);
            
            int lives = -1;
            
            if (scoreboard != null) {
                lives = scoreboard.getScore();
            }

            tempMap.put(playerName, new Pair<>(playerName, lives));
        }

        ServerPlayNetworking.send(entity, new WackyPlayerMap(tempMap));


    }

    @Override
    public String toString() {
        return "Wacky Skins";
    }

    @Override
    public void deactivate(MinecraftServer server) {
        List<String> playerNames = new ArrayList<>();
        server.getPlayerManager().getPlayerList().forEach(player -> playerNames.add(player.getName().getString()));

        for (String playerName : playerNames) {
            ScoreAccess scoreboard = ScoreboardManager.INSTANCE.getScoreboard(playerName, server, Constants.LivesScoreboard);

            int lives = -1;

            if (scoreboard != null) {
                lives = scoreboard.getScore();
            }

            playerNameMap.put(playerName, new Pair<>(playerName, lives));
        }

        sendtoplayers(server);

    }

    @Override
    public void onActivate(MinecraftServer server) {
        List<String> playerNames = new ArrayList<>();
        PlayerUtils.getActivePlayers(server).forEach(player -> playerNames.add(player.getName().getString()));

        for (String playerName : playerNames) {
            ScoreAccess scoreboard = ScoreboardManager.INSTANCE.getScoreboard(playerName, server, Constants.LivesScoreboard);

            int lives = -1;

            if (scoreboard != null) {
                lives = scoreboard.getScore();
            }

            playerNameMap.put(playerName, new Pair<>(playerName, lives));
        }

        sendtoplayers(server);
    }

    public void swapPlayerList(MinecraftServer server) {
        List<String> playerNames = new ArrayList<>();
        List<ServerPlayerEntity> players = server.getPlayerManager()
                .getPlayerList()
                .stream()
                .filter(p -> ScoreboardManager.INSTANCE.getLives(p, server) > 0)
                .collect(Collectors.toCollection(ArrayList::new));

        players.forEach(player -> playerNames.add(player.getName().getString()));

        Map<String, String> previousTargets = new HashMap<>();
        if (afterFirstSwap) {
            for (String pName : playerNames) {
                if (playerNameMap.containsKey(pName)) {
                    previousTargets.put(pName, playerNameMap.get(pName).getLeft());
                }
            }
        }

        List<Pair<String, Integer>> targetPairs = new ArrayList<>();
        for (String playerName : playerNames) {
            int lives = -1;
            ScoreAccess scoreboard = ScoreboardManager.INSTANCE.getScoreboard(playerName, server, Constants.LivesScoreboard);
            if (scoreboard != null) {
                lives = scoreboard.getScore();
            }
            targetPairs.add(new Pair<>(playerName, lives));
        }

        int maxAttempts = 100;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            Collections.shuffle(targetPairs);

            boolean isValidShuffle = true;

            for (int i = 0; i < playerNames.size(); i++) {
                String currentPlayer = playerNames.get(i);
                String assignedTarget = targetPairs.get(i).getLeft();

                if (playerNames.size() > 1 && currentPlayer.equals(assignedTarget)) {
                    isValidShuffle = false;
                    break;
                }

                String previousTarget = previousTargets.get(currentPlayer);
                if (previousTarget != null && previousTarget.equals(assignedTarget)) {
                    if (playerNames.size() > 2) {
                        isValidShuffle = false;
                        break;
                    }
                }
            }

            if (isValidShuffle) {
                break;
            }
        }

        // 4. Apply the map
        playerNameMap.clear();
        for (int i = 0; i < playerNames.size(); i++) {
            playerNameMap.put(playerNames.get(i), targetPairs.get(i));
        }

        afterFirstSwap = true;
        sendtoplayers(server);
    }

    private void sendtoplayers(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            WackyPlayerMap wackyPlayerMap = new WackyPlayerMap(playerNameMap);
            ServerPlayNetworking.send(player, wackyPlayerMap);

        }
    }
}
