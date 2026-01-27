package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.Wildcard;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.triviabot.TriviaBot;

import bikerboys.wackylife.entity.triviabot.server.*;
import bikerboys.wackylife.entity.triviabot.trivia.*;
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
import java.util.stream.*;

public class TriviaWildcard extends Wildcard {

    // --- Configuration ---
    public static final int BOTS_PER_PLAYER = 5;
    public static final int MIN_SPAWN_INTERVAL_TICKS = 5000;  // 250 seconds
    public static final int MAX_SPAWN_INTERVAL_TICKS = 8000;  // 400 seconds
    private static final Random rnd = new Random();

    // --- State ---
    public static final Map<UUID, TriviaBot> activeBots = new HashMap<>();
    private static final Map<UUID, Integer> botsSpawnedCount = new HashMap<>();
    private static final Map<UUID, Integer> playerSpawnTimers = new HashMap<>();
    private static final Map<UUID, Integer> playerSpawnIntervals = new HashMap<>();

    // Cleanup counter (not per-player)
    private int cleanupCounter = 0;



    @Override
    public void onActivate(MinecraftServer server) {
        activeBots.clear();
        botsSpawnedCount.clear();
        playerSpawnTimers.clear();
        playerSpawnIntervals.clear();
        cleanupCounter = 0;
        resetQuestionState();
    }

    @Override
    public void tick(MinecraftServer server) {
        // 1. Cleanup Dead Bots from Map (every 10 seconds)
        if (Constants.paused) return;
        cleanupCounter++;
        if (cleanupCounter >= 200) {
            cleanupCounter = 0;
            cleanupDeadBots();
        }

        List<ServerPlayerEntity> players = server.getPlayerManager()
                .getPlayerList()
                .stream()
                .filter(p -> ScoreboardManager.INSTANCE.getLives(p, server) > 0)
                .collect(Collectors.toCollection(ArrayList::new));

        // 2. Update per-player spawn timers
        for (ServerPlayerEntity player : players) {
            UUID uuid = player.getUuid();

            // Initialize timers for new players
            if (!playerSpawnTimers.containsKey(uuid)) {
                playerSpawnTimers.put(uuid, 0);
                playerSpawnIntervals.put(uuid, getRandomSpawnInterval());
            }

            // Skip spectators
            if (player.isSpectator()) continue;

            // Increment this player's timer
            int currentTimer = playerSpawnTimers.get(uuid) + 1;
            playerSpawnTimers.put(uuid, currentTimer);

            int spawnInterval = playerSpawnIntervals.get(uuid);

            // Check if it's time to spawn for this player
            if (currentTimer >= spawnInterval) {
                int currentCount = botsSpawnedCount.getOrDefault(uuid, 0);

                // If they haven't reached the cap and don't have an active bot
                if (currentCount < BOTS_PER_PLAYER) {
                    if (!activeBots.containsKey(uuid) || !activeBots.get(uuid).isAlive()) {
                        spawnBotFor(player, QuestionManager.getRandomQuestion());
                        botsSpawnedCount.put(uuid, currentCount + 1);
                    }
                }

                // Reset timer and generate new interval for this player
                playerSpawnTimers.put(uuid, 0);
                playerSpawnIntervals.put(uuid, getRandomSpawnInterval());
            }
        }
    }

    @Override
    public void deactivate(MinecraftServer server) {
        killAllBots(server);

        List<ServerPlayerEntity> players = server.getPlayerManager()
                .getPlayerList()
                .stream()
                .filter(p -> ScoreboardManager.INSTANCE.getLives(p, server) > 0)
                .collect(Collectors.toCollection(ArrayList::new));

        for (ServerPlayerEntity player : players) {
            resetPlayerOnBotSpawn(player);
        }

        activeBots.clear();
        botsSpawnedCount.clear();
        playerSpawnTimers.clear();
        playerSpawnIntervals.clear();
        resetQuestionState();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        // Initialize timers for joining player
        if (!playerSpawnTimers.containsKey(uuid)) {
            playerSpawnTimers.put(uuid, 0);
            playerSpawnIntervals.put(uuid, getRandomSpawnInterval());
        }

        if (activeBots.containsKey(uuid)) {
            TriviaBot bot = activeBots.get(uuid);
            if (bot == null || !bot.isAlive()) {
                activeBots.remove(uuid);
            }
        }
    }

    @Override
    public void onPlayerLeave(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        playerSpawnTimers.remove(uuid);
        playerSpawnIntervals.remove(uuid);
    }

    @Override
    public String toString() {
        return "Trivia Bots";
    }

    private static int getRandomSpawnInterval() {
        return MIN_SPAWN_INTERVAL_TICKS + rnd.nextInt(MAX_SPAWN_INTERVAL_TICKS - MIN_SPAWN_INTERVAL_TICKS + 1);
    }

    public static void spawnBotFor(ServerPlayerEntity player) {
        spawnBotFor(player, TriviaBotPathfinding.getBlockPosNearPlayer(player, player.getBlockPos().add(0, 50, 0), 10));
    }

    public static void spawnBotFor(ServerPlayerEntity player, Question question) {
        spawnBotFor(player, TriviaBotPathfinding.getBlockPosNearPlayer(player, player.getBlockPos().add(0, 50, 0), 10), question);
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

    public static void spawnBotFor(ServerPlayerEntity player, BlockPos pos, Question question) {
        resetPlayerOnBotSpawn(player);

        ServerWorld world = player.getEntityWorld();

        TriviaBot bot = ModEntities.TRIVIA_BOT.create(world, SpawnReason.COMMAND);

        if (bot != null) {
            bot.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            bot.serverData.setBoundPlayer(player);
            bot.setQuestion(question);
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

    public int getTickCounter(ServerPlayerEntity player) {
        return playerSpawnTimers.get(player.getUuid());
    }

    public void setTickCounter(ServerPlayerEntity player ,int amount) {
        playerSpawnTimers.put(player.getUuid(), amount);
    }
}