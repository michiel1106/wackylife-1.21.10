package bikerboys.wackylife.entity.triviabot.server;

import bikerboys.wackylife.*;
import bikerboys.wackylife.Wildcard.*;
import bikerboys.wackylife.Wildcard.wildcards.*;
import bikerboys.wackylife.entity.*;
import bikerboys.wackylife.entity.triviabot.*;
import bikerboys.wackylife.util.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TriviaBotServerData implements PlayerBoundEntity {
    private TriviaBot bot;
    public TriviaBotServerData(TriviaBot bot) {
        this.bot = bot;
    }

    public int despawnPlayerChecks = 0;

    private UUID _boundPlayerUUID;

    @Override
    public void onSetPlayer(ServerPlayerEntity player) {}

    @Override
    public UUID getBoundPlayerUUID() {
        return _boundPlayerUUID;
    }

    @Override
    public void setBoundPlayerUUID(UUID uuid) {
        _boundPlayerUUID = uuid;
    }

    @Override
    public boolean shouldPathfind() {
        if (bot.getEntityWorld().isClient()) return false;
        ServerPlayerEntity player = getBoundPlayer();
        if (player == null) return false;
        if (!player.isAlive()) return false;
        if (getPlayerPos() == null) return false;
        if (player.isSpectator()) return false;
        return true;
    }

    @Override
    public ServerPlayerEntity getPlayer() {
        if (!bot.getEntityWorld().isClient()) {
            return bot.getEntityWorld().getServer().getPlayerManager().getPlayer(getBoundPlayerUUID());
        }
        return null;

    }

    public void tick(MinecraftServer server) {
        if (bot.getEntityWorld().isClient()) return;
        if (despawnChecks()) return;
        bot.pathfinding.tick();
        
        chunkLoading();
        bot.clearStatusEffects();
        bot.sounds.playSounds(server);
    }

    @Nullable
    public ServerPlayerEntity getBoundPlayer() {
        return getPlayer();
    }

    public boolean despawnChecks() {
        ServerPlayerEntity player;
        boolean client = bot.getEntityWorld().isClient();

        if (!client) {
            player = bot.getEntityWorld().getServer().getPlayerManager().getPlayer(getBoundPlayerUUID());


            if (player == null || (player.isSpectator() && ScoreboardManager.INSTANCE.isDead(player))) {
                despawnPlayerChecks++;
            }
            if (despawnPlayerChecks > 200) {
                despawn();
                return true;
            }
            if (bot.age % 10 == 0) {
                if (WackyLife.wackyLife.getWildcardObj() != null && WackyLife.wackyLife.getWildcardObj() instanceof TriviaWildcard) {
                    if (!TriviaWildcard.activeBots.containsValue(bot)) {
                        despawn();
                        return true;
                    }
                }
                else {
                    despawn();
                    return true;
                }
            }
        }
        return false;
    }


    public void handleHighVelocity() {
        Vec3d velocity = bot.getVelocity();
        if (velocity.y > 0.15) {
            bot.setVelocity(velocity.x,0.15,velocity.z);
        }
        else if (velocity.y < -0.15) {
            bot.setVelocity(velocity.x,-0.15,velocity.z);
        }
    }

    public void chunkLoading() {
        if (bot.getEntityWorld() instanceof ServerWorld level) {
            level.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(bot.getBlockPos()), 2);
        }
    }

    public void despawn() {
        if (getBoundPlayerUUID() != null) {
            TriviaWildcard.activeBots.remove(getBoundPlayerUUID());
        }
        if (!bot.getEntityWorld().isClient()) {
            bot.kill((ServerWorld) bot.getEntityWorld());

        }
        bot.discard();
    }
}
