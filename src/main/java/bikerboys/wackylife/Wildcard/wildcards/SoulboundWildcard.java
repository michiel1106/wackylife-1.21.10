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

    private final Map<UUID, UUID> soulmates = new HashMap<>();

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
        // Optional: re-sync health on join
        ServerPlayerEntity mate = getSoulmate(player);
        if (mate != null) {
            syncHealth(player, mate);
        }
    }

    @Override
    public void onPlayerLeave(ServerPlayerEntity player) {
        syncLock.remove(player.getUuid());
    }

    /* ---------------- Soulmate Logic ---------------- */

    private void assignSoulmates(MinecraftServer server) {
        List<ServerPlayerEntity> list = new ArrayList<>(server.getPlayerManager().getPlayerList());
        List<ServerPlayerEntity> players = new ArrayList<>(list.stream().filter((player -> !ScoreboardManager.INSTANCE.isDead(player))).toList());


        Collections.shuffle(players);

        for (int i = 0; i + 1 < players.size(); i += 2) {
            ServerPlayerEntity a = players.get(i);
            ServerPlayerEntity b = players.get(i + 1);

            soulmates.put(a.getUuid(), b.getUuid());
            soulmates.put(b.getUuid(), a.getUuid());

            syncHealth(a, b);
        }
    }

    private ServerPlayerEntity getSoulmate(ServerPlayerEntity player) {
        UUID mateId = soulmates.get(player.getUuid());
        if (mateId == null) return null;
        return player.getEntityWorld().getServer().getPlayerManager().getPlayer(mateId);
    }

    /* ---------------- Damage Sync ---------------- */

    public void onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        if (amount <= 0) return;
        if (syncLock.contains(player.getUuid())) return;

        ServerPlayerEntity mate = getSoulmate(player);
        if (mate == null || !mate.isAlive()) return;

        try {
            syncLock.add(mate.getUuid());
            mate.damage(player.getEntityWorld().toServerWorld(), source, amount);
        } finally {
            syncLock.remove(mate.getUuid());
        }
    }

    /* ---------------- Healing Sync ---------------- */

    public void onPlayerHeal(ServerPlayerEntity player, float amount) {
        if (amount <= 0) return;
        if (syncLock.contains(player.getUuid())) return;

        ServerPlayerEntity mate = getSoulmate(player);
        if (mate == null || !mate.isAlive()) return;

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
