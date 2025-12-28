package bikerboys.wackylife.entity.triviabot;

import bikerboys.wackylife.util.*;
import net.minecraft.server.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;


public class TriviaBotSounds {
    private TriviaBot bot;
    public TriviaBotSounds(TriviaBot bot) {
        this.bot = bot;
    }

    private int introSoundCooldown = 0;
    private boolean playedCountdownSound = false;
    private boolean playedCountdownEndingSound = false;
    public boolean playWithoutSource = false;
    public void playSounds(MinecraftServer server) {
        if (introSoundCooldown > 0) introSoundCooldown--;

        if (introSoundCooldown == 0 && !bot.interactedWith()) {
            SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_intro"));
            if (playWithoutSource) {
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 1, 1);//TODO sound
            }
            else {
                PlayerUtils.playSoundWithSourceToPlayers(server.getPlayerManager().getPlayerList(), bot, sound, SoundCategory.NEUTRAL, 1, 1);
            }
            introSoundCooldown = 830;
        }

        if (!playedCountdownEndingSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime() /* && bot.triviaHandler.getRemainingTicks() <= 676*/) {
            if (playWithoutSource) {
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(),
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense_end")),
                        0.65f, 1);
            }
            else {
                PlayerUtils.playSoundWithSourceToPlayers(
                        server.getPlayerManager().getPlayerList(), bot,
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense_end")),
                        SoundCategory.NEUTRAL, 0.65f, 1);
            }
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()) {
            if (playWithoutSource) {
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(),
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense")),//TODO sound
                        0.65f, 1);
            }
            else {
                PlayerUtils.playSoundWithSourceToPlayers(
                        server.getPlayerManager().getPlayerList(), bot,
                        SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense")),
                        SoundCategory.NEUTRAL, 0.65f, 1);
            }
            playedCountdownSound = true;
        }
    }
}
