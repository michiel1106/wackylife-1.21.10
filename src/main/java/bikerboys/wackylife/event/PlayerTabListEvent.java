package bikerboys.wackylife.event;

import com.google.common.collect.*;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.*;

import java.util.*;

public interface PlayerTabListEvent {

    public static final Event<ModifySentPlayerList> MODIFY_SENT_PLAYER_LIST = EventFactory.createArrayBacked(ModifySentPlayerList.class, callbacks -> (player, playerEntityList, action) -> {

        List<ServerPlayerEntity> allowthrough = Lists.<ServerPlayerEntity>newArrayList();
        for (ModifySentPlayerList callback : callbacks) {
            allowthrough = callback.updatePlayerList(player, playerEntityList, action);
        }
        return allowthrough;

    });


    @FunctionalInterface
    public interface ModifySentPlayerList {
        List<ServerPlayerEntity> updatePlayerList(ServerPlayerEntity targetplayer, List<ServerPlayerEntity> playerEntityList, PlayerListS2CPacket.Action action);
    }

}

