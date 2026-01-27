package bikerboys.wackylife.Wildcard;

import bikerboys.wackylife.Wildcard.wildcards.*;
import net.minecraft.util.*;

public enum WildcardEnum implements StringIdentifiable {
    WACKY_SKINS("wackyskins"),
    CHOICES("choices"),
    TRIVIA("trivia"),
    SOULBOUND("soulbound"),
    SWAP("swap"),
    SNAILS("snails"),
    ANXIETY("anxiety"),
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
            case "soulbound" -> {
                return new SoulboundWildcard();
            }
            case "swap" -> {
                return new SwapWildcard();
            }
            case "snails" -> {
                return new SnailsWildCard();
            }
            case "anxiety" -> {
                return new AnxietyWildcard();
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
