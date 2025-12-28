package bikerboys.wackylife.entity.triviabot.trivia;

import java.util.*;

public record Question(String question, List<String> answers, int rightQuestionIndex) {
    public static Question DEFAULT = new Question("You shouldnt be able to see this", List.of("Uhhh, idk what you should do honestly.", "I guess your sorta lucky cause this is the answer", "good luck"), 1);

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
