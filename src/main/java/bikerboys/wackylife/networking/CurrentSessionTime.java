package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

public record CurrentSessionTime(int integer) implements CustomPayload {
    public static final Identifier CURRENT_SESSION_TIME = Identifier.of(WackyLife.MOD_ID, "currentsessiontime");
    public static final Id<CurrentSessionTime> ID = new Id<>(CURRENT_SESSION_TIME);
    public static final PacketCodec<RegistryByteBuf, CurrentSessionTime> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, // encode/decode list of strings
                    CurrentSessionTime::integer,
                    CurrentSessionTime::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
