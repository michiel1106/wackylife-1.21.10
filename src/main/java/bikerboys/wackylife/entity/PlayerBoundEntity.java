package bikerboys.wackylife.entity;

import static bikerboys.wackylife.WackyLife.clientHelper;
import net.fabricmc.api.*;
import net.fabricmc.loader.api.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

import java.util.UUID;

public interface PlayerBoundEntity {
    void onSetPlayer(ServerPlayerEntity player);
    UUID getBoundPlayerUUID();
    void setBoundPlayerUUID(UUID uuid);
    boolean shouldPathfind();

    default boolean hasBoundPlayer() {
        if (getBoundPlayerUUID() == null) return false;
        if (getBoundPlayer() == null) return false;
        return true;
    }

    default void setBoundPlayer(ServerPlayerEntity player) {
        if (player == null) return;
        setBoundPlayerUUID(player.getUUID());
        onSetPlayer(player);
    }

    @Nullable
    default ServerPlayerEntity getBoundPlayer() {
        if (isLogicalSide()) {
            return PlayerUtils.getPlayer(getBoundPlayerUUID());
        }
        return null;
    }

    static boolean isLogicalSide() {
        if (!isClient()) return true;
        return clientHelper != null && clientHelper.isRunningIntegratedServer();
    }


    static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Nullable
    default LivingEntity getBoundEntity() {
        if (isLogicalSide()) {
            ServerPlayerEntity player = PlayerUtils.getPlayer(getBoundPlayerUUID());

            return player;
        }
        return null;
    }

    default Vec3d getPlayerPos() {
        if (!isLogicalSide()) return null;
        Entity entity = getBoundEntity();
        if (entity != null) {
            return entity.getEntityPos();
        }
        return null;
    }
}
