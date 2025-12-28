package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.Wildcard;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.triviabot.TriviaBot;

import bikerboys.wackylife.entity.triviabot.server.*;
import bikerboys.wackylife.mixin.*;
import bikerboys.wackylife.util.*;
import de.maxhenkel.voicechat.api.*;
import net.minecraft.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.core.jmx.*;

import java.io.IOException;
import java.util.*;

public class TriviaWildcard extends Wildcard {

    // --- Configuration ---
    public static final int BOTS_PER_PLAYER = 5;
    public static final int SPAWN_INTERVAL_TICKS = 600; // Try spawn every 30 seconds
    private int tickCounter = 0;

    // --- State ---
    public static final Map<UUID, TriviaBot> activeBots = new HashMap<>();
    private static final Map<UUID, Integer> botsSpawnedCount = new HashMap<>();
    private static final Random rnd = new Random();



    @Override
    public void onActivate(MinecraftServer server) {
        activeBots.clear();
        botsSpawnedCount.clear();
        resetQuestionState();
        tickCounter = 0;
    }

    @Override
    public void tick(MinecraftServer server) {
        tickCounter++;

        // 1. Cleanup Dead Bots from Map
        if (tickCounter % 200 == 0) { // Every 10 seconds
            cleanupDeadBots();
        }

        // 2. Try Spawning Bots
        if (tickCounter >= SPAWN_INTERVAL_TICKS) {
            tickCounter = 0;
            trySpawnBots(server);
        }
    }

    @Override
    public void deactivate(MinecraftServer server) {
        killAllBots(server);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            resetPlayerOnBotSpawn(player);
        }

        activeBots.clear();
        botsSpawnedCount.clear();
        resetQuestionState();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {

        if (activeBots.containsKey(player.getUuid())) {
            TriviaBot bot = activeBots.get(player.getUuid());
            if (bot == null || !bot.isAlive()) {
                activeBots.remove(player.getUuid());
            }
        }
    }

    @Override
    public void onPlayerLeave(ServerPlayerEntity player) {

    }

    @Override
    public String toString() {
        return "Trivia Bots";
    }


    private void trySpawnBots(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // Check if player is alive/valid (Spectators shouldn't get bots)
            if (player.isSpectator()) continue;

            UUID uuid = player.getUuid();
            int currentCount = botsSpawnedCount.getOrDefault(uuid, 0);

            // If they haven't reached the cap
            if (currentCount < BOTS_PER_PLAYER) {
                // If they don't currently have an active bot haunting them
                if (!activeBots.containsKey(uuid) || !activeBots.get(uuid).isAlive()) {
                    spawnBotFor(player);
                    botsSpawnedCount.put(uuid, currentCount + 1);
                }
            }
        }
    }

    public static void spawnBotFor(ServerPlayerEntity player) {
        spawnBotFor(player, TriviaBotPathfinding.getBlockPosNearPlayer(player, player.getBlockPos().add(0, 50, 0), 10));
    }

    public static void handleAnswer(ServerPlayerEntity player, int answer) {
        if (activeBots.containsKey(player.getUuid())) {
            TriviaBot bot = activeBots.get(player.getUuid());
            if (bot.isAlive()) {
                bot.triviaHandler.handleAnswer(answer);
            }
        }

    }

    public static void spawnBotFor(ServerPlayerEntity player, BlockPos pos) {
        resetPlayerOnBotSpawn(player);

        ServerWorld world = player.getEntityWorld();

        TriviaBot bot = ModEntities.TRIVIA_BOT.create(world, SpawnReason.COMMAND);

        if (bot != null) {
            bot.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            bot.serverData.setBoundPlayer(player);
            world.spawnEntity(bot);

            activeBots.put(player.getUuid(), bot);

            // Sound Notification
            ((IServerPlayer) player).ls$playNotifySound(
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                    SoundCategory.MASTER,
                    0.5f,
                    1
            );
        }
    }

    private void cleanupDeadBots() {
        Iterator<Map.Entry<UUID, TriviaBot>> iterator = activeBots.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, TriviaBot> entry = iterator.next();
            if (entry.getValue() == null || !entry.getValue().isAlive()) {
                iterator.remove();
            }
        }
    }

    public void killAllBots(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            List<Entity> toKill = new ArrayList<>();
            for (Entity entity : world.getEntitiesByType(ModEntities.TRIVIA_BOT, e -> e instanceof TriviaBot)) {
                toKill.add(entity);
            }
            toKill.forEach(Entity::discard);
        }
    }


    public static void resetPlayerOnBotSpawn(ServerPlayerEntity player) {
        if (activeBots.containsKey(player.getUuid())) {
            TriviaBot bot = activeBots.get(player.getUuid());
            if (bot != null && bot.isAlive()) {
                bot.discard();
            }
        }

    }

    private void resetQuestionState() {

    }

    private void broadcast(MinecraftServer server, String message) {
        server.getPlayerManager().broadcast(Text.of(message), false);
    }

}