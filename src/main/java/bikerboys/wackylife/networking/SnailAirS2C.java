package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

public record SnailAirS2C(int integer) implements CustomPayload {
    public static final Identifier QUESTION_TIME_LEFT = Identifier.of(WackyLife.MOD_ID, "snailair");
    public static final Id<SnailAirS2C> ID = new Id<>(QUESTION_TIME_LEFT);
    public static final PacketCodec<RegistryByteBuf, SnailAirS2C> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, // encode/decode list of strings
                    SnailAirS2C::integer,
                    SnailAirS2C::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
