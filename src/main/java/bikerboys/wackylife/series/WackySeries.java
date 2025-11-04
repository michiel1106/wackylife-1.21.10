package bikerboys.wackylife.series;

import bikerboys.wackylife.manager.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
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
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.world.*;

import java.util.*;

public class WackySeries {
    private final DeathsManager deathsManager = new DeathsManager(this);


    public WackySeries() {
        ServerTickEvents.END_WORLD_TICK.register(this::tick);
        EntityEvent.LIVING_DEATH.register(this::onPlayerDeath);
    }

    public DeathsManager getDeathsManager() {
        return deathsManager;
    }

    public void initialize(World world) {
        if (world.getServer() == null) return;

        for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
            ScoreAccess scoreAccess = ScoreboardManager.INSTANCE.getScoreboard(player, world.getServer(), Constants.LivesScoreboard);
            if (scoreAccess == null) {
                ServerScoreboard scoreboard = world.getServer().getScoreboard();
                ScoreboardObjective objective = scoreboard.getNullableObjective(Constants.LivesScoreboard);
                if (objective != null) {
                    ScoreAccess newScore = scoreboard.getOrCreateScore(player, objective);
                    newScore.setScore(6);
                }
            }
        }
    }

    public EventResult onPlayerDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (Constants.paused) return EventResult.pass();

        if (livingEntity instanceof ServerPlayerEntity player) {
            ServerWorld world = player.getEntityWorld();
            Entity attacker = damageSource.getAttacker();

            if (attacker instanceof PlayerEntity predator) {
                onPlayerKill(predator, player, world);
            }

            if (damageSource.isOf(DamageTypes.FALLING_ANVIL)
                    || (damageSource.isOf(DamageTypes.EXPLOSION)
                    && damageSource.getSource() instanceof TntMinecartEntity)) {
                deathsManager.registerDeath(player);



                for (ServerPlayerEntity online : world.getPlayers()) {
                    online.sendMessage(Text.literal("§c" + player.getName().getString() +
                            " §7was killed! §eUse §a/wackylife claimkill " +
                            player.getName().getString() + " §eto claim the kill."), false);
                }

            }

            ScoreboardManager.INSTANCE.decrementScoreboard(player, world.getServer(), Constants.LivesScoreboard);

            int lives = ScoreboardManager.INSTANCE.getLives(player, world);
            if (lives == 0) {
                player.changeGameMode(GameMode.SPECTATOR);

                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world, SpawnReason.COMMAND);
                if (lightningEntity != null) {
                    lightningEntity.setPosition(player.getX(), player.getY(), player.getZ());
                    lightningEntity.setCosmetic(true);
                    world.spawnEntity(lightningEntity);
                }
            }
        }

        return EventResult.pass();
    }


    private void tick(ServerWorld world) {

        if (Constants.paused) {
            List<String> allNames = world.getPlayers().stream()
                    .map(p -> p.getName().getString())
                    .toList();

            for (ServerPlayerEntity player : world.getPlayers()) {
                ServerPlayNetworking.send(player, new AlivePlayerList(allNames));
            }

            return;
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            int lives = ScoreboardManager.INSTANCE.getLives(player, world);
            ServerPlayNetworking.send(player, new LivesAmountUpdate(lives));
        }

        List<String> aliveNames = world.getPlayers().stream()
                .filter(p -> ScoreboardManager.INSTANCE.getLives(p, world) >= 1)
                .map(p -> p.getName().getString())
                .toList();

        List<String> allNames = world.getPlayers().stream()
                .map(p -> p.getName().getString())
                .toList();

        for (ServerPlayerEntity player : world.getPlayers()) {
            int lives = ScoreboardManager.INSTANCE.getLives(player, world);
            if (lives >= 1) {
                ServerPlayNetworking.send(player, new AlivePlayerList(aliveNames));
            } else {
                ServerPlayNetworking.send(player, new AlivePlayerList(allNames));
            }
        }



    }




    public void onPlayerKill(PlayerEntity predator, PlayerEntity prey, World world) {
        if (Constants.paused) return;

        int predatorLives = ScoreboardManager.INSTANCE.getLives(predator, world);
        int preyLives = ScoreboardManager.INSTANCE.getLives(prey, world);

        if (predatorLives <= 2 && predatorLives >= 1) {
            if (preyLives >= 4) {
                ScoreboardManager.INSTANCE.incrementLives(predator, world);
            }
        }
    }





















}
