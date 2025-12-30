package bikerboys.wackylife.event;

import com.google.common.collect.*;
import net.fabricmc.fabric.api.event.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.*;

import java.util.*;

public interface CustomPlayerEvent {

    public static final Event<Heal> HEAL = EventFactory.createArrayBacked(Heal.class, callbacks -> (player, amount) -> {
        for (Heal callback : callbacks) {
            callback.heal(player, amount);
        }
    });


    @FunctionalInterface
    public interface Heal {
        void heal(ServerPlayerEntity player, float amount);
    }

}

