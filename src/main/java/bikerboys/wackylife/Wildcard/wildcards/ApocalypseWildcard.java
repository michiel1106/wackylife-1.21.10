package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.event.*;
import bikerboys.wackylife.util.*;
import dev.architectury.event.events.common.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

import java.util.*;

public class ApocalypseWildcard extends Wildcard {
    public Random random = new Random();
    public List<UUID> zombies = new ArrayList<>();

    @Override
    public String toString() {
        return "Apocalypse";
    }

    @Override
    public void onActivate(MinecraftServer server) {
        TaskScheduler.scheduleTask(5*60*20, () -> {
            List<ServerPlayerEntity> activePlayers = PlayerUtils.getActivePlayers(server);

            ServerPlayerEntity player = activePlayers.get(random.nextInt(activePlayers.size()));
            zombies.add(player.getUuid());

            PlayerUtils.sendTitleWithSubtitle(player, Text.literal("You are a zombie.").formatted(Formatting.DARK_GREEN), Text.literal("Infect everyone or face the consequences"), 20, 60, 20);
            player.sendMessage(Text.literal("""
                    You are a Zombie, infect people by killing them, reds do not count. Infecting everyone will result in
                    you gaining a life, failing to do so will result in a life being given to the people who are left alive.
                    """).formatted(Formatting.GRAY));
        });
    }

    @Override
    public void onPlayerKill(PlayerEntity predator, PlayerEntity prey, MinecraftServer server) {
        if (zombies.contains(predator.getUuid())) {
            if (ScoreboardManager.INSTANCE.getLives(prey, server) >= 2) {
                zombies.add(prey.getUuid());

                predator.sendMessage(Text.literal("Successfully infected someone."), true);

                prey.sendMessage(Text.literal("""
                    You are a Zombie, infect people by killing them, reds do not count. Infecting everyone will result in
                    you gaining a life, failing to do so will result in a life being given to the people who are left alive.
                    """).formatted(Formatting.GRAY), false);
            }
        }
    }

    @Override
    public void deactivate(MinecraftServer server) {
        List<ServerPlayerEntity> activePlayers = PlayerUtils.getActivePlayers(server);

        List<ServerPlayerEntity> alivePlayers = new ArrayList<>();
        for (ServerPlayerEntity player : activePlayers) {
            if (ScoreboardManager.INSTANCE.getLives(player, server) > 1) {
                alivePlayers.add(player);
            }
        }

        boolean allInfected = true;

        for (ServerPlayerEntity player : alivePlayers) {
            if (!zombies.contains(player.getUuid())) {
                allInfected = false;
                break;
            }
        }

        if (allInfected && !alivePlayers.isEmpty()) {
            zombiesWon(server);
        } else {
            nonZombiesWon(server);
        }

        zombies.clear();
    }


    public void zombiesWon(MinecraftServer server) {
        for (UUID uuid : zombies) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                ScoreboardManager.INSTANCE.incrementLives(player, server);
                player.sendMessage(Text.literal("All players infected. You gained 1 life!")
                        .formatted(Formatting.DARK_GREEN));
            }
        }
    }

    public void nonZombiesWon(MinecraftServer server) {
        List<ServerPlayerEntity> activePlayers = PlayerUtils.getActivePlayers(server);

        for (ServerPlayerEntity player : activePlayers) {
            if (ScoreboardManager.INSTANCE.getLives(player, server) > 0
                    && !zombies.contains(player.getUuid())) {

                ScoreboardManager.INSTANCE.incrementLives(player, server);

                player.sendMessage(Text.literal("You survived the apocalypse and gained 1 life!")
                        .formatted(Formatting.GOLD));
            }
        }
    }

}











































