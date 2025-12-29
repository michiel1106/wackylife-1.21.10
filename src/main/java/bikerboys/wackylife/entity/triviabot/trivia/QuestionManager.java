package bikerboys.wackylife.entity.triviabot.trivia;

import bikerboys.wackylife.entity.triviabot.*;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;

import java.util.*;

public class QuestionManager {
    private static final Identifier ID = Identifier.of("wackylife", "questions");
    private static final Random random = Random.create();

    public static List<Question> questions = new ArrayList<>();


    public static void setQuestions(TriviaBot bot) {
        if (!questions.isEmpty()) {
            Question question = questions.get(random.nextInt(questions.size()));
            bot.setQuestion(question);
        }
    }

    public static Question getRandomQuestion() {
        if (!questions.isEmpty()) {
            return questions.get(random.nextInt(questions.size()));
        }
        return Question.DEFAULT;
    }


    public static void registerQuestions() {
    }


}

