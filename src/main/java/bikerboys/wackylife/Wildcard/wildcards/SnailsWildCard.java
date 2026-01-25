package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.snail.*;
import bikerboys.wackylife.entity.snail.server.*;
import bikerboys.wackylife.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;

import java.util.*;

public class SnailsWildCard extends Wildcard {

    public static Map<UUID, Snail> snails = new HashMap<>();
    public static Map<UUID, String> snailNames = new HashMap<>();
    long ticks = 0;


    @Override
    public void onActivate(MinecraftServer server) {
        snails.clear();
        List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList().stream().filter(player -> ScoreboardManager.INSTANCE.isDead(player)).toList();

        for (ServerPlayerEntity player : playerList) {
            if (!canHaveSnail(player)) continue;
            spawnSnailFor(player);
        }

        super.onActivate(server);
    }


    @Override
    public void deactivate(MinecraftServer server) {
        snails.clear();
        killAllSnails(server);
        super.deactivate(server);
    }


    @Override
    public void tick(MinecraftServer server) {
        ticks++;
        if (ticks % 100 == 0) {
            List<ServerPlayerEntity> playerList = server.getPlayerManager().getPlayerList().stream().filter(player -> !(ScoreboardManager.INSTANCE.isDead(player))).toList();

            for (ServerPlayerEntity player : playerList) {
                if (!canHaveSnail(player)) continue;
                UUID playerUUID = player.getUuid();
                if (snails.containsKey(playerUUID)) {
                    Snail snail = snails.get(playerUUID);
                    if (snail == null || !snail.isAlive()) {
                        snails.remove(playerUUID);
                        spawnSnailFor(player);
                    }
                }
                else {
                    spawnSnailFor(player);
                }
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
        if (pos == null) pos = player.getBlockPos().add(0,30,0);
        spawnSnailFor(player, pos);
    }

    public static void spawnSnailFor(ServerPlayerEntity player, BlockPos pos) {
        if (player == null || pos == null) return;
        Snail snail = LevelUtils.spawnEntity(ModEntities.SNAIL, player.getEntityWorld(), pos);
        if (snail != null) {
            snail.serverData.setBoundPlayer(player);
            snails.put(player.getUuid(), snail);
        }
    }

    public static void killAllSnails(MinecraftServer server) {
        if (server == null) return;
        List<Entity> toKill = new ArrayList<>();
        for (ServerWorld level : server.getWorlds()) {
            for (Entity entity : level.iterateEntities()) {
                if (entity instanceof Snail snail && !snail.isFromTrivia()) {
                    toKill.add(entity);
                }
            }
        }
        toKill.forEach(Entity::discard);
    }

    public static void reloadSnailNames() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.serverData.updateSnailName();
        }
    }

    public static void reloadSnailSkins() {
        for (Snail snail : snails.values()) {
            if (snail == null) return;
            snail.serverData.updateSkin(snail.serverData.getPlayer());
        }
    }

    public static void setSnailName(ServerPlayerEntity player, String name) {
        snailNames.put(player.getUuid(), name);
        reloadSnailNames();
    }

    public static void resetSnailName(ServerPlayerEntity player) {
        snailNames.remove(player.getUuid());
        reloadSnailNames();
    }

    public static String getSnailName(PlayerEntity player) {
        if (player == null) return "Snail";
        if (snailNames.containsKey(player.getUuid())) {
            return snailNames.get(player.getUuid());
        }
        return TextUtils.formatString("{}'s Snail", player);
    }



    @Override
    public String toString() {
        return "Snails";
    }
}
