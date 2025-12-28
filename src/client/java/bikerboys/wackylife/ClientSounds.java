package bikerboys.wackylife;

import bikerboys.wackylife.mixin.client.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.sound.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;

import java.util.*;

public class ClientSounds {
    public static final Map<UUID, SoundInstance> onlyPlayLatestEntities = new HashMap<>();
    public static final List<SoundInstance> onlyPlayLatest = new ArrayList<>();
    private static final List<String> onlyPlayLatestSounds = List.of(
            "wildlife_trivia_intro",
            "wildlife_trivia_suspense",
            "wildlife_trivia_suspense_end",
            "wildlife_trivia_analyzing",

            "nicelife_santabot_intro",
            "nicelife_santabot_suspense",
            "nicelife_santabot_suspense_end",
            "nicelife_santabot_analyzing"
    );

    public static void onSoundPlay(SoundInstance sound) {


        if (!onlyPlayLatestSounds.contains(sound.getId().getPath())) return;

        if (sound instanceof EntityTrackingSoundInstance entityTrackingSound) {
            if ((entityTrackingSound instanceof EntityBoundSoundInstanceAccessor entityTrackingSoundAccessor)) {
                Entity entity = entityTrackingSoundAccessor.getEntity();
                if (entity == null) return;
                UUID uuid = entity.getUuid();
                if (uuid == null) return;

                if (onlyPlayLatestEntities.containsKey(uuid)) {
                    SoundInstance stopSound = onlyPlayLatestEntities.get(uuid);
                    if (stopSound != null) {
                        MinecraftClient.getInstance().getSoundManager().stop(stopSound);
                    }
                }
                onlyPlayLatestEntities.put(uuid, sound);
                return;
            }
        }

        for (SoundInstance stopSound : onlyPlayLatest) {
            if (stopSound != null) {
                MinecraftClient.getInstance().getSoundManager().stop(stopSound);
            }
        }
        onlyPlayLatest.clear();
        onlyPlayLatest.add(sound);
    }

    private static final List<String> onlyOneOf = List.of(
            "wildlife_trivia_intro",
            "wildlife_trivia_suspense",
            "wildlife_trivia_suspense_end"
    );
    private static long ticks = 0;
    public static void updateSingleSoundVolumes() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ticks++;
        if (ticks % 15 != 0) return;
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        if (!(soundManager instanceof SoundManagerAccessor managerAccessor)) return;
        SoundSystem soundSystem = managerAccessor.getSoundSystem();
        if (!(soundSystem instanceof SoundEngineAccessor accessor)) return;
        Map<String, Map<Double, SoundInstance>> soundMap = new HashMap<>();
        for (Collection<SoundInstance> soundCategory : accessor.getSounds().asMap().values()) {
            for (SoundInstance sound : soundCategory) {

                String name = sound.getId().getPath();

                if (!onlyOneOf.contains(name)) continue;
                Vec3d soundPosition = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
                double distance = player.getEntityPos().distanceTo(soundPosition);
                if (soundMap.containsKey(name)) {
                    soundMap.get(name).put(distance, sound);
                }
                else {
                    Map<Double, SoundInstance> distanceMap = new TreeMap<>();
                    distanceMap.put(distance, sound);
                    soundMap.put(name, distanceMap);
                }
            }
        }

        if (soundMap.isEmpty()) return;
        for (Map<Double, SoundInstance> distanceMap : soundMap.values()) {
            if (distanceMap.isEmpty()) continue;
            int index = 0;
            for (SoundInstance sound : distanceMap.values()) {
                if (sound instanceof AbstractSoundInstanceAccessor soundAccessor) {
                    if (index == 0) {
                        soundAccessor.setVolume(1);
                    }
                    else {
                        soundAccessor.setVolume(0);
                    }
                    index++;
                }
            }
        }
    }
}
