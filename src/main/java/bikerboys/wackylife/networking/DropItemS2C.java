package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

import java.util.*;

public record DropItemS2C() implements CustomPayload {
    public static final Identifier ALIVE_PLAYERS_PAYLOAD_ID = Identifier.of(WackyLife.MOD_ID, "dropitems");
    public static final Id<DropItemS2C> ID = new Id<>(ALIVE_PLAYERS_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, DropItemS2C> CODEC = PacketCodec.unit(new DropItemS2C());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
