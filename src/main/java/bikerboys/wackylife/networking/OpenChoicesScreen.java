package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import bikerboys.wackylife.wyr.choice.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

import java.nio.charset.*;
import java.util.*;

public record OpenChoicesScreen(List<ChoicePair> choices) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(WackyLife.MOD_ID, "open_choices_screen");
    public static final Id<OpenChoicesScreen> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, ChoicePair> CHOICE_PAIR_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            ChoicePair::positiveChoice,
            PacketCodecs.STRING,
            ChoicePair::negativeChoice,
            ChoicePair::new
    );

    public static final PacketCodec<RegistryByteBuf, List<ChoicePair>> CHOICE_PAIR_LIST_CODEC =
            CHOICE_PAIR_CODEC.collect(PacketCodecs.toList());

    public static final PacketCodec<RegistryByteBuf, OpenChoicesScreen> CODEC = PacketCodec.tuple(
            CHOICE_PAIR_LIST_CODEC,
            OpenChoicesScreen::choices,
            OpenChoicesScreen::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
