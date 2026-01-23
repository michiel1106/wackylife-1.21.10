package bikerboys.wackylife.series;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.manager.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import bikerboys.wackylife.wyr.choice.*;
import dev.architectury.event.*;
import dev.architectury.event.events.common.*;
import net.fabricmc.fabric.api.entity.event.v1.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import java.util.*;

public class WackySeries {
    private final DeathsManager deathsManager = new DeathsManager(this);
    private Wildcard wildcard;

    public WackySeries() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
        PlayerEvent.PLAYER_JOIN.register(this::playerJoin);
        ServerPlayerEvents.LEAVE.register(this::playerLeave);
        EntityEvent.LIVING_DEATH.register(this::onPlayerDeath);
    }

    private void playerLeave(ServerPlayerEntity player) {
        if (this.wildcard != null) {
            this.wildcard.onPlayerLeave(player);
        }
    }

    private void playerJoin(ServerPlayerEntity player) {
        if (this.wildcard != null) {
            this.wildcard.onPlayerJoin(player);
        }


    }

    public DeathsManager getDeathsManager() {
        return deathsManager;
    }

    public void initialize(World world) {
        if (world.getServer() == null) return;

        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            start(world, player);
        }
    }

    public void start(World world, ServerPlayerEntity player) {
        ScoreAccess scoreAccess = ScoreboardManager.INSTANCE.getScoreboard(player, world.getServer(), Constants.LivesScoreboard);
        if (scoreAccess == null) {
            ServerScoreboard scoreboard = world.getServer().getScoreboard();
            ScoreboardObjective objective = scoreboard.getNullableObjective(Constants.LivesScoreboard);
            if (objective != null) {
                ScoreAccess newScore = scoreboard.getOrCreateScore(player, objective);
                newScore.setScore(6);
                player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("6").formatted(Formatting.DARK_GREEN)));
            }
        }
    }

    public EventResult onPlayerDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (Constants.paused) return EventResult.pass();

        if (livingEntity instanceof ServerPlayerEntity player) {
            MinecraftServer server = player.getEntityWorld().getServer();
            Entity attacker = damageSource.getAttacker();

            if (attacker instanceof PlayerEntity predator) {
                onPlayerKill(predator, player, server);
            }

            if (damageSource.isOf(DamageTypes.FALLING_ANVIL)
                    || (damageSource.isOf(DamageTypes.EXPLOSION)
                    && damageSource.getSource() instanceof TntMinecartEntity)) {
                deathsManager.registerDeath(player);



                for (ServerPlayerEntity online : server.getPlayerManager().getPlayerList()) {
                    online.sendMessage(Text.literal("§c" + player.getName().getString() +
                            " §7was killed! §eUse §a/wackylife claimkill " +
                            player.getName().getString() + " §eto claim the kill."), false);
                }

            }

            ScoreboardManager.INSTANCE.decrementScoreboard(player, server, Constants.LivesScoreboard);

            int lives = ScoreboardManager.INSTANCE.getLives(player, server);
            if (lives == 0) {
                player.changeGameMode(GameMode.SPECTATOR);

                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(livingEntity.getEntityWorld(), SpawnReason.COMMAND);
                if (lightningEntity != null) {
                    lightningEntity.setPosition(player.getX(), player.getY(), player.getZ());
                    lightningEntity.setCosmetic(true);
                    livingEntity.getEntityWorld().spawnEntity(lightningEntity);
                }
            }
        }

        return EventResult.pass();
    }


    private void tick(MinecraftServer server) {

        boolean wackyskinsactive = false;
        if (this.wildcard instanceof WackySkins) {
            wackyskinsactive = true;
        }

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new CurrentSessionTime(ScoreboardManager.INSTANCE.getTime(server)));
            ServerPlayNetworking.send(player, new WackySkinsActive(wackyskinsactive));
        }


        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        if (Constants.paused) {
            List<String> allNames = players.stream()
                    .map(p -> p.getName().getString())
                    .toList();

            for (ServerPlayerEntity player : players) {
                ServerPlayNetworking.send(player, new AlivePlayerList(allNames));
            }

            return;
        }

        if (wildcard != null) {
            wildcard.tick(server);
        }

        decrementTime(server);
        updateData(server);
    }

    private void decrementTime(MinecraftServer server) {
        int time = ScoreboardManager.INSTANCE.getTime(server);
        if (time > 0) {
            ScoreboardManager.INSTANCE.decrementTime(server);
        } else if (time == 0){
            endSession(server);
        }

    }



    private void updateData(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

        for (ServerPlayerEntity player : players) {
            int lives = ScoreboardManager.INSTANCE.getLives(player, server);
            ServerPlayNetworking.send(player, new LivesAmountUpdate(lives));
        }

        List<String> aliveNames = players.stream()
                .filter(p -> ScoreboardManager.INSTANCE.getLives(p, server) >= 1)
                .map(p -> p.getName().getString())
                .toList();

        List<String> allNames = players.stream()
                .map(p -> p.getName().getString())
                .toList();

        for (ServerPlayerEntity player : players) {
            int lives = ScoreboardManager.INSTANCE.getLives(player, server);
            if (lives >= 1) {
                ServerPlayNetworking.send(player, new AlivePlayerList(aliveNames));
            } else {
                ServerPlayNetworking.send(player, new AlivePlayerList(allNames));
            }
        }


    }

    private void endSession(MinecraftServer server) {

        if (this.wildcard != null) {
            this.wildcard.deactivate(server);
        }

        PlayerUtils.sendTitleToPlayers(server.getPlayerManager().getPlayerList(), Text.literal("End of session!").formatted(Formatting.GRAY), 12, 45, 12);
        PlayerUtils.applyToAll(server, (player -> ChoiceManager.setChoice(player, ChoiceRegistry.get("empty_pos"), ChoiceRegistry.get("empty_neg"))));

        Constants.paused = true;

    }

    public void setWildcard(String wildcard, MinecraftServer server) {
        Wildcard wildcard1 = WildcardEnum.getWildcard(wildcard);


        if (wildcard1 != null) {

            activateWildcard(server, wildcard1);
        }
    }

    public String getWildcard() {
        if (wildcard != null) {


            return wildcard.toString();
        }
        return null;
    }

    public static void activateWildcard(MinecraftServer server, Wildcard wildcard1) {
        showDots(server);
        TaskScheduler.scheduleTask(90, () -> {

          //  if (activeWildcards.isEmpty()) {
           //     chooseRandomWildcard();
            //}

            showCryptTitle("A wildcard is active!", server, wildcard1);
        });
    }

    public static void showDots(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
        PlayerUtils.sendTitleToPlayers(players, Text.literal("§a§l,"),0,40,0);
        TaskScheduler.scheduleTask(30, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            PlayerUtils.sendTitleToPlayers(players, Text.literal("§a§l, §e§l,"),0,40,0);
        });
        TaskScheduler.scheduleTask(60, () -> {
            PlayerUtils.playSoundToPlayers(players, SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(), 0.4f, 1);
            PlayerUtils.sendTitleToPlayers(players, Text.literal("§a§l, §e§l, §c§l,"),0,40,0);
        });
    }

    public static void showCryptTitle(String text, MinecraftServer server, Wildcard wildcard1) {
        PlayerUtils.playSoundToPlayers(server.getPlayerManager().getPlayerList(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.2f, 1);
        if (WackyLife.wackyLife.wildcard != null && wildcard1 != null) {
            WackyLife.wackyLife.wildcard.deactivate(server);
        }
        WackyLife.wackyLife.wildcard = wildcard1;
        WackyLife.wackyLife.wildcard.onActivate(server);


        String colorCrypt = "§r§6§l§k";
        String colorNormal = "§r§6§l";
        Random random = new Random();

        List<Integer> encryptedIndexes = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            encryptedIndexes.add(i);
        }

        for (int i = 0; i < text.length(); i++) {
            if (!encryptedIndexes.isEmpty()) {
                encryptedIndexes.remove(random.nextInt(encryptedIndexes.size()));
            }

            StringBuilder result = new StringBuilder();
            for (int j = 0; j < text.length(); j++) {
                result.append(encryptedIndexes.contains(j) ? colorCrypt : colorNormal);
                result.append(text.charAt(j));
            }

            TaskScheduler.scheduleTask((i + 1) * 4, () -> PlayerUtils.sendTitleToPlayers(server.getPlayerManager().getPlayerList(), Text.literal(String.valueOf(result)), 0, 30, 20));
        }
    }

    public Wildcard getWildcardObj() {
        if (wildcard != null) {
            return wildcard;
        }
        return null;
    }


    public void onPlayerKill(PlayerEntity predator, PlayerEntity prey, MinecraftServer server) {
        if (Constants.paused) return;

        int predatorLives = ScoreboardManager.INSTANCE.getLives(predator, server);
        int preyLives = ScoreboardManager.INSTANCE.getLives(prey, server);

        if (predatorLives <= 2 && predatorLives >= 1) {
            if (preyLives >= 4) {
                ScoreboardManager.INSTANCE.incrementLives(predator, server);
            }
        }
    }





















}
