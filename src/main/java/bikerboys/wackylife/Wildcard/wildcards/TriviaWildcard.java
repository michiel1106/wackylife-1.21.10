package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.Wildcard;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.triviabot.TriviaBot;
import bikerboys.wackylife.entity.triviabot.server.trivia.*;
import net.minecraft.entity.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

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

    // --- Question Managers ---
    public static TriviaQuestionManager easyTrivia;
    public static TriviaQuestionManager normalTrivia;
    public static TriviaQuestionManager hardTrivia;

    // Tracking used questions to avoid repeats
    private static final List<String> usedEasyQuestions = new ArrayList<>();
    private static final List<String> usedNormalQuestions = new ArrayList<>();
    private static final List<String> usedHardQuestions = new ArrayList<>();

    @Override
    public void onActivate(MinecraftServer server) {
        // Reset State
        activeBots.clear();
        botsSpawnedCount.clear();
        resetQuestionState();
        tickCounter = 0;

        // Initialize Question Managers
        easyTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "easy-trivia.json");
        normalTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "normal-trivia.json");
        hardTrivia = new TriviaQuestionManager("./config/lifeseries/wildlife", "hard-trivia.json");

        broadcast(server, "§7Trivia Bots have been activated! Watch your step.");
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

        // Reset all players
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            resetPlayerOnBotSpawn(player);
        }

        activeBots.clear();
        botsSpawnedCount.clear();
        resetQuestionState();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        // Optional: If they log in during the wildcard, ensure they are clean
        if (activeBots.containsKey(player.getUuid())) {
            TriviaBot bot = activeBots.get(player.getUuid());
            if (bot == null || !bot.isAlive()) {
                activeBots.remove(player.getUuid());
            }
        }
    }

    @Override
    public void onPlayerLeave(ServerPlayerEntity player) {
        // Optional: Kill their bot if they leave?
        // keeping it simple: do nothing, let tick cleanup handle it if the bot dies/despawns
    }

    @Override
    public String toString() {
        return "Trivia Bots";
    }

    // --- Core Logic ---

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

    public void spawnBotFor(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();
        // Simple spawn logic: Spawn near player (offset Y slightly up to avoid stuck in floor)
        BlockPos spawnPos = player.getBlockPos().up();

        TriviaBot bot = ModEntities.TRIVIA_BOT.create(player.getEntityWorld(), SpawnReason.COMMAND); // You might need to adjust this constructor based on your entity class

        if (bot != null) {
            bot.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
        }

        if (bot != null) {
            world.spawnEntity(bot);

            // Setup Bot
            bot.setBoundPlayer(player); // Assuming this method exists in TriviaBot
            activeBots.put(player.getUuid(), bot);

            // Effects
            player.playSoundToPlayer(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.MASTER, 0.5f, 1);

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
            // In Yarn, iterating entities can be tricky depending on version.
            // This is a safe generic approach:
            for (Entity entity : world.getEntitiesByType(null, e -> e instanceof TriviaBot)) {
                toKill.add(entity);
            }
            toKill.forEach(Entity::discard);
        }
    }

    // --- Helper Logic (Questions & Player Reset) ---

    public static void resetPlayerOnBotSpawn(ServerPlayerEntity player) {
        // Despawn existing bot if alive
        if (activeBots.containsKey(player.getUuid())) {
            TriviaBot bot = activeBots.get(player.getUuid());
            if (bot != null && bot.isAlive()) {
                bot.discard();
            }
        }

        // Reset Player "Curses" (Punishments)
        // Assuming AttributeUtils/SizeShifting are accessible util classes
        // If these classes don't exist in the simplified version, remove these lines.
        /*
        SizeShifting.setPlayerSize(player, 1.0f);
        AttributeUtils.resetMaxPlayerHealth(player);
        AttributeUtils.resetPlayerJumpHeight(player);
        */

        // Notify Client to reset UI
        // ServerPlayNetworking.send(player, new ResetTriviaPayload(true));
    }

    private void resetQuestionState() {
        usedEasyQuestions.clear();
        usedNormalQuestions.clear();
        usedHardQuestions.clear();
    }

    private void broadcast(MinecraftServer server, String message) {
        server.getPlayerManager().broadcast(Text.of(message), false);
    }

    // --- Question Getter (Called by TriviaBot entity) ---

    public static bikerboys.wackylife.entity.triviabot.server.trivia.TriviaQuestion getTriviaQuestion(ServerPlayerEntity player) {
        int difficulty = 1 + rnd.nextInt(3); // 1, 2, or 3
        try {
            if (difficulty == 1) return getQuestionFromManager(easyTrivia, usedEasyQuestions);
            if (difficulty == 2) return getQuestionFromManager(normalTrivia, usedNormalQuestions);
            return getQuestionFromManager(hardTrivia, usedHardQuestions);
        } catch (Exception e) {
            e.printStackTrace();
            return TriviaQuestion.getDefault(); // Ensure you have a fallback
        }
    }

    private static TriviaQuestion getQuestionFromManager(TriviaQuestionManager manager, List<String> usedList) throws IOException {
        if (manager == null) return TriviaQuestion.getDefault();

        List<TriviaQuestion> available = new ArrayList<>();
        for (TriviaQuestion q : manager.getTriviaQuestions()) {
            if (!usedList.contains(q.getQuestion())) {
                available.add(q);
            }
        }

        // If we ran out of unique questions, clear history and allow repeats
        if (available.isEmpty()) {
            usedList.clear();
            available.addAll(manager.getTriviaQuestions());
        }

        if (available.isEmpty()) return TriviaQuestion.getDefault();

        TriviaQuestion selected = available.get(rnd.nextInt(available.size()));
        usedList.add(selected.getQuestion());
        return selected;
    }
}