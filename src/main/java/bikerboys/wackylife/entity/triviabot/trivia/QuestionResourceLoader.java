package bikerboys.wackylife.entity.triviabot.trivia;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class QuestionResourceLoader implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new Gson();

    public static final Identifier ID = Identifier.of("wackylife", "questions_loader");
    private static final Identifier QUESTIONS_FILE = Identifier.of("wackylife", "questions/questions.json");

    @Override
    public Identifier getFabricId() {
        return ID;
    }



    @Override
    public void reload(ResourceManager manager) {

        QuestionManager.questions.clear();

        try {
            Collection<Resource> resources =
                    manager.getAllResources(QUESTIONS_FILE);

            for (Resource resource : resources) {
                try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

                    JsonArray array = GSON.fromJson(reader, JsonArray.class);

                    for (JsonElement element : array) {
                        JsonObject obj = element.getAsJsonObject();
                        String question = obj.get("question").getAsString();
                        List<String> answers = new ArrayList<>();

                        for (JsonElement answer : obj.getAsJsonArray("answers")) {
                            answers.add(answer.getAsString());
                        }

                        int correctIndex = obj.get("correct_answer_index").getAsInt();



                        QuestionManager.questions.add(new Question(question, answers, correctIndex));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load trivia questions", e);
        }
    }
}

