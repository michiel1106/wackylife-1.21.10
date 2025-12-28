package bikerboys.wackylife.util;

import bikerboys.wackylife.entity.triviabot.trivia.*;

public class Trivia {
    public static Question currentQuestion = null;
    private static int TicksLeft;


    public static void setTicksLeft(int ticksLeft) {
        TicksLeft = ticksLeft;
    }

    public static int getTicksLeft() {
        return TicksLeft;
    }
}
