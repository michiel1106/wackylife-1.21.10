package bikerboys.wackylife.Wildcard.wildcards;

import bikerboys.wackylife.Wildcard.Wildcard;
import bikerboys.wackylife.event.*;
import bikerboys.wackylife.util.*;
import dev.architectury.event.*;
import dev.architectury.event.events.common.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.*;

import java.util.*;

public class SoulboundWildcard extends Wildcard {

    private final Map<UUID, Set<UUID>> soulmates = new HashMap<>();

    // Prevent infinite damage/heal loops
    private final Set<UUID> syncLock = new HashSet<>();

    @Override
    public void onActivate(MinecraftServer server) {
        EntityEvent.LIVING_HURT.register(this::hurt);
        CustomPlayerEvent.HEAL.register(this::onPlayerHeal);
        soulmates.clear();
        syncLock.clear();
        assignSoulmates(server);
    }

    private EventResult hurt(LivingEntity livingEntity, DamageSource damageSource, float v) {
        if (livingEntity instanceof ServerPlayerEntity player) {
            onPlayerDamage(player, damageSource, v);
        }

        return EventResult.pass();
    }

    @Override
    public void deactivate(MinecraftServer server) {
        soulmates.clear();
        syncLock.clear();
    }

    @Override
    public void onPlayerJoin(ServerPlayerEntity player) {
        List<ServerPlayerEntity> mates = getSoulmates(player);
        if (!mates.isEmpty()) {
            syncHealth(player, mates.get(0));
        }
    }


    @Override
    public void onPlayerLeave(ServerPlayerEntity player) {
        syncLock.remove(player.getUuid());
    }

    /* ---------------- Soulmate Logic ---------------- */

    private void assignSoulmates(MinecraftServer server) {
        List<ServerPlayerEntity> players =
                new ArrayList<>(server.getPlayerManager().getPlayerList());

        Collections.shuffle(players);
        soulmates.clear();

        int i = 0;

        // Normal pairs
        for (; i + 1 < players.size(); i += 2) {
            ServerPlayerEntity a = players.get(i);
            ServerPlayerEntity b = players.get(i + 1);

            Set<UUID> group = new HashSet<>();
            group.add(a.getUuid());
            group.add(b.getUuid());

            soulmates.put(a.getUuid(), new HashSet<>(group));
            soulmates.put(b.getUuid(), new HashSet<>(group));

            syncHealth(a, b);
        }

        // Odd one out → attach to random existing pair
        if (players.size() % 2 == 1 && players.size() >= 3) {
            ServerPlayerEntity leftover = players.get(players.size() - 1);

            // Pick any existing group
            UUID randomKey = soulmates.keySet().iterator().next();
            Set<UUID> group = soulmates.get(randomKey);

            group.add(leftover.getUuid());

            for (UUID id : group) {
                soulmates.put(id, new HashSet<>(group));
            }

            ServerPlayerEntity any =
                    server.getPlayerManager().getPlayer(group.iterator().next());

            if (any != null) {
                syncHealth(leftover, any);
            }
        }
    }


    public void setSoulmates(ServerPlayerEntity a, ServerPlayerEntity b) {
        Set<UUID> group = new HashSet<>();
        group.add(a.getUuid());
        group.add(b.getUuid());

        soulmates.put(a.getUuid(), new HashSet<>(group));
        soulmates.put(b.getUuid(), new HashSet<>(group));

        syncHealth(a, b);
    }


    public List<ServerPlayerEntity> getSoulmates(ServerPlayerEntity player) {
        Set<UUID> ids = soulmates.get(player.getUuid());
        if (ids == null) return List.of();

        return ids.stream()
                .filter(id -> !id.equals(player.getUuid()))
                .map(id -> player.getEntityWorld().getServer().getPlayerManager().getPlayer(id))
                .filter(Objects::nonNull)
                .toList();
    }


    /* ---------------- Damage Sync ---------------- */

    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        if (amount <= 0) return;
        if (syncLock.contains(player.getUuid())) return;

        for (ServerPlayerEntity mate : getSoulmates(player)) {
            if (!mate.isAlive()) continue;

            try {
                syncLock.add(mate.getUuid());
                mate.damage(player.getEntityWorld(), player.getDamageSources().magic(), amount);
            } finally {
                syncLock.remove(mate.getUuid());
            }
        }
    }


    /* ---------------- Healing Sync ---------------- */

    public void onPlayerHeal(ServerPlayerEntity player, float amount) {
        if (amount <= 0) return;
        if (syncLock.contains(player.getUuid())) return;

        for (ServerPlayerEntity mate : getSoulmates(player)) {
            if (!mate.isAlive()) continue;

            try {
                syncLock.add(mate.getUuid());

                float newHealth = Math.min(
                        mate.getHealth() + amount,
                        mate.getMaxHealth()
                );
                mate.setHealth(newHealth);

            } finally {
                syncLock.remove(mate.getUuid());
            }
        }
    }


    @Override
    public void tick(MinecraftServer server) {
        super.tick(server);

        PlayerUtils.applyToAll(server, (player -> {
            if (!ScoreboardManager.INSTANCE.isDead(player)) {
                if (player.getMainHandStack().isOf(Items.GOLDEN_APPLE) || player.getMainHandStack().isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                    if (player.age % 20 == 0) {
                        player.sendMessage(Text.literal("The use of golden apples or enchanted golden apples in this session is not allowed."), true);
                    }
                }
            }

        }));

    }

    private void syncHealth(ServerPlayerEntity a, ServerPlayerEntity b) {
        float shared = Math.min(a.getHealth(), b.getHealth());
        a.setHealth(shared);
        b.setHealth(shared);
    }

    @Override
    public String toString() {
        return "Soulbound";
    }
}
