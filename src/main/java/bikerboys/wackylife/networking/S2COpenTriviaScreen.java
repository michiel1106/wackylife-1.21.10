package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import bikerboys.wackylife.entity.triviabot.trivia.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

import java.util.*;

public record S2COpenTriviaScreen(Question question, UUID uuid) implements CustomPayload {
    public static final Identifier OPEN_TRIVIA_SCREEN = Identifier.of(WackyLife.MOD_ID, "opentriviascreen");
    public static final Id<S2COpenTriviaScreen> ID = new Id<>(OPEN_TRIVIA_SCREEN);

    public static final PacketCodec<RegistryByteBuf, S2COpenTriviaScreen> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, p -> p.question().question(),
                    PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), p -> p.question().answers(),
                    PacketCodecs.INTEGER, p -> p.question().rightQuestionIndex(),
                    Uuids.PACKET_CODEC, S2COpenTriviaScreen::uuid,
                    (q, a, r, u) -> new S2COpenTriviaScreen(new Question(q, a, r), u)
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
