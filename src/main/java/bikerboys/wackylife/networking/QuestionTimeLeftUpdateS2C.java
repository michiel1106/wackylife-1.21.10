package bikerboys.wackylife.networking;

import bikerboys.wackylife.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.network.packet.*;
import net.minecraft.util.*;

public record QuestionTimeLeftUpdateS2C(int integer) implements CustomPayload {
    public static final Identifier QUESTION_TIME_LEFT = Identifier.of(WackyLife.MOD_ID, "questiontimeleft");
    public static final Id<QuestionTimeLeftUpdateS2C> ID = new Id<>(QUESTION_TIME_LEFT);
    public static final PacketCodec<RegistryByteBuf, QuestionTimeLeftUpdateS2C> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER, // encode/decode list of strings
                    QuestionTimeLeftUpdateS2C::integer,
                    QuestionTimeLeftUpdateS2C::new
            );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
