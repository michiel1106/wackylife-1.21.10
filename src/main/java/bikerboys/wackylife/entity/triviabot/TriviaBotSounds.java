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
    public int delay = 0;
    public void playSounds(MinecraftServer server) {
        if (delay > 0) {
            delay--;
            return;
        }

        if (introSoundCooldown > 0) introSoundCooldown--;

        if (introSoundCooldown == 0 && !bot.interactedWith()) {
            if (!bot.santaBot()) {
                SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_intro"));
                PlayerUtils.playSoundWithSourceToPlayers(server.getPlayerManager().getPlayerList(), bot, sound, SoundCategory.NEUTRAL, 1, 1);
                introSoundCooldown = 830;
            }
            else {
                SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("nicelife_santabot_intro"));
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 1, 1);
                introSoundCooldown = 624;
            }
        }


        if (!playedCountdownEndingSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()
                && ((!bot.santaBot() && bot.triviaHandler.getRemainingTicks() <= 676) || (bot.santaBot() && bot.triviaHandler.getRemainingTicks() <= 643))) {
            if (!bot.santaBot()) {
                SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense_end"));
                PlayerUtils.playSoundWithSourceToPlayers(server.getPlayerManager().getPlayerList(), bot, sound, SoundCategory.NEUTRAL, 0.65f, 1);
            }
            else {
                SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("nicelife_santabot_suspense_end"));
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
            }
            playedCountdownEndingSound = true;
            playedCountdownSound = true;
        }
        else if (!playedCountdownSound && bot.interactedWith() && !bot.submittedAnswer() && !bot.ranOutOfTime()) {
            if (!bot.santaBot()) {
                SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("wildlife_trivia_suspense"));
                PlayerUtils.playSoundWithSourceToPlayers(server.getPlayerManager().getPlayerList(), bot, sound, SoundCategory.NEUTRAL, 0.65f, 1);
            }
            else  {
                SoundEvent sound = SoundEvent.of(Identifier.ofVanilla("nicelife_santabot_suspense"));
                PlayerUtils.playSoundToPlayer(bot.serverData.getBoundPlayer(), sound, 0.65f, 1);
            }
            playedCountdownSound = true;
        }
    }
}
