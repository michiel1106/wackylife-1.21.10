package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

public record SubmitAnswerC2S(int index) implements CustomPayload {
    public static final Identifier PACKET_ID = Identifier.of(WackyLife.MOD_ID, "submitanswer");
    public static final Id<SubmitAnswerC2S> ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, SubmitAnswerC2S> SUBMIT_ANSWER_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER,
            SubmitAnswerC2S::index,
            SubmitAnswerC2S::new
    );



    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
