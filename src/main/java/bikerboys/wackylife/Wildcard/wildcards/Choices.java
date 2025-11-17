package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

import java.util.*;

public class Choices extends Wildcard {

    public List<UUID> processQueue = new ArrayList<>();
    public int tickDelay = 30000;

    @Override
    public void tick(MinecraftServer server) {
        if (!processQueue.isEmpty()) {
            processPlayers(server);
        }
        if (tickDelay > 0) {
            tickDelay--;
        }

        if (tickDelay == 0) {
            tickDelay = 30000;
            execute(server);
        }

    }

    private void processPlayers(MinecraftServer server) {
        sendChoices(server);
        processQueue.clear();
    }

    public void sendChoices(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new OpenChoicesScreen(ChoiceRegistry.getRandomChoicePairs()));
        }
    }

    public void playerChose(ServerPlayerEntity player, ChoicePair choicePair) {
        Choice posChoice = ChoiceRegistry.get(choicePair.positiveChoice());
        Choice negChoice = ChoiceRegistry.get(choicePair.negativeChoice());
        ChoiceManager.setChoice(player, posChoice, negChoice);
    }

    @Override
    public String toString() {
        return "Choices";
    }

    @Override
    public void deactivate(MinecraftServer server) {
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : playerList) {
            ChoiceManager.clearChoices(player);
        }
    }

    public void execute(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (ScoreboardManager.INSTANCE.getLives(player, server) >= 1) {
                processQueue.add(player.getUuid());
            }
        }
    }
}
