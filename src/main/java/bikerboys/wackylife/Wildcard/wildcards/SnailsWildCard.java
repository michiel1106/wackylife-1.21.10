package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.snail.*;
import bikerboys.wackylife.entity.snail.server.*;
import bikerboys.wackylife.util.*;
import com.google.common.reflect.*;
import com.google.gson.*;
import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.loader.api.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.resource.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

public class SnailsWildCard extends Wildcard {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path snailFile;

    public static Map<UUID, Snail> snails = new HashMap<>();
    public static Map<String, String> snailNames = new HashMap<>();
    long ticks = 0;


    @Override
    public void onActivate(MinecraftServer server) {
        snails.clear();
        List<ServerPlayerEntity> playerList = PlayerUtils.getActivePlayers(server);



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
            List<ServerPlayerEntity> playerList = PlayerUtils.getActivePlayers(server);

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

    private static void saveSnailNames() {
        if (snailFile == null) return;

        try {
            String json = GSON.toJson(snailNames);
            Files.writeString(snailFile, json, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSnailName(ServerPlayerEntity player, String name) {
        snailNames.put(player.getGameProfile().name().toLowerCase(), name);
        saveSnailNames();
        reloadSnailNames();
    }

    public static void resetSnailName(ServerPlayerEntity player) {
        snailNames.remove(player.getGameProfile().name().toLowerCase());
        saveSnailNames();
        reloadSnailNames();
    }

    public static String getSnailName(PlayerEntity player) {
        if (player == null) return "Snail";

        String username = player.getGameProfile().name().toLowerCase();

        if (snailNames.containsKey(username)) {
            return snailNames.get(username);
        }

        return TextUtils.formatString("{}'s Snail", player);
    }



    @Override
    public String toString() {
        return "Snails";
    }



    private static Map<String, String> createDefaultSnailNames() {
        Map<String, String> defaults = new HashMap<>();

        defaults.put("bikerboys", "Bikersnail");


        defaults.put("spokecat", "shellcat");
        defaults.put("Flitterboom", "Shellboom");
        defaults.put("hayberri", "hayShelli");
        defaults.put("TheZivZumbo", "TheSnailZumbo");
        defaults.put("sumi3909", "Snailmi");
        defaults.put("Vermilyra", "Snailyra");
        defaults.put("Blithesome89", "Blithesnail");
        defaults.put("Feufeu000", "SlimeySnail000");
        defaults.put("Lennydavillain", "LennyDaSnail");
        defaults.put("Cryoclipse_", "CryoShell");
        defaults.put("championhestu", "SnailChampion");
        defaults.put("P1NKSY", "Snailsy");
        defaults.put("CHANGETHISTORANDOMSUSERNAME", "Randosnail");
        defaults.put("Pitterr_", "Pittastropod");
        defaults.put("Bikerboys", "Slimeyboys");
        defaults.put("astroey", "Slimey");
        defaults.put("CTheKeyLord", "CTheSnailLord");
        defaults.put("MrMidnight", "MrSlimeNight");
        defaults.put("Cake_Crazy22", "Snail_Crazy22");
        defaults.put("Teei", "Sneei");
        defaults.put("DBisCrazy", "DBisSlimey");
        defaults.put("Maddnova109", "MaddSnail109");

        return defaults;
    }

    public static class SnailNameLoader implements SimpleSynchronousResourceReloadListener {
        public static final Identifier ID = Identifier.of("wackylife", "snailnameloader");

        @Override
        public Identifier getFabricId() {
            return ID;
        }

        @Override
        public void reload(ResourceManager manager) {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            Path wackylifeDir = configDir.resolve("wackylife");
            snailFile = wackylifeDir.resolve("snail_names.json");

            try {
                Files.createDirectories(wackylifeDir);

                if (Files.notExists(snailFile)) {
                    // Create defaults
                    Map<String, String> defaults = createDefaultSnailNames();

                    String json = GSON.toJson(defaults);
                    Files.writeString(snailFile, json);

                    snailNames.clear();
                    snailNames.putAll(defaults);
                    return;
                }

                // Load existing file
                String json = Files.readString(snailFile);

                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> loaded = GSON.fromJson(json, type);

                snailNames.clear();
                if (loaded != null) {
                    for (Map.Entry<String, String> entry : loaded.entrySet()) {
                        snailNames.put(entry.getKey().toLowerCase(), entry.getValue());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
