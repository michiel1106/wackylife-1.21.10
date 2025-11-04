package bikerboys.wackylife.series;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
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
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import java.util.*;

public class WackySeries {
    private final DeathsManager deathsManager = new DeathsManager(this);
    private Wildcard wildcard;

    public WackySeries() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
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
                    player.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("6").formatted(Formatting.DARK_GREEN)));
                }
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

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new CurrentSessionTime(ScoreboardManager.INSTANCE.getTime(server)));
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

        int time = ScoreboardManager.INSTANCE.getTime(server);
        if (time > 0) {
            ScoreboardManager.INSTANCE.decrementTime(server);
        } else if (time == 0){
            endSession(server);
        }

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
        Constants.paused = true;

    }

    public void setWildcard(String wildcard, MinecraftServer server) {
        Wildcard wildcard1 = WildcardEnum.getWildcard(wildcard);

        if (this.wildcard != null) {
            this.wildcard.deactivate(server);
        }

        if (wildcard1 != null) {
            this.wildcard = wildcard1;
        }
    }

    public String getWildcard() {
        if (wildcard != null) {
            return wildcard.toString();
        }
        return null;
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
