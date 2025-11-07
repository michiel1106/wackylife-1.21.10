package bikerboys.wackylife.Wildcard;

import net.minecraft.server.*;
import net.minecraft.server.network.*;

public abstract class Wildcard {

    public void tick(MinecraftServer server) {

    }

    public void deactivate(MinecraftServer server) {

    }

    public void onPlayerJoin(ServerPlayerEntity player) {

    }

    public void onPlayerLeave(ServerPlayerEntity player) {

    }

    public void onActivate(MinecraftServer server) {

    }



    @Override
    public abstract String toString();
}
