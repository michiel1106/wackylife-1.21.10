package bikerboys.wackylife.entity.triviabot.server.trivia;



import bikerboys.wackylife.resources.*;
import com.google.common.reflect.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TriviaQuestionManager {
    private File file;
    private File folder;
    public TriviaQuestionManager(String folder, String file) {
        this.file = new File(folder + "/" + file);
        this.folder = new File(folder);
        if (!this.folder.exists()) {
            if (!this.folder.mkdirs()) {
                return;
            }
        }
        if (!this.file.exists()) {
            ResourceHandler handler = new ResourceHandler();
            if (folder.contains("nicelife")) {
                handler.copyBundledSingleFile("/files/trivia/nicelife-trivia.json", this.file.toPath());
            }
            if (file.startsWith("easy-")) {
                handler.copyBundledSingleFile("/files/trivia/easy-trivia.json", this.file.toPath());
            }
            else if (file.startsWith("normal-")) {
                handler.copyBundledSingleFile("/files/trivia/normal-trivia.json", this.file.toPath());
            }
            else if (file.startsWith("hard-")) {
                handler.copyBundledSingleFile("/files/trivia/hard-trivia.json", this.file.toPath());
            }
        }
    }

    private void setFileContent(String content) {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(file, false);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {

        }
    }

    public List<TriviaQuestion> getTriviaQuestions() throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        Gson gson = new Gson();
        return gson.fromJson(content, new TypeToken<List<TriviaQuestion>>() {}.getType());
    }
}