package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.networking.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

import java.util.*;

public class WackySkins extends Wildcard {
    // actualname, changedname
    public Map<String, String> playerNameMap = new HashMap<>();
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
    public void deactivate(MinecraftServer server) {
        List<String> playerNames = new ArrayList<>();
        server.getPlayerManager().getPlayerList().forEach(player -> playerNames.add(player.getName().getString()));

        for (String playerName : playerNames) {
            playerNameMap.put(playerName, playerName);
        }

        sendtoplayers(server);




    }

    public void swapPlayerList(MinecraftServer server) {
        List<String> playerNames = new ArrayList<>();
        server.getPlayerManager().getPlayerList().forEach(player -> playerNames.add(player.getName().getString()));

        List<String> shuffledNames = new ArrayList<>(playerNames);
        Collections.shuffle(shuffledNames);

        playerNameMap.clear();

        for (int i = 0; i < playerNames.size(); i++) {
            playerNameMap.put(playerNames.get(i), shuffledNames.get(i));
        }

        for (int i = 0; i < playerNames.size(); i++) {
            if (playerNames.get(i).equals(playerNameMap.get(playerNames.get(i))) && playerNames.size() > 1) {
                String temp = playerNameMap.get(playerNames.get(i));
                String next = playerNames.get((i + 1) % playerNames.size());
                playerNameMap.put(playerNames.get(i), playerNameMap.get(next));
                playerNameMap.put(next, temp);
            }
        }

        sendtoplayers(server);


    }

    private void sendtoplayers(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            WackyPlayerMap wackyPlayerMap = new WackyPlayerMap(playerNameMap);
            ServerPlayNetworking.send(player, wackyPlayerMap);

        }
    }
}
