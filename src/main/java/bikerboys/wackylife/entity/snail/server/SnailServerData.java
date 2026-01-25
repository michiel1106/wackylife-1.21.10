package bikerboys.wackylife.entity.snail.server;






import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.snail.*;
import bikerboys.wackylife.networking.*;
import bikerboys.wackylife.util.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.registry.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import java.util.Locale;
import java.util.UUID;

public class SnailServerData implements PlayerBoundEntity {
    public static final RegistryKey<DamageType> SNAIL_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(WackyLife.MOD_ID, "snail"));
    public final Snail snail;

    public SnailServerData(Snail snail) {
        this.snail = snail;
    }

    private UUID boundPlayerUUID;

    @Override
    public void onSetPlayer(ServerPlayerEntity player) {
        resetAirPacket();
        updateSnailName();
        updateSkin(player);
        snail.setBoundPlayerDead(ScoreboardManager.INSTANCE.isDead(player));
    }

    @Override
    public void setBoundPlayerUUID(UUID uuid) {
        boundPlayerUUID = uuid;
    }

    @Override
    public UUID getBoundPlayerUUID() {
        return boundPlayerUUID;
    }

    @Override
    public boolean shouldPathfind() {
        if (snail.getEntityWorld().isClient()) return false;
        ServerPlayerEntity player = getPlayer();
        if (player == null) return false;
        if (player.isCreative()) return false;
        if (!player.isAlive()) return false;
        if (getPlayerPos() == null) return false;

        if (player.isSpectator()) return false;

        return true;
    }

    @Override
    public ServerPlayerEntity getPlayer() {
        if (!snail.getEntityWorld().isClient()) {
            return snail.getEntityWorld().getServer().getPlayerManager().getPlayer(getBoundPlayerUUID());
        }
        return null;

    }

    public int dontAttackFor = 0;
    public int despawnPlayerChecks = 0;
    public Text snailName;
    private int lastAir = 0;

    public int getJumpRangeSquared() {
        if (isNerfed()) return 9;
        return Snail.JUMP_RANGE_SQUARED;
    }

    public void updateSnailName() {
        if (!hasBoundPlayer()) return;
        snailName = Text.of(SnailsWildCard.getSnailName(getPlayer()));
    }

    public void tick() {
        if (snail.getEntityWorld().isClient()) return;
        snail.pathfinding.tick();
        if (despawnChecks()) return;
        ServerPlayerEntity boundPlayer = getPlayer();
        LivingEntity boundEntity = getBoundEntity();

        if (dontAttackFor > 0) dontAttackFor--;

        if (snail.age % 20 == 0) {
            updateSnailName();
            if (boundPlayer != null) {
                snail.setBoundPlayerDead(ScoreboardManager.INSTANCE.isDead(boundPlayer));
            }
        }

        if (boundEntity != null && shouldPathfind() && snail.getBoundingBox().expand(0.05).intersects(boundEntity.getBoundingBox())) {
            killBoundEntity(boundEntity);
        }

        if (boundPlayer != null && boundEntity != null) {
            if (Snail.SHOULD_DROWN_PLAYER && !snail.isFromTrivia()) {
                int currentAir = snail.getAir();
                if (boundEntity.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                    currentAir = snail.getMaxAir();
                }
                if (lastAir != currentAir) {
                    lastAir = currentAir;
                    sendAirPacket(boundPlayer, currentAir);
                }
                if (currentAir == 0) damageFromDrowning(boundEntity);
            }
        }

        handleHighVelocity();
        chunkLoading();
        snail.sounds.playSounds();
        if (!Snail.ALLOW_POTION_EFFECTS) {
            snail.clearStatusEffects();
        }
    }

    public boolean despawnChecks() {
        ServerPlayerEntity player = getPlayer();
        if (player == null || (player.isSpectator() && ScoreboardManager.INSTANCE.isDead(player))) {
            despawnPlayerChecks++;
        }
        else {
            despawnPlayerChecks = 0;
        }

        if (despawnPlayerChecks > 200) {
            despawn();
            return true;
        }
        if (snail.age % 10 == 0) {
            if (!snail.isFromTrivia()) {
                if (!SnailsWildCard.snails.containsValue(snail) || !(WackyLife.wackyLife.getWildcardObj() instanceof SnailsWildCard)) {
                    despawn();
                    return true;
                }
            }
            else {
                if (snail.age >= 36000) {
                    despawn();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNerfed() {
        if (snail.isFromTrivia()) return true;

        // TODO make it so that its nerfed in the future
        /*
        if (WildcardManager.FINALE) return true;
         return Wildcard.isFinale();
         */
       return false;
    }

    public void setFromTrivia() {
        snail.setFromTrivia(true);
        dontAttackFor = 100;
        snail.sounds.playAttackSound();
    }

    public void chunkLoading() {
        if (snail.getEntityWorld() instanceof ServerWorld level) {

            level.getChunkManager().addTicket(ChunkTicketType.PORTAL, LevelUtils.chunkPosFromBlockPos(snail.getBlockPos()), 2);

        }
    }

    public void despawn() {
        resetAirPacket();
        if (boundPlayerUUID != null) {
            TriviaWildcard.activeBots.remove(boundPlayerUUID);
        }
        snail.pathfinding.cleanup();

        if (snail.getEntityWorld() instanceof ServerWorld level) {
            snail.kill(level);
        }
        snail.discard();
    }

    public void resetAirPacket() {
        ServerPlayerEntity player = getPlayer();
        if (player != null) {
            sendAirPacket(player, 300);
        }
    }

    public void sendAirPacket(ServerPlayerEntity player, int amount) {
        ServerPlayNetworking.send(player, new SnailAirS2C(amount));
    }

    public void handleHighVelocity() {
        Vec3d velocity = snail.getVelocity();
        if (velocity.y > 0.15) {
            snail.setVelocity(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            snail.setVelocity(velocity.x,-0.15,velocity.z);
        }
    }

    public void killBoundEntity(Entity entity) {
        World level = entity.getEntityWorld();
        if (level instanceof ServerWorld ServerWorld) {
            if (entity instanceof ServerPlayerEntity player) {
                player.setAttacker(snail);
            }

            DamageSource damageSource = new DamageSource(ServerWorld.getRegistryManager()
                    .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(SNAIL_DAMAGE));
            entity.damage(ServerWorld, damageSource, 1000);

        }
    }

    public void damageFromDrowning(Entity entity) {
        if (!entity.isAlive()) return;
        World level = entity.getEntityWorld();
        if (level instanceof ServerWorld ServerWorld) {
            if (entity instanceof ServerPlayerEntity player) {
                player.setAttacker(snail);
            }

            DamageSource damageSource = new DamageSource(ServerWorld.getRegistryManager()
                    .getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(DamageTypes.DROWN));
            entity.damage(ServerWorld, damageSource, 2);

            if (!entity.isAlive() && entity instanceof ServerPlayerEntity) {
                despawn();
            }
        }
    }

    public Text getDefaultName() {
        if (snail.isFromTrivia()) return Text.of("VHSnail");
        if (snailName == null) return snail.getType().getName();
        if (snailName.getString().isEmpty()) return snail.getType().getName();
        return snailName;
    }


    public void updateSkin(PlayerEntity player) {
        if (player == null) return;
        String skinName = player.getNameForScoreboard().toLowerCase(Locale.ROOT);

        snail.setSkinName(skinName);
    }
}
