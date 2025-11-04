package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

import java.util.*;

public record LivesAmountUpdate(int integer) implements CustomPayload {
    public static final Identifier LIVES_AMOUNT_UPDATE = Identifier.of(WackyLife.MOD_ID, "livesamountupdate");
    public static final Id<LivesAmountUpdate> ID = new Id<>(LIVES_AMOUNT_UPDATE);
    public static final PacketCodec<RegistryByteBuf, LivesAmountUpdate> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, // encode/decode list of strings
                    LivesAmountUpdate::integer,
                    LivesAmountUpdate::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
