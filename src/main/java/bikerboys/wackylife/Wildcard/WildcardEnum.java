package bikerboys.wackylife.Wildcard;

import bikerboys.wackylife.Wildcard.wildcards.*;
import net.minecraft.util.*;

public enum WildcardEnum implements StringIdentifiable {
    WACKY_SKINS("wackyskins"),
    CHOICES("choices"),
    TRIVIA("trivia"),
    EMPTY("empty");


    String id;

    WildcardEnum(String id) {
        this.id = id;
    }

    public static Wildcard getWildcard(String id) {
        switch (id) {
            case "wackyskins" -> {
                return new WackySkins();
            }
            case "choices" -> {
                return new Choices();
            }
            case "trivia" -> {
                return new TriviaWildcard();
            }
            case "empty" -> {
                return new Empty();
            }
            default -> {
                return null;
            }

        }

    }

    @Override
    public String asString() {
        return id;
    }
}
