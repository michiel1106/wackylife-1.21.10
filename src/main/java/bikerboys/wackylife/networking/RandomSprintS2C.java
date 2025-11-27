package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

public record RandomSprintS2C(boolean sprint) implements CustomPayload {
    public static final Identifier RANDOM_SPRINT = Identifier.of(WackyLife.MOD_ID, "randomsprint");
    public static final Id<RandomSprintS2C> ID = new Id<>(RANDOM_SPRINT);
    public static final PacketCodec<RegistryByteBuf, RandomSprintS2C> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOLEAN, // encode/decode list of strings
                    RandomSprintS2C::sprint,
                    RandomSprintS2C::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
