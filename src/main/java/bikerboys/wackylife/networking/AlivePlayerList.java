package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

import java.util.*;

public record AlivePlayerList(List<String> playerNames) implements CustomPayload {
    public static final Identifier ALIVE_PLAYERS_PAYLOAD_ID = Identifier.of(WackyLife.MOD_ID, "aliveplayerlist");
    public static final CustomPayload.Id<AlivePlayerList> ID = new CustomPayload.Id<>(ALIVE_PLAYERS_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, AlivePlayerList> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING.collect(PacketCodecs.toList()), // encode/decode list of strings
                    AlivePlayerList::playerNames,
                    AlivePlayerList::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
