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

public class WackySkins extends Wildcard {
    // actualname, changedname,integer
    public Map<String, Pair<String, Integer>> playerNameMap = new HashMap<>();
    public boolean afterFirstSwap = false;
    public int tickDelay = 24000;

    @Override
    public void tick(MinecraftServer server) {
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

    public void swapPlayerList(MinecraftServer server) {
        List<String> playerNames = new ArrayList<>();
        server.getPlayerManager().getPlayerList().forEach(player -> playerNames.add(player.getName().getString()));

        // Create a list of the existing pairs
        List<Pair<String, Integer>> shuffledPairs = new ArrayList<>();
        for (String playerName : playerNames) {
            int lives = -1;
            ScoreAccess scoreboard = ScoreboardManager.INSTANCE.getScoreboard(playerName, server, Constants.LivesScoreboard);
            if (scoreboard != null) {
                lives = scoreboard.getScore();
            }
            shuffledPairs.add(new Pair<>(playerName, lives));
        }

        // Shuffle the pairs
        Collections.shuffle(shuffledPairs);

        // Clear the current map and reassign
        playerNameMap.clear();
        for (int i = 0; i < playerNames.size(); i++) {
            playerNameMap.put(playerNames.get(i), shuffledPairs.get(i));
        }

        // Avoid mapping a player to themselves if there’s more than one player
        for (int i = 0; i < playerNames.size(); i++) {
            String originalName = playerNames.get(i);
            Pair<String, Integer> assigned = playerNameMap.get(originalName);

            if (assigned.getLeft().equals(originalName) && playerNames.size() > 1) {
                String nextName = playerNames.get((i + 1) % playerNames.size());
                Pair<String, Integer> temp = assigned;
                playerNameMap.put(originalName, playerNameMap.get(nextName));
                playerNameMap.put(nextName, temp);
            }
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
