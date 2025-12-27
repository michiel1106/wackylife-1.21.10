package bikerboys.wackylife.entity.triviabot;

import bikerboys.wackylife.util.*;
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
    public void playSounds() {
        if (introSoundCooldown > 0) introSoundCooldown--;

        if (introSoundCooldown == 0 && !bot.interactedWith()) {
            SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_intro"));
            if (playWithoutSource) {
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 1, 1);//TODO sound
            }
            else {
                PlayerUtils.playSoundWithSourceToPlayers(PlayerUtils.getAllPlayers(), bot, sound, SoundSource.NEUTRAL, 1, 1);
            }
            introSoundCooldown = 830;
        }

        if (!playedCountdownEndingSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime() && bot.triviaHandler.getRemainingTicks() <= 676) {
            if (playWithoutSource) {
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(),
                        SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_suspense_end")),//TODO sound
                        0.65f, 1);
            }
            else {
                PlayerUtils.playSoundWithSourceToPlayers(
                        PlayerUtils.getAllPlayers(), bot,
                        SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_suspense_end")),
                        SoundSource.NEUTRAL, 0.65f, 1);
            }
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()) {
            if (playWithoutSource) {
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(),
                        SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_suspense")),//TODO sound
                        0.65f, 1);
            }
            else {
                PlayerUtils.playSoundWithSourceToPlayers(
                        PlayerUtils.getAllPlayers(), bot,
                        SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla("wildlife_trivia_suspense")),
                        SoundSource.NEUTRAL, 0.65f, 1);
            }
            playedCountdownSound = true;
        }
    }
}
