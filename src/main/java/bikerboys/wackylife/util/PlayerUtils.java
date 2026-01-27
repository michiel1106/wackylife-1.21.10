package bikerboys.wackylife.util;

import bikerboys.wackylife.*;
import net.minecraft.component.*;
import net.minecraft.entity.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class PlayerUtils {

    private static HashMap<Component, Integer> broadcastCooldown = new HashMap<>();

    public static void sendTitleWithSubtitle(ServerPlayerEntity player, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
        if (player.getEntityWorld().getServer() == null) return;
        if (player == null) return;
        if (!player.isAlive()) {
            TaskScheduler.scheduleTask(5, () -> sendTitleWithSubtitle(player.getEntityWorld().getServer().getPlayerManager().getPlayer(player.getUuid()), title, subtitle, fadeIn, stay, fadeOut));
            return;
        }
        TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
        player.networkHandler.sendPacket(fadePacket);
        TitleS2CPacket titlePacket = new TitleS2CPacket(title);
        player.networkHandler.sendPacket(titlePacket);
        SubtitleS2CPacket subtitlePacket = new SubtitleS2CPacket(subtitle);
        player.networkHandler.sendPacket(subtitlePacket);
    }

    public static List<ServerPlayerEntity> getActivePlayers(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager()
                .getPlayerList()
                .stream()
                .filter(p -> ScoreboardManager.INSTANCE.getLives(p, server) > 0)
                .collect(Collectors.toCollection(ArrayList::new));

        return players;

    }

    public static void sendTitle(ServerPlayerEntity player, Text title, int fadeIn, int stay, int fadeOut) {
        if (player == null) return;
        if (!player.isAlive()) {
            TaskScheduler.scheduleTask(5, () -> sendTitle(player.getEntityWorld().getServer().getPlayerManager().getPlayer(player.getUuid()), title, fadeIn, stay, fadeOut));
            return;
        }
        TitleFadeS2CPacket fadePacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
        player.networkHandler.sendPacket(fadePacket);
        TitleS2CPacket titlePacket = new TitleS2CPacket(title);
        player.networkHandler.sendPacket(titlePacket);
    }

    public static ServerPlayerEntity getPlayer(String name) {
        if (WackyLife.server == null || name == null) return null;
        return WackyLife.server.getPlayerManager().getPlayer(name);
    }

    public static ServerPlayerEntity getPlayer(UUID uuid) {
        System.out.println("server " + WackyLife.server == null);
        System.out.println("uuid " + uuid == null);
        if (WackyLife.server == null || uuid == null) return null;
        return WackyLife.server.getPlayerManager().getPlayer(uuid);
    }

    public static void sendTitleToPlayers(Collection<ServerPlayerEntity> players, Text title, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayerEntity player : players) {
            sendTitle(player, title, fadeIn, stay, fadeOut);
        }
    }

    public static void sendTitleWithSubtitleToPlayers(Collection<ServerPlayerEntity> players, Text title, Text subtitle, int fadeIn, int stay, int fadeOut) {
        for (ServerPlayerEntity player : players) {
            sendTitleWithSubtitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound) {
        playSoundToPlayers(players,sound, SoundCategory.MASTER, 1, 1);
    }
    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound, float volume, float pitch) {
        playSoundToPlayers(players,sound, SoundCategory.MASTER, volume, pitch);
    }

    public static void playSoundToPlayers(Collection<ServerPlayerEntity> players, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        for (ServerPlayerEntity player : players) {
            if (player == null) continue;
            ((IServerPlayer)player).ls$playNotifySound(sound, soundCategory, volume, pitch);
        }
    }

    public static void applyToAll(MinecraftServer server, Consumer<ServerPlayerEntity> player) {
        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            player.accept(serverPlayerEntity);
        }
    }

    public static void playSoundToPlayer(ServerPlayerEntity player, SoundEvent sound) {
        playSoundToPlayer(player, sound, 1, 1);
    }

    public static void playSoundToPlayer(ServerPlayerEntity player, SoundEvent sound, float volume, float pitch) {
        if (player == null) return;
        ((IServerPlayer)player).ls$playNotifySound(sound, SoundCategory.MASTER, volume, pitch);
    }

    private static final Random rnd = new Random();
    public static void playSoundWithSourceToPlayers(Entity source, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        playSoundWithSourceToPlayers(source.getEntityWorld().getServer().getPlayerManager().getPlayerList(), source, sound, soundCategory, volume, pitch);
    }
    public static void playSoundWithSourceToPlayers(Collection<ServerPlayerEntity> players, Entity source, SoundEvent sound, SoundCategory soundCategory, float volume, float pitch) {
        PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(Registries.SOUND_EVENT.getEntry(sound), soundCategory, source, volume, pitch, rnd.nextLong());

        for (ServerPlayerEntity player : players) {
            player.networkHandler.sendPacket(packet);
        }
    }

}
