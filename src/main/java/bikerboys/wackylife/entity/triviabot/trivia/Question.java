package bikerboys.wackylife.entity.triviabot.trivia;

import io.netty.buffer.*;
import net.minecraft.network.codec.*;

import java.util.*;

public record Question(String question, List<String> answers, int rightQuestionIndex) {
    public static Question DEFAULT = new Question("You shouldnt be able to see this", List.of("Uhhh, idk what you should do honestly.", "I guess your sorta lucky cause this is the answer", "good luck"), 1);

    public static final PacketCodec<ByteBuf, Question> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, Question::question,
                    PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), Question::answers,
                    PacketCodecs.INTEGER, Question::rightQuestionIndex,
                    Question::new
            );


    @Override
    public List<String> answers() {
        return answers;
    }

    @Override
    public String question() {
        return question;
    }

    @Override
    public int rightQuestionIndex() {
        return rightQuestionIndex;
    }


}
