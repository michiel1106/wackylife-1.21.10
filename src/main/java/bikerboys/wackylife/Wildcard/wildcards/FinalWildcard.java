package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.snail.*;
import bikerboys.wackylife.entity.snail.server.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;

import net.fabricmc.fabric.api.networking.v1.*;

import net.minecraft.entity.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public class FinalWildcard extends Wildcard {

    /*
     * =====================================
     * PHASE SYSTEM
     * =====================================
     */

    public int phase = 0;
    private int phaseTimer = 0;
    private boolean transitioning = false;

    private static final int PHASE_TIME = 29 * 60 * 20;
    private static final int TRANSITION_TIME = 60 * 20;

    private final Random random = new Random();

    /*
     * =====================================
     * CHOICES DATA
     * =====================================
     */

    public List<UUID> processQueue = new ArrayList<>();
    public int choicesTickDelay = 5000;

    /*
     * =====================================
     * SNAILS DATA
     * =====================================
     */

    public static Map<UUID, Snail> snails = new HashMap<>();
    public static Map<UUID, String> snailNames = new HashMap<>();
    long snailTicks = 0;

    /*
     * =====================================
     * WACKY SKINS DATA
     * =====================================
     */

    public Map<String, Pair<String, Integer>> playerNameMap = new HashMap<>();
    public int skinsTickDelay = 5000;

    /*
     * =====================================
     * SWAP DATA
     * =====================================
     */

    public static final int MIN_SWAP_INTERVAL_TICKS = 2000;
    public static final int MAX_SWAP_INTERVAL_TICKS = 6000;

    private int swapTimer = 2000;

    /*
     * =====================================
     * ACTIVATE
     * =====================================
     */

    @Override
    public void onActivate(MinecraftServer server) {
        phase = 0;
        transitioning = false;
        phaseTimer = PHASE_TIME;
        activateCurrentPhase(server);
    }

    @Override
    public void deactivate(MinecraftServer server) {
        deactivateCurrentPhase(server);
    }

    /*
     * =====================================
     * TICK
     * =====================================
     */

    @Override
    public void tick(MinecraftServer server) {

        if (Constants.paused) return;

        phaseTimer--;

        if (!transitioning) {

            runCurrentPhase(server);

            if (phaseTimer <= 0) {
                transitioning = true;
                phaseTimer = TRANSITION_TIME;

                PlayerUtils.sendTitleToPlayers(
                        server.getPlayerManager().getPlayerList(),
                        Text.literal("§r§6§lA wacky twist has faded"),
                        10,120,10
                );

                deactivateCurrentPhase(server);
            }

        } else {

            if (phaseTimer <= 0) {

                phase++;

                if (phase >= 4) return;

                showDots(server);

                TaskScheduler.scheduleTask(90, () -> {

                    PlayerUtils.sendTitleToPlayers(
                            server.getPlayerManager().getPlayerList(),
                            Text.literal("§r§6§lA wacky twist has activated!"),
                            10,120,10
                    );

                    activateCurrentPhase(server);

                });

                transitioning = false;
                phaseTimer = PHASE_TIME;
            }
        }
    }

    private void runCurrentPhase(MinecraftServer server) {
        if (phase == 0) tickChoices(server);
        if (phase == 1) tickSnails(server);
        if (phase == 2) tickWackySkins(server);
        if (phase == 3) tickSwap(server);
    }

    /*
     * =====================================
     * PHASE ACTIVATION / DEACTIVATION
     * =====================================
     */

    private void activateCurrentPhase(MinecraftServer server) {

        if (phase == 0) {
            choicesTickDelay = 5000;
        }

        if (phase == 1) {

            snails.clear();

            for (ServerPlayerEntity player : PlayerUtils.getActivePlayers(server)) {
                if (canHaveSnail(player)) {
                    spawnSnailFor(player);
                }
            }
        }

        if (phase == 2) {

            playerNameMap.clear();

            for (ServerPlayerEntity player : PlayerUtils.getActivePlayers(server)) {

                String name = player.getName().getString();

                ScoreAccess scoreboard =
                        ScoreboardManager.INSTANCE.getScoreboard(
                                name,
                                server,
                                Constants.LivesScoreboard
                        );

                int lives = scoreboard != null ? scoreboard.getScore() : -1;

                playerNameMap.put(name, new Pair<>(name, lives));
            }

            sendWackyMap(server);
        }

        if (phase == 3) {
            swapTimer = getRandomSwapInterval();
        }
    }

    private void deactivateCurrentPhase(MinecraftServer server) {

        if (phase == 0) {
            clearChoices(server);
        }

        if (phase == 1) {
            killAllSnails(server);
        }

        if (phase == 2) {
            playerNameMap.clear();
            sendWackyMap(server);
        }
    }

    /*
     * =====================================
     * CHOICES LOGIC
     * =====================================
     */

    private void tickChoices(MinecraftServer server) {

        if (!processQueue.isEmpty()) {
            sendChoices(server);
            processQueue.clear();
        }

        if (choicesTickDelay > 0) choicesTickDelay--;

        if (choicesTickDelay == 0) {

            choicesTickDelay = 5000;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (ScoreboardManager.INSTANCE.getLives(player, server) >= 1) {
                    processQueue.add(player.getUuid());
                }
            }
        }
    }

    private void sendChoices(MinecraftServer server) {

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(
                    player,
                    new OpenChoicesScreen(
                            ChoiceRegistry.getRandomChoicePairs()
                    )
            );
        }
    }

    private void clearChoices(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ChoiceManager.clearChoices(player);
        }
    }

    /*
     * =====================================
     * SNAILS LOGIC
     * =====================================
     */

    private void tickSnails(MinecraftServer server) {

        snailTicks++;

        if (snailTicks % 100 != 0) return;

        for (ServerPlayerEntity player :
                server.getPlayerManager()
                        .getPlayerList()
                        .stream()
                        .filter(p -> !ScoreboardManager.INSTANCE.isDead(p))
                        .toList()) {

            if (!canHaveSnail(player)) continue;

            UUID uuid = player.getUuid();

            if (snails.containsKey(uuid)) {

                Snail snail = snails.get(uuid);

                if (snail == null || !snail.isAlive()) {
                    snails.remove(uuid);
                    spawnSnailFor(player);
                }

            } else {
                spawnSnailFor(player);
            }
        }
    }

    public static boolean canHaveSnail(ServerPlayerEntity player) {
        if (player.isCreative()) return false;
        if (!player.isAlive()) return false;
        if (player.isSpectator()) return false;
        return true;
    }

    public static void spawnSnailFor(ServerPlayerEntity player) {

        BlockPos pos = SnailPathfinding.getBlockPosNearPlayer(player, 30);

        if (pos == null) pos = player.getBlockPos().add(0, 30, 0);

        Snail snail = LevelUtils.spawnEntity(
                ModEntities.SNAIL,
                player.getEntityWorld(),
                pos
        );

        if (snail != null) {
            snail.serverData.setBoundPlayer(player);
            snails.put(player.getUuid(), snail);
        }
    }

    public static void killAllSnails(MinecraftServer server) {

        List<Entity> toKill = new ArrayList<>();

        for (ServerWorld world : server.getWorlds()) {
            for (Entity entity : world.iterateEntities()) {
                if (entity instanceof Snail snail && !snail.isFromTrivia()) {
                    toKill.add(entity);
                }
            }
        }

        toKill.forEach(Entity::discard);
    }

    /*
     * =====================================
     * WACKY SKINS LOGIC (NO SELF MAP)
     * =====================================
     */

    private void tickWackySkins(MinecraftServer server) {

        if (skinsTickDelay > 0) skinsTickDelay--;

        if (skinsTickDelay == 0) {
            skinsTickDelay = 4500;
            swapPlayerList(server);
        }
    }

    private void swapPlayerList(MinecraftServer server) {

        List<ServerPlayerEntity> players =
                server.getPlayerManager()
                        .getPlayerList()
                        .stream()
                        .filter(p -> ScoreboardManager.INSTANCE.getLives(p, server) > 0)
                        .toList();

        if (players.size() < 2) return;

        List<String> names = players.stream()
                .map(p -> p.getName().getString())
                .toList();

        List<Pair<String,Integer>> targets = new ArrayList<>();

        for (String name : names) {

            ScoreAccess scoreboard =
                    ScoreboardManager.INSTANCE.getScoreboard(
                            name,
                            server,
                            Constants.LivesScoreboard
                    );

            int lives = scoreboard != null ? scoreboard.getScore() : -1;

            targets.add(new Pair<>(name, lives));
        }

        boolean valid = false;

        while (!valid) {

            Collections.shuffle(targets);
            valid = true;

            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).equals(targets.get(i).getLeft())) {
                    valid = false;
                    break;
                }
            }
        }

        playerNameMap.clear();

        for (int i = 0; i < names.size(); i++) {
            playerNameMap.put(names.get(i), targets.get(i));
        }

        sendWackyMap(server);
    }

    private void sendWackyMap(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new WackyPlayerMap(playerNameMap));
        }
    }

    /*
     * =====================================
     * SWAP LOGIC (NO SAME POSITION)
     * =====================================
     */

    private void tickSwap(MinecraftServer server) {

        swapTimer--;

        if (swapTimer <= 0) {
            swapRandomPlayers(server);
            swapTimer = getRandomSwapInterval();
        }
    }

    private void swapRandomPlayers(MinecraftServer server) {

        List<ServerPlayerEntity> players =
                server.getPlayerManager()
                        .getPlayerList()
                        .stream()
                        .filter(p -> !ScoreboardManager.INSTANCE.isDead(p))
                        .toList();

        if (players.size() < 2) return;

        List<Vec3d> positions =
                players.stream().map(ServerPlayerEntity::getEntityPos).toList();

        List<Vec3d> shuffled = new ArrayList<>(positions);

        boolean valid = false;

        while (!valid) {

            Collections.shuffle(shuffled);
            valid = true;

            for (int i = 0; i < positions.size(); i++) {
                if (positions.get(i).equals(shuffled.get(i))) {
                    valid = false;
                    break;
                }
            }
        }

        for (int i = 0; i < players.size(); i++) {

            ServerPlayerEntity player = players.get(i);
            Vec3d pos = shuffled.get(i);

            player.teleport(
                    player.getEntityWorld(),
                    pos.x, pos.y, pos.z,
                    EnumSet.noneOf(PositionFlag.class),
                    player.getYaw(),
                    player.getPitch(),
                    false
            );
        }
    }

    private int getRandomSwapInterval() {
        return MIN_SWAP_INTERVAL_TICKS +
                random.nextInt(MAX_SWAP_INTERVAL_TICKS - MIN_SWAP_INTERVAL_TICKS);
    }

    /*
     * =====================================
     * DOT ANIMATION
     * =====================================
     */

    private void showDots(MinecraftServer server) {

        List<ServerPlayerEntity> players =
                server.getPlayerManager().getPlayerList();

        playDot(players, "§a§l,");
        TaskScheduler.scheduleTask(30,
                () -> playDot(players, "§a§l, §e§l,"));
        TaskScheduler.scheduleTask(60,
                () -> playDot(players, "§a§l, §e§l, §c§l,"));
    }

    private void playDot(List<ServerPlayerEntity> players, String text) {

        PlayerUtils.playSoundToPlayers(
                players,
                SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),
                0.4f, 1
        );

        PlayerUtils.sendTitleToPlayers(
                players,
                Text.literal(text),
                0, 40, 0
        );
    }

    @Override
    public String toString() {
        return "Wacky Phases";
    }
}