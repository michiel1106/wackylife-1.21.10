package bikerboys.wackylife.util;

import bikerboys.wackylife.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.entity.player.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ScoreboardManager {
    public static ScoreboardManager INSTANCE = new ScoreboardManager();
    private final Map<String, String> playerTeamCache = new HashMap<>();

    public ScoreboardManager() {
        ServerTickEvents.START_SERVER_TICK.register(this::worldTick);

    }

    private void worldTick(MinecraftServer server) {
        if (Constants.paused) return;

        ServerScoreboard scoreboard = server.getScoreboard();
        createTeamsAndScoreboards(scoreboard);

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ScoreAccess scoreAccess = getScoreboard(player, server, Constants.LivesScoreboard);
            if (scoreAccess == null) continue;

            int score = scoreAccess.getScore();
            String teamName =
                    score >= 4 ? "dark_green" :
                            score == 3 ? "green" :
                                    score == 2 ? "yellow" :
                                            score == 1 ? "red" : "dead";

            // Only update the team if it’s different from last tick
            String cachedTeam = playerTeamCache.get(player.getName().getString());
            if (!teamName.equals(cachedTeam)) {
                Team team = scoreboard.getTeam(teamName);
                if (team != null) {
                    scoreboard.addScoreHolderToTeam(player.getNameForScoreboard(), team);
                    playerTeamCache.put(player.getName().getString(), teamName);
                }
            }
        }
    }

    public int getLives(PlayerEntity player, MinecraftServer server) {
        if (server != null) {
            ScoreAccess scoreboard = getScoreboard(player, server, Constants.LivesScoreboard);
            if (scoreboard != null) {
                return scoreboard.getScore();
            }
        }

        return -1;

    }

    public boolean isDead(ServerPlayerEntity player) {
        int lives = getLives(player, player.getEntityWorld().getServer());

        if (lives <= 0) {
            return true;
        }

        return false;

    }

    public void incrementLives(PlayerEntity player, MinecraftServer server) {
        if (server != null) {
            ScoreAccess scoreboard = getScoreboard(player, server, Constants.LivesScoreboard);
            if (scoreboard != null) {
                scoreboard.incrementScore();
            }
        }
    }

    public void setLives(PlayerEntity player, MinecraftServer server, int lives) {
        if (server != null) {
            ScoreAccess scoreBoard = getScoreboard(player, server, Constants.LivesScoreboard);
            if (scoreBoard != null) {
                scoreBoard.setScore(lives);
            }
        }

    }

    private void createTeamsAndScoreboards(ServerScoreboard scoreboard) {
        extracted(scoreboard, Constants.KillsScoreboard);
        extracted(scoreboard, Constants.SessionTime);
        extracted(scoreboard, Constants.LivesScoreboard);

        createTeamIfMissing(scoreboard, "dark_green", Formatting.DARK_GREEN);
        createTeamIfMissing(scoreboard, "green", Formatting.GREEN);
        createTeamIfMissing(scoreboard, "yellow", Formatting.YELLOW);
        createTeamIfMissing(scoreboard, "red", Formatting.RED);
        createTeamIfMissing(scoreboard, "dead", Formatting.GRAY);
    }

    public int getTime(MinecraftServer server) {
        ScoreAccess scoreboard = getScoreboard("time", server, Constants.SessionTime);
        if (scoreboard != null) {
            return scoreboard.getScore();
        }
        return -1;
    }

    public void decrementTime(MinecraftServer server) {
        ScoreAccess scoreboard = getScoreboard("time", server, Constants.SessionTime);
        if (scoreboard != null) {
            scoreboard.incrementScore(-1);
        }
    }

    private static void extracted(ServerScoreboard scoreboard, String killsScoreboard) {
        ScoreboardObjective nullableObjective = scoreboard.getNullableObjective(killsScoreboard);
        if (nullableObjective == null) {
            scoreboard.addObjective(killsScoreboard, ScoreboardCriterion.DUMMY, Text.of(killsScoreboard), ScoreboardCriterion.RenderType.INTEGER, false, null);
        }
    }

    private void createTeamIfMissing(ServerScoreboard scoreboard, String name, Formatting color) {
        if (scoreboard.getTeam(name) == null) {
            Team team = scoreboard.addTeam(name);
            team.setColor(color);
        }
    }

    @Nullable
    public ScoreAccess getScoreboard(PlayerEntity player, MinecraftServer server, String scoreboardName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(scoreboardName);
        if (objective == null) return null;

        for (ScoreboardEntry entry : scoreboard.getScoreboardEntries(objective)) {
            if (entry.owner().equals(player.getNameForScoreboard())) {

                return scoreboard.getOrCreateScore(player, objective);
            }
        }
        return null;
    }

    @Nullable
    public ScoreAccess getScoreboard(String player, MinecraftServer server, String scoreboardName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(scoreboardName);
        ScoreHolder scoreHolder = ScoreHolder.fromName(player);
        if (objective != null) {
            return scoreboard.getOrCreateScore(scoreHolder, objective);
        }
        return null;
    }

    public void incrementScoreboard(PlayerEntity player, MinecraftServer server, String scoreboardName) {
        ScoreAccess scoreboard = getScoreboard(player, server, scoreboardName);
        if (scoreboard != null) {
            scoreboard.incrementScore();
        }
    }

    public void decrementScoreboard(PlayerEntity player, MinecraftServer server, String scoreboardName) {
        ScoreAccess scoreboard = getScoreboard(player, server, scoreboardName);
        if (scoreboard != null) {
            if (scoreboard.getScore() > 0) {
                scoreboard.incrementScore(-1);
            }
        }
    }


}
