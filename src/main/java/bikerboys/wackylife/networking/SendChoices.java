package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import bikerboys.wackylife.wyr.choice.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

import java.util.*;

public record SendChoices(ChoicePair choices) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(WackyLife.MOD_ID, "send_choices");
    public static final Id<SendChoices> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, ChoicePair> CHOICE_PAIR_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            ChoicePair::positiveChoice,
            PacketCodecs.STRING,
            ChoicePair::negativeChoice,
            ChoicePair::new
    );


    public static final PacketCodec<RegistryByteBuf, SendChoices> CODEC = PacketCodec.tuple(
            CHOICE_PAIR_CODEC,
            SendChoices::choices,
            SendChoices::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
