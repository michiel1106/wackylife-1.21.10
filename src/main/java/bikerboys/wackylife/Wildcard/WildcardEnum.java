package bikerboys.wackylife.Wildcard;

import bikerboys.wackylife.Wildcard.wildcards.*;
import net.minecraft.util.*;

public enum WildcardEnum implements StringIdentifiable {
    WACKY_SKINS("wackyskins");


    String id;

    WildcardEnum(String id) {
        this.id = id;
    }

    public static Wildcard getWildcard(String id) {
        switch (id) {
            case "wackyskins" -> {
                return new WackySkins();
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
