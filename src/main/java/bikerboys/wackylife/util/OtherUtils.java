package bikerboys.wackylife.util;

import net.minecraft.sound.*;
import net.minecraft.util.*;

import java.util.*;

public class OtherUtils {
    public static final Random rnd = new Random();

    public static SoundEvent getRandomSound(String name, int from, int to) {
        if (to > from) {
            int index = rnd.nextInt(from, to + 1);
            name += index;
        }
        return SoundEvent.of(Identifier.ofVanilla(name));
    }
}
